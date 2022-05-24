package cc.sleek.client.gui.cgui.lime.components;

import cc.sleek.client.gui.cgui.lime.Priority;
import cc.sleek.client.gui.cgui.lime.components.settings.BoolSetting;
import cc.sleek.client.gui.cgui.lime.components.settings.EnumSetting;
import cc.sleek.client.gui.cgui.lime.components.settings.SlideSetting;
import cc.sleek.client.module.Module;
import cc.sleek.client.property.impl.BooleanValue;
import cc.sleek.client.property.impl.EnumValue;
import cc.sleek.client.property.impl.NumberValue;
import cc.sleek.client.util.Animate;
import cc.sleek.client.util.Easing;
import cc.sleek.client.util.RenderUtils;
import cc.sleek.client.util.fonts.Fonts;
import net.minecraft.client.gui.GuiScreen;

import java.awt.Color;
import java.util.ArrayList;

public class FrameModule implements Priority {
    private final Module module;
    private final ArrayList<Component> components;

    private final FrameCategory owner;

    private final Animate moduleAnimation;

    private int x, y;
    private int offset;

    private boolean opened;

    public FrameModule(Module module, FrameCategory owner, int x, int y)
    {
        this.module = module;
        this.components = new ArrayList<>();
        this.owner = owner;
        this.moduleAnimation = new Animate();
        moduleAnimation.setMin(0).setMax(255).setReversed(!module.isToggled()).setEase(Easing.LINEAR);
        this.opened = false;

        this.x = x;
        this.y = y;

        if(!module.getValues().isEmpty())
        {
            module.getValues().forEach(setting ->
            {
                if(setting instanceof BooleanValue)
                {
                    this.components.add(new BoolSetting(0, 0, this, setting));
                }
                if(setting instanceof EnumValue)
                {
                    this.components.add(new EnumSetting(0, 0, this, setting));
                }
                if(setting instanceof NumberValue)
                {
                    this.components.add(new SlideSetting(0, 0, this, setting));
                }
            });
        }
    }

    public void drawScreen(int mouseX, int mouseY)
    {
        moduleAnimation.setReversed(!module.isToggled());
        moduleAnimation.setSpeed(1000).update();

        if(RenderUtils.hover(x, y, mouseX, mouseY, defaultWidth, moduleHeight) && hoveredColor) {
            GuiScreen.drawRect(x,y, x + defaultWidth, y + moduleHeight, darkerMainColor);
        }

        if(module.isToggled() || (moduleAnimation.isReversed() && moduleAnimation.getValue() != 0)) {
            GuiScreen.drawRect(x,y, x + defaultWidth, y + moduleHeight, RenderUtils.setAlpha(new Color(enabledColor), (int) moduleAnimation.getValue()).getRGB());
        }

        Fonts.productSans20.drawString(module.getName(), x+3, y + (moduleHeight / 2F - (Fonts.productSans20.getHeight() / 2F)), stringColor, true);

        int offset = 0;

        if(opened) {
            for (Component component : this.components) { // using for loop because continue isn't supported on foreach
                // wykt u fucking retard do return and its the same thing :brain:
                if(!component.getSetting().isVisible()) continue;

                component.setX(x);
                component.setY(y + moduleHeight + offset);

                component.drawScreen(mouseX, mouseY);

                offset += component.getOffset();
            }
        }

        this.setOffset(moduleHeight + offset);
    }

    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if(RenderUtils.hover(x, y, mouseX, mouseY, defaultWidth, moduleHeight) && RenderUtils.hover(owner.getX(), owner.getY(), mouseX, mouseY, defaultWidth, owner.getHeight()))
        {
            switch(mouseButton)
            {
                case 0:
                    module.toggle();
                    break;
                case 1:
                    opened = !opened;
                    break;
                case 2:
                    //TODO: Bind
                    break;
            }
            return true;
        }

        if(RenderUtils.hover(owner.getX(), owner.getY(), mouseX, mouseY, defaultWidth, owner.getHeight()) && opened) {
            for (Component component : this.components) {
                if(component.getSetting().isVisible() && component.mouseClicked(mouseX, mouseY, mouseButton))
                    return true;
            }
        }

        return false;
    }

    public int getOffset() {
        offset = 0;
        if(opened) {
            for (Component component : this.components) { // using for loop because continue isn't supported on foreach
                if(!component.getSetting().isVisible()) continue;

                offset += component.getOffset();
            }
        }

        this.setOffset(moduleHeight + offset);
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}