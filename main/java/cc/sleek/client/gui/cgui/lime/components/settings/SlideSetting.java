package cc.sleek.client.gui.cgui.lime.components.settings;

import cc.sleek.client.gui.cgui.lime.Priority;
import cc.sleek.client.gui.cgui.lime.components.Component;
import cc.sleek.client.gui.cgui.lime.components.FrameModule;
import cc.sleek.client.property.Value;
import cc.sleek.client.property.impl.NumberValue;
import cc.sleek.client.util.RenderUtils;
import cc.sleek.client.util.fonts.Fonts;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SlideSetting extends Component implements Priority {
    public SlideSetting(int x, int y, FrameModule owner, Value setting) {
        super(x, y, owner, setting);
    }

    private boolean drag;

    @Override
    public void initGui() {
        drag = false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {

        if(!Mouse.isButtonDown(0)) drag = false;

        NumberValue slide = (NumberValue) getSetting();
        double min = ((NumberValue<?>)slide).getMin().doubleValue();
        double max = ((NumberValue<?>)slide).getMax().doubleValue();
        double diff = Math.min(defaultWidth + 5, Math.max(0, mouseX - (this.x)));
        double renderWidth = defaultWidth * (((NumberValue<?>)slide).getValue().doubleValue() - min) / (max - min);
        Gui.drawRect(x, y, x + (int) renderWidth, y + getOffset(), darkerMainColor);

        if(drag)
        {
            if(diff == 0)
                slide.setValue(min);
            else
            {
                double newValue = roundToPlace((diff / defaultWidth) * (max - min) + min, 2);
                if(newValue <= max)
                    this.setValue(newValue);
            }
        }

        Fonts.productSans20.drawString(getSetting().getName() + ": " + roundToPlace(((NumberValue<?>) getSetting()).getValue().doubleValue(), 2), x + 5, y + (getOffset() / 2F - (Fonts.productSans20.getHeight() / 2F)), stringColor, true);
    }

    private double roundToPlace(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


    private float snapToStep(float value, float valueStep) {
        if (valueStep > 0.0F)
            value = valueStep * Math.round(value / valueStep);

        return value;
    }

    private int snapToStep(int value, int valueStep) {
        if (valueStep > 0.0F)
            value = valueStep * Math.round(value / valueStep);

        return value;
    }

    private void setValue(double value) {
        final NumberValue set = (NumberValue) getSetting();
        if (set.getValue() instanceof Double || set.getValue() instanceof Float) {
            set.setValue(MathHelper.clamp_double(snapToStep((float) value, set.getIncrement().floatValue()), set.getMin().floatValue(), set.getMax().floatValue()));
        } else {
            set.setValue(MathHelper.clamp_int(snapToStep((int) value, set.getIncrement().intValue()), set.getMin().intValue(), set.getMax().intValue()));
        }

    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        return drag = RenderUtils.hover(x, y, mouseX, mouseY, defaultWidth, getOffset()) && mouseButton == 0;
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