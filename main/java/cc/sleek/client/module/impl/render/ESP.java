package cc.sleek.client.module.impl.render;

import cc.sleek.client.event.impl.Render2DEvent;
import cc.sleek.client.module.Module;
import cc.sleek.client.module.api.Category;
import cc.sleek.client.module.api.ModuleInfo;
import cc.sleek.client.property.impl.BooleanValue;
import cc.sleek.client.property.impl.ColorValue;
import cc.sleek.client.property.impl.EnumValue;
import cc.sleek.client.property.impl.NumberValue;
import cc.sleek.client.util.RenderUtils;
import com.sun.org.apache.xpath.internal.operations.Bool;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * @author Kansio
 */
@ModuleInfo(name = "ESP", description = "Renders entities for better awareness.", category = Category.RENDER)
public class ESP extends Module {

    private final BooleanValue onlyPlayers = new BooleanValue("Only Players", true);
    private final BooleanValue invisibles = new BooleanValue("Invisibles", true);
    private final BooleanValue local = new BooleanValue("Local", true);
    private final BooleanValue renderName = new BooleanValue("Render Name", true);
    private final BooleanValue filled = new BooleanValue("Filled", true);

    private final BooleanValue lineESP = new BooleanValue("Line ESP", true);
    private final EnumValue<LinePosition> linePosition = new EnumValue<>("Line Position", LinePosition.values());

    private final EnumValue boxType = new EnumValue("Box Type", BoxMode.values());
    private final ColorValue espColor = new ColorValue("ESP Color", Color.blue.getRGB());

    private final NumberValue<Double> lineWidth = new NumberValue<Double>("Line Width", 1d, 0.5, 3d, 0.1);
    // Will work on this later

    public ESP() {
        register(lineESP, onlyPlayers, local, renderName, filled, boxType, linePosition, lineWidth, espColor);
    }

    @EventLink
    private final Listener<Render2DEvent> render2DEventListener = event -> {
        for (Entity entity : mc.theWorld.loadedEntityList) {

            //otherwise it'll be funni
            if (!RenderUtils.isInViewFrustrum(entity)) {
                continue;
            }

            if (!(entity instanceof EntityLivingBase)) {
                continue;
            }

            if (!invisibles.getValue() && entity.isInvisible()) {
                continue;
            }

            if (onlyPlayers.getValue() && !(entity instanceof EntityPlayer)) {
                continue;
            }

            if (local.getValue() && entity == mc.thePlayer && mc.gameSettings.thirdPersonView == 0) {
                continue;
            }

            float partialTicks = mc.timer.renderPartialTicks;
            double x = RenderUtils.interpolate(entity.posX, entity.lastTickPosX, partialTicks);
            double y = RenderUtils.interpolate(entity.posY, entity.lastTickPosY, partialTicks);
            double z = RenderUtils.interpolate(entity.posZ, entity.lastTickPosZ, partialTicks);

            double width = entity.width / 1.5;
            double height = entity.height + (entity.isSneaking() ? -0.3 : 0.2);

            AxisAlignedBB bb = new AxisAlignedBB(x - width, y, z - width, x + width, y + height + 0.05, z + width);

            List<Vector3d> vectorList = Arrays.asList(
                    new Vector3d(bb.minX, bb.minY, bb.minZ),
                    new Vector3d(bb.minX, bb.maxY, bb.minZ),
                    new Vector3d(bb.maxX, bb.minY, bb.minZ),
                    new Vector3d(bb.maxX, bb.maxY, bb.minZ),
                    new Vector3d(bb.minX, bb.minY, bb.maxZ),
                    new Vector3d(bb.minX, bb.maxY, bb.maxZ),
                    new Vector3d(bb.maxX, bb.minY, bb.maxZ),
                    new Vector3d(bb.maxX, bb.maxY, bb.maxZ)
            );

            mc.entityRenderer.setupCameraTransform(partialTicks, 0);

            Vector4d position = null;

            for (Vector3d vector : vectorList) {
                vector = RenderUtils.project(vector.x - mc.getRenderManager().viewerPosX, vector.y - mc.getRenderManager().viewerPosY, vector.z - mc.getRenderManager().viewerPosZ);
                if (vector != null && vector.z >= 0.0 && vector.z < 1.0) {
                    if (position == null) {
                        position = new Vector4d(vector.x, vector.y, vector.z, 0.0);
                    }
                    position.x = Math.min(vector.x, position.x);
                    position.y = Math.min(vector.y, position.y);
                    position.z = Math.max(vector.x, position.z);
                    position.w = Math.max(vector.y, position.w);
                }
            }

            mc.entityRenderer.setupOverlayRendering();

            if (position == null) {
                continue;
            }

            GL11.glPushMatrix();

            float boxX = (float) position.x;
            float boxWidth = (float) (position.z - boxX);
            float boxY = (float) (position.y + 3);
            float boxHeight = (float) (position.w - boxY);

            if (renderName.getValue()) {

            }

            //line esp aka tracers
            if (lineESP.getValue() && entity != mc.thePlayer) {
                double yPosition;

                switch (linePosition.getValue()) {
                    case TOP:
                        yPosition = 0;
                        break;
                    case BOTTOM:
                        yPosition = RenderUtils.getResolution().getScaledHeight();
                        break;
                    default:
                        yPosition = RenderUtils.getResolution().getScaledHeight() / 2;
                        break;
                }

                RenderUtils.drawLine(RenderUtils.getResolution().getScaledWidth() / 2, yPosition, (int) (position.x + position.z) / 2, (int) position.y, new Color(espColor.getValue()));
            }

            RenderUtils.drawCornerBoxA(position.x, position.y, position.z, position.w, lineWidth.getValue(), new Color(espColor.getValue()));
            GL11.glPopMatrix();
        }
    };

    private enum BoxMode {
        A, B, C, D,
    }

    private enum LinePosition {
        TOP, BOTTOM, CENTER
    }
}
