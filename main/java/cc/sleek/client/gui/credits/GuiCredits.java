package cc.sleek.client.gui.credits;

import cc.sleek.client.util.RenderUtils;
import cc.sleek.client.util.glsl.GLSLSandboxShader;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.util.Arrays;
import java.util.Comparator;

public class GuiCredits extends GuiScreen {

    private final String[] devs = {
            "Cade",
            "Divine",
            "Kobley",
            "Kansio",
            "nullswap",
            "qoft",
            "Reset",
            "Spectre",
            "Snowyy",
            "Error"
    };
    private final String[] peopleWhoGaveMeShit = {
            "Vince (some scaffold things)",
            "Dort (couple bypasses)",
            "Wykt (clickgui)",
            "HTB (clickgui) (dont know the devs)",
            "haiku (funny verus hake speed)",
            "zane (eventbus)"
    };
    private final String[] testers = {
            "Aether",
            "boeing747",
            "Dar",
            "days",
            "LucaForever",
            "MinecraftHaxor",
            "noq",
            "Wenenu",
            "Anime",
            "Asterous",
            "bricklover",
            "Dogoo",
            "life",
            "Percs",
            "xences",
            "zedpan",
            "xman",
            "aww",
    };
    private GLSLSandboxShader backgroundShader;
    private long initTime = System.currentTimeMillis();

    public GuiCredits() {
        try {
            this.backgroundShader = new GLSLSandboxShader("/background.fsh");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.enableAlpha();
        GlStateManager.disableCull();
        this.backgroundShader.useShader(width, height, mouseX, mouseY, (System.currentTimeMillis() - initTime) / 1000f);

        GL11.glBegin(GL11.GL_QUADS);

        GL11.glVertex2f(-1f, -1f);
        GL11.glVertex2f(-1f, 1f);
        GL11.glVertex2f(1f, 1f);
        GL11.glVertex2f(1f, -1f);

        GL11.glEnd();

        // Unbind shader
        GL20.glUseProgram(0);
        RenderUtils.drawRect(0, 0, this.width, this.height, 0xFF000000);
        mc.fontRendererObj.drawStringWithShadow("Developed by:" + Arrays.toString(devs).replace('[', ' ').replace(']', ' '), 0, height - 10, -1);
        drawString(fontRendererObj, "Press escape to go back (tell divine to fix later)", this.width - fontRendererObj.getStringWidth("Press escape to go back (tell divine to fix later)"), this.height - 10, -1);
        Arrays.sort(devs, Comparator.comparingInt((dev) -> mc.fontRendererObj.getStringWidth((String) dev)).reversed());
        Arrays.sort(testers, Comparator.comparingInt((dev) -> mc.fontRendererObj.getStringWidth((String) dev)).reversed());
        Arrays.sort(peopleWhoGaveMeShit, Comparator.comparingInt((dev) -> mc.fontRendererObj.getStringWidth((String) dev)).reversed());
        int c = fontRendererObj.FONT_HEIGHT;
        drawString(fontRendererObj, "People who gave me things:", 0, 0, 0xFF00FFAA);
        for (String dev : peopleWhoGaveMeShit) {
            drawString(fontRendererObj, dev, 0, c, 0xFF00FFAA);
            c += fontRendererObj.FONT_HEIGHT;
        }
        c = fontRendererObj.FONT_HEIGHT;
        drawString(fontRendererObj, "Beta testers:", this.width - fontRendererObj.getStringWidth("Beta testers:") - 13, 0, 0xFFFFAAFF);
        for (String dev : testers) {
            drawString(fontRendererObj, dev, this.width - fontRendererObj.getStringWidth(dev), c, 0xFFFFAAFF);
            c += fontRendererObj.FONT_HEIGHT;
        }

    }
}
