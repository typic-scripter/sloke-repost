package cc.sleek.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import javax.vecmath.Vector3d;
import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static net.minecraft.client.renderer.GlStateManager.disableBlend;
import static net.minecraft.client.renderer.GlStateManager.enableTexture2D;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author Divine
 *
 * Mashup of utils found in old sleek base and generally easy things
 */
public class RenderUtils implements IUtil {

    public static void drawRectWithBorder(double x, double y, double width, double height, double lineSize, int borderColor, int color) {
        Gui.drawRect(x, y, x + width, y + height, color);
        Gui.drawRect(x, y, x + width, y + lineSize, borderColor);
        Gui.drawRect(x, y, x + lineSize, y + height, borderColor);
        Gui.drawRect(x + width, y, x + width - lineSize, y + height, borderColor);
        Gui.drawRect(x, y + height, x + width, y + height - lineSize, borderColor);
    }

    // integers because its in pixels and how can u have a fraction of a pixel
    public static void drawRect(int x, int y, int width, int height, int color) {
        Gui.drawRect(x, y, x + width, y + height, color);
    }

    // i did not make this
    public static boolean hover(int x, int y, int mouseX, int mouseY, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    // moshi explained this so i can understand it
    public static double interpolate(double current, double old, double scale) {
        return old + (current - old) * scale;
    }

    public static void rectangleOutlinedGradient(double x1, double y1, double x2, double y2, int[] color,
                                                 double width) {
        rectangleGradient(x1, y1, x2, y1 + width, color);
        rectangleGradient(x1, y2 - width, x2, y2, color);
        rectangleGradient(x1, y1 + width, x1 + width, y2 - width, color);
        rectangleGradient(x2 - width, y1 + width, x2, y2 - width, color);
    }

    // Found this from FDP. Could have some potential. Makes tracers like a million times easier
    public static void connectPoints(float xOne, float yOne, float xTwo, float yTwo) {
        glPushMatrix();
        glEnable(GL_LINE_SMOOTH);
        glColor4f(1.0F, 1.0F, 1.0F, 0.8F);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);
        glLineWidth(0.5F);
        glBegin(GL_LINES);
        glVertex2f(xOne, yOne);
        glVertex2f(xTwo, yTwo);
        glEnd();
        glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        glDisable(GL_LINE_SMOOTH);
        glEnable(GL_TEXTURE_2D);
        glPopMatrix();
    }



    public static void rectangleGradient(double x1, double y1, double x2, double y2, int[] color) {
        float[] r = new float[color.length];
        float[] g = new float[color.length];
        float[] b = new float[color.length];
        float[] a = new float[color.length];
        for (int i = 0; i < color.length; i++) {
            r[i] = ((color[i] >> 16 & 0xFF) / 255.0F);
            g[i] = ((color[i] >> 8 & 0xFF) / 255.0F);
            b[i] = ((color[i] & 0xFF) / 0.0F);
            a[i] = ((color[i] >> 24 & 0xFF) / 255.0F);
        }
        GlStateManager.disableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.disableAlpha();
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GlStateManager.blendFunc(770, 771);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        GL11.glBegin(11);
        if (color.length == 1) {
            GL11.glVertex3d(x2, y1, 0.0D);
            GL11.glVertex3d(x1, y1, 0.0D);
            GL11.glVertex3d(x1, y2, 0.0D);
            GL11.glVertex3d(x2, y2, 0.0D);
        } else if ((color.length == 2) || (color.length == 3)) {
            GL11.glVertex3d(x2, y1, 0.0D);
            GL11.glVertex3d(x1, y1, 0.0D);
            GL11.glVertex3d(x1, y2, 0.0D);
            GL11.glVertex3d(x2, y2, 0.0D);
        } else if (color.length >= 4) {
            GL11.glVertex3d(x2, y1, 0.0D);
            GL11.glVertex3d(x1, y1, 0.0D);
            GL11.glVertex3d(x1, y2, 0.0D);
            GL11.glVertex3d(x2, y2, 0.0D);
        }
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }


    public static void drawRect(double x, double y, double x2, double y2, int color) {
        Gui.drawRect(x, y, x2, y2, color); // why in the FUCK is this here
    }

    public static void drawBorderedRect(double x, double y, double x2, double y2, double thickness, int inside,
                                        int outline) {
        double fix = 0.0;
        if (thickness < 1.0) {
            fix = 1.0;
        }
        drawRect(x + thickness, y + thickness, x2 - thickness, y2 - thickness, inside);
        drawRect(x, y + 1.0 - fix, x + thickness, y2, outline);
        drawRect(x, y, x2 - 1.0 + fix, y + thickness, outline);
        drawRect(x2 - thickness, y, x2, y2 - 1.0 + fix, outline);
        drawRect(x + 1.0 - fix, y2 - thickness, x2, y2, outline);
    }

    public static void enableGL2D() {
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glDepthMask(true);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
    }

    public static void disableGL2D() {
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }

    private static final Frustum frustrum;
    private static final FloatBuffer modelViewMatrix;
    private static final FloatBuffer projectionMatrix;
    private static final IntBuffer viewport;

    public static ScaledResolution getResolution() {
        return new ScaledResolution(Minecraft.getMinecraft());
    }

    public static Vector3d project(double x, double y, double z) {
        FloatBuffer vector = GLAllocation.createDirectFloatBuffer(4);
        GL11.glGetFloat(2982, modelViewMatrix);
        GL11.glGetFloat(2983, projectionMatrix);
        GL11.glGetInteger(2978, viewport);
        if (GLU.gluProject((float) x, (float) y, (float) z, modelViewMatrix, projectionMatrix, viewport, vector)) {
            return new Vector3d(vector.get(0) / getResolution().getScaleFactor(),
                    (Display.getHeight() - vector.get(1)) / getResolution().getScaleFactor(),
                    vector.get(2));
        }
        return null;
    }

    public static boolean isInViewFrustrum(Entity entity) {
        return isInViewFrustrum(entity.getEntityBoundingBox()) || entity.ignoreFrustumCheck;
    }

    public static boolean isInViewFrustrum(AxisAlignedBB bb) {
        Entity current = Minecraft.getMinecraft().getRenderViewEntity();
        frustrum.setPosition(current.posX, current.posY, current.posZ);
        return frustrum.isBoundingBoxInFrustum(bb);
    }

    static {
        frustrum = new Frustum();
        modelViewMatrix = GLAllocation.createDirectFloatBuffer(16);
        projectionMatrix = GLAllocation.createDirectFloatBuffer(16);
        viewport = GLAllocation.createDirectIntBuffer(16);
    }

    public static void start2D() {
        glEnable(3042);
        glDisable(3553);
        glBlendFunc(770, 771);
        glEnable(2848);
    }
    public static void stop2D() {
        glEnable(3553);
        glDisable(3042);
        glDisable(2848);
        enableTexture2D();
        disableBlend();
        glColor4f(1, 1, 1, 1);
    }
    public static void setColor(Color color) {
        float alpha = (color.getRGB() >> 24 & 0xFF) / 255.0F;
        float red = (color.getRGB() >> 16 & 0xFF) / 255.0F;
        float green = (color.getRGB() >> 8 & 0xFF) / 255.0F;
        float blue = (color.getRGB() & 0xFF) / 255.0F;
        GL11.glColor4f(red, green, blue, alpha);
    }
    //pasted utils end

    //draw a 2d line using opengl
    public static void drawLine(double x1, double y1, double x2, double y2, Color color) {
        start2D();
        setColor(color);
        glBegin(GL_LINES);
        glVertex2d(x1, y1);
        glVertex2d(x2, y2);
        glEnd();
        stop2D();
    }

    public static void drawScaledCustomSizeModalRect(int x, int y, float u, float v, int uWidth, int vHeight, int width, int height, float tileWidth, float tileHeight) {
        float f = 1.0F / tileWidth;
        float f1 = 1.0F / tileHeight;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x, y + height, 0.0D).tex(u * f, (v + (float) vHeight) * f1).endVertex();
        worldrenderer.pos(x + width, y + height, 0.0D).tex((u + (float) uWidth) * f, (v + (float) vHeight) * f1).endVertex();
        worldrenderer.pos(x + width, y, 0.0D).tex((u + (float) uWidth) * f, v * f1).endVertex();
        worldrenderer.pos(x, y, 0.0D).tex(u * f, v * f1).endVertex();
        tessellator.draw();
    }

    public static void drawFace(ResourceLocation skin, int x, int y, int width, int height) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
        RenderUtils.drawScaledCustomSizeModalRect(x, y, 8F, 8F, 8, 8, width, height,
                64F, 64F);
        RenderUtils.drawScaledCustomSizeModalRect(x, y, 40F, 8F, 8, 8, width, height,
                64F, 64F);
    }


    public static void drawCornerBoxA(double x, double y, double x2, double y2, double lw, Color color) {
        double width = Math.abs(x2 - x);
        double height = Math.abs(y2 - y);
        double halfWidth = width / 4;
        double halfHeight = height / 4;
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        start2D();
        GL11.glPushMatrix();
        GL11.glLineWidth((float) lw);
        setColor(color);

        GL11.glBegin(GL_LINE_STRIP);
        GL11.glVertex2d(x + halfWidth, y);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x, y + halfHeight);
        GL11.glEnd();


        GL11.glBegin(GL_LINE_STRIP);
        GL11.glVertex2d(x, y + height - halfHeight);
        GL11.glVertex2d(x, y + height);
        GL11.glVertex2d(x + halfWidth, y + height);
        GL11.glEnd();

        GL11.glBegin(GL_LINE_STRIP);
        GL11.glVertex2d(x + width - halfWidth, y + height);
        GL11.glVertex2d(x + width, y + height);
        GL11.glVertex2d(x + width, y + height - halfHeight);
        GL11.glEnd();

        GL11.glBegin(GL_LINE_STRIP);
        GL11.glVertex2d(x + width, y + halfHeight);
        GL11.glVertex2d(x + width, y);
        GL11.glVertex2d(x + width - halfWidth, y);
        GL11.glEnd();

        GL11.glPopMatrix();
        stop2D();
    }

    // from wykt cock guice
    public static void prepareScissorBox(float x2, float y2, float x22, float y22) {
        ScaledResolution scale = new ScaledResolution(mc);
        int factor = scale.getScaleFactor();
        GL11.glScissor((int)(x2 * (float)factor), (int)(((float)scale.getScaledHeight() - y22) * (float)factor), (int)((x22 - x2) * (float)factor), (int)((y22 - y2) * (float)factor));
    }

    // From LiquidBounce
    public static void drawFilledCircle(final float xx, final float yy, final float radius, final Color color) {
        int sections = 50;
        double dAngle = 2 * Math.PI / sections;
        float x, y;

        glPushAttrib(GL_ENABLE_BIT);

        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glBegin(GL_TRIANGLE_FAN);

        for (int i = 0; i < sections; i++) {
            x = (float) (radius * Math.sin((i * dAngle)));
            y = (float) (radius * Math.cos((i * dAngle)));

            glColor(color);
            glVertex2f(xx + x, yy + y);
        }

        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        glEnd();

        glPopAttrib();
    }


    // fucking hell writing these comments are hard
    public static Color setAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    // from wykt's github
    public static void glColor(Color color) {
        GlStateManager.color((float) color.getRed() / 255F, (float) color.getGreen() / 255F, (float) color.getBlue() / 255F, (float) color.getAlpha() / 255F);
    }



}