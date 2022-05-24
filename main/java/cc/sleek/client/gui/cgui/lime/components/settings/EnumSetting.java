package cc.sleek.client.gui.cgui.lime.components.settings;

import cc.sleek.client.gui.cgui.lime.Priority;
import cc.sleek.client.gui.cgui.lime.components.Component;
import cc.sleek.client.gui.cgui.lime.components.FrameModule;
import cc.sleek.client.property.Value;
import cc.sleek.client.property.impl.EnumValue;
import cc.sleek.client.util.RenderUtils;
import cc.sleek.client.util.fonts.Fonts;

public class EnumSetting extends Component implements Priority {
    public EnumSetting(int x, int y, FrameModule owner, Value setting) {
        super(x, y, owner, setting);
    }

    @Override
    public void initGui() {

    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        Fonts.productSans20.drawString(getSetting().getName(), x + 5, y + (getOffset() / 2F - (Fonts.productSans20.getHeight() / 2F)), -1, true);
        Fonts.productSans20.drawString(((EnumValue<?>) getSetting()).getValue().name().toUpperCase(), x + defaultWidth - Fonts.productSans20.getStringWidth(((EnumValue<?>) getSetting()).getValue().name().toUpperCase()) - 5, y + (getOffset() / 2F - (Fonts.productSans20.getHeight() / 2F)), -1, true);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(RenderUtils.hover(x, y, mouseX, mouseY, defaultWidth, getOffset()))
        {
            EnumValue enumValue = (EnumValue) getSetting();

            int enumIndex = 0;
            for(Enum _enum : enumValue.getChoices()) {
                if(_enum == enumValue.getValue()) break;
                ++enumIndex;
            }

            if(mouseButton == 1) {
                if(enumIndex - 1 >= 0) {
                    enumValue.setValue(enumValue.getChoices()[enumIndex - 1]);
                } else {
                    enumValue.setValue(enumValue.getChoices()[enumValue.getChoices().length - 1]);
                }
            }

            if(mouseButton == 0) {
                if(enumIndex + 1 < enumValue.getChoices().length) {
                    enumValue.setValue(enumValue.getChoices()[enumIndex + 1]);
                } else {
                    enumValue.setValue(enumValue.getChoices()[0]);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void onGuiClosed(int mouseX, int mouseY, int mouseButton) {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }

    @Override
    public int getOffset() {
        return 15;
    }
}