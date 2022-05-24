package cc.sleek.client.gui.cgui.lime.components.settings;

import cc.sleek.client.gui.cgui.lime.Priority;
import cc.sleek.client.gui.cgui.lime.components.FrameModule;
import cc.sleek.client.property.Value;
import cc.sleek.client.property.impl.BooleanValue;
import cc.sleek.client.util.Animate;
import cc.sleek.client.util.Easing;
import cc.sleek.client.util.RenderUtils;
import cc.sleek.client.util.fonts.Fonts;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import cc.sleek.client.gui.cgui.lime.components.Component;
import java.awt.*;

public class BoolSetting extends Component implements Priority {
    private final Animate animation;

    public BoolSetting(int x, int y, FrameModule owner, Value setting)
    {
        super(x, y, owner, setting);
        this.animation = new Animate().setMin(0).setMax(5).setSpeed(150).setEase(Easing.LINEAR).setReversed(!((BooleanValue) setting).getValue());
    }

    @Override
    public void initGui()
    {

    }

    @Override
    public void drawScreen(int mouseX, int mouseY)
    {
        animation.update();
        Fonts.productSans20.drawString(getSetting().getName(), x + 5, y + (getOffset() / 2F - (Fonts.productSans20.getHeight() / 2F)), -1, true);
        //Gui.drawRect(x + defaultWidth - 15, y, x + defaultWidth - 5, y + 10, darkerMainColor);
        RenderUtils.drawFilledCircle(x + defaultWidth - 10,  (y + (getOffset() / 2F - (Fonts.productSans20.getHeight() / 2F)) + 6.75f), 5, new Color(darkerMainColor));

        if(((BooleanValue) getSetting()).getValue() || animation.getValue() != 0)
        {
            RenderUtils.drawFilledCircle(x + defaultWidth - 10,  (y + (getOffset() / 2F - (Fonts.productSans20.getHeight() / 2F)) + 6.75f), animation.getValue(), new Color(enabledColor));
            GlStateManager.resetColor();
            GL11.glColor4f(1, 1, 1, 1);
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if(RenderUtils.hover(x, y, mouseX, mouseY, defaultWidth, getOffset())) {
            BooleanValue set = (BooleanValue) getSetting();
            set.setValue(!set.getValue());
            animation.setReversed(!set.getValue());
            return true;
        }
        return false;
    }

    @Override
    public void onGuiClosed(int mouseX, int mouseY, int mouseButton)
    {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {

    }

    @Override
    public int getOffset()
    {
        return 15;
    }
}