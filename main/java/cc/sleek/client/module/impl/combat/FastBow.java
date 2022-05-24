package cc.sleek.client.module.impl.combat;

import cc.sleek.client.event.impl.UpdateEvent;
import cc.sleek.client.module.Module;
import cc.sleek.client.module.api.Category;
import cc.sleek.client.module.api.ModuleInfo;
import cc.sleek.client.property.impl.BooleanValue;
import cc.sleek.client.property.impl.NumberValue;
import cc.sleek.client.util.IPacketUtil;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.lwjgl.input.Mouse;

@ModuleInfo(
        name = "FastBow",
        description = "Turns you into a school shooter",
        category = Category.COMBAT
)
public class FastBow extends Module {

    private final NumberValue<Integer> packets = new NumberValue<>("Packets", 20, 1, 20, 1);
    private final BooleanValue aimbot = new BooleanValue("Aimbot", false);

    public FastBow() {
        register(packets, aimbot);
    }

    @EventLink
    Listener<UpdateEvent> updateEventListener = event -> {
        if (event.isPre()) {
            if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemBow && mc.thePlayer.onGround) {
                if (Mouse.isButtonDown(1)) {
                    mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());

//                    double nig = 1E-64;
                    for (int i = 0; i < packets.getValue(); i++) {
                        double x = mc.thePlayer.posX;
                        double y = mc.thePlayer.posY;
                        double z = mc.thePlayer.posZ;
                        float yaw = mc.thePlayer.rotationYaw;
                        float pitch = mc.thePlayer.rotationPitch;
                        IPacketUtil.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(x, y, z, yaw, pitch, true));
                    }

                    mc.playerController.onStoppedUsingItem(mc.thePlayer);
                }
            }
        }
    };

    /**
     * Attempts to get rotations to aim a perfect bow shot for this {@code Entity}
     *
     * @param entity - The {@code Entity} to get rotations
     * @return The predicted rotations for this entity
     * <p>
     */
    // i no make this method
    public float[] getRotations(Entity entity) {
        double xDelta = entity.posX - entity.lastTickPosX;
        double zDelta = entity.posZ - entity.lastTickPosZ;
        double distance = mc.thePlayer.getDistanceToEntity(entity) % .8;
        boolean sprint = entity.isSprinting();
        double xMulti = distance / .8 * xDelta * (sprint ? 1.45 : 1.3);
        double zMulti = distance / .8 * zDelta * (sprint ? 1.45 : 1.3);
        double x = entity.posX + xMulti - mc.thePlayer.posX;
        double y = mc.thePlayer.posY + mc.thePlayer.getEyeHeight()
                - (entity.posY + entity.getEyeHeight());
        double z = entity.posZ + zMulti - mc.thePlayer.posZ;
        double distanceToEntity = mc.thePlayer.getDistanceToEntity(entity);
        float yaw = (float) Math.toDegrees(Math.atan2(z, x)) - 90;
        float pitch = (float) Math.toDegrees(Math.atan2(y, distanceToEntity));
        return new float[]{yaw, pitch};
    }

}
