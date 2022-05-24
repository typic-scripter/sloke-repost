package cc.sleek.client.gui.cgui.htb.frame;

import cc.sleek.client.Sleek;
import cc.sleek.client.module.Module;
import cc.sleek.client.module.api.Category;
import cc.sleek.client.property.Value;
import cc.sleek.client.property.impl.BooleanValue;
import cc.sleek.client.property.impl.ColorValue;
import cc.sleek.client.property.impl.EnumValue;
import cc.sleek.client.property.impl.NumberValue;
import cc.sleek.client.util.*;
import cc.sleek.client.util.fonts.Fonts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public abstract class Frame implements Labeled {

    private final String label;
    private int posX, posY, lastPosX, lastPosY, width, height;
    private boolean dragging, extended, slidedragging, enumextended, binding, colorextended, colorsliding;
    private Category selectedCategory = Category.COMBAT;
    private Module extendedmod, bindingmod;
    private EnumValue selectedenum;
    private ColorValue selectedcolor;
    private int modoffset, valueoffset;
    private final Stopwatch timer = new Stopwatch();

    public Frame(String label, int posX, int posY, int width, int height) {
        this.label = label;
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.lastPosX = posX;
        this.lastPosY = posY;
    }

    public abstract void init();

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GL11.glColor4f(1, 1, 1, 1);
        if (dragging) {
            this.posX = mouseX + this.lastPosX;
            this.posY = mouseY + this.lastPosY;
        }
        if (posY + 32.5f + (Sleek.INSTANCE.getModuleManager().getModulesInCategory(selectedCategory).size() * (Fonts.clickfont.getHeight() * 6)) >= posY + height + 2.5) {
            int wheel = Mouse.getDWheel();
            if (MouseUtil.mouseWithinBounds(mouseX, mouseY, posX + 2.5f, posY + 10.5f,
                    extended ? posX + (float) width / 2.0F : posX + width - 2.5f, posY + height - 2.5f)) {
                if (Mouse.hasWheel()) {
                    if (wheel < 0 && posY + modoffset
                            + (Sleek.INSTANCE.getModuleManager().getModulesInCategory(selectedCategory).size()
                            * (Fonts.clickfont.getHeight() * 6)) > posY + 16) {
                        modoffset -= 4;
                    } else if (wheel > 0) {
                        modoffset += 4;
                        if (posY + height - 7.5 + modoffset >= posY + height - 7.5) {
                            modoffset = 0;
                        }
                    }
                }
            } else {
                wheel = 0;
            }
        }
        if (extended && extendedmod != null) {
            if ((posY + 32.5f + Fonts.clickfont.getHeight() * 6)
                    + (extendedmod.getValues().size() * (Fonts.clickfont.getHeight() * 6)) >= posY + height + 12.5) {
                int wheel = Mouse.getDWheel();
                if (MouseUtil.mouseWithinBounds(mouseX, mouseY, posX + (width / 2) + 0.5f, posY + 10.5f,
                        posX + width - 2.5f, posY + height - 2.5f)) {
                    if (Mouse.hasWheel()) {
                        if (wheel < 0 && posY + valueoffset
                                + (extendedmod.getValues().size() * (Fonts.clickfont.getHeight() * 6)) > posY + 1) {
                            valueoffset -= 4;
                        } else if (wheel > 0) {
                            valueoffset += 4;
                            if (posY + height - 7.5 + valueoffset >= posY + height - 7.5) {
                                valueoffset = 0;
                            }
                        }
                    }
                } else {
                    wheel = 0;
                }
            }
        }
        GL11.glColor4f(1, 1, 1, 1);
        RenderUtils.drawBorderedRect(posX, posY, posX + width, posY + height, 1, new Color(30, 30, 30, 255).getRGB(),
                new Color(0, 0, 0, 255).getRGB());
        RenderUtils.drawBorderedRect(posX + 2.5, posY + 10.5, posX + width - 2.5, posY + height - 2.5, 1,
                new Color(38, 38, 38, 255).getRGB(), new Color(0, 0, 0, 255).getRGB());
        Fonts.clickfont.drawStringWithShadow(label, posX + 4, posY + 4.5f, new Color(255, 255, 255, 255).getRGB());
        float x = 0;
        for (Category category : Category.values()) {
            RenderUtils
                    .drawBorderedRect(posX + 2.5 + x, posY + 10.5,
                            posX + 2.5 + x + ((width - 5) / Category.values().length), posY + 23, 0.5,
                            MouseUtil.mouseWithinBounds(mouseX, mouseY, posX + 2.5f + x, posY + 10.5f,
                                    posX + 2.5f + x + ((width - 5) / Category.values().length), posY + 23)
                                    ? new Color(55, 55, 55, 255).getRGB()
                                    : (selectedCategory == category ? new Color(50, 50, 50, 255).getRGB()
                                    : new Color(38, 38, 38, 255).getRGB()),
                            new Color(0, 0, 0, 255).getRGB());
            Fonts.clickfont.drawStringWithShadow(category.name().toLowerCase(),
                    posX + 2.5 + x + (((width - 5) / Category.values().length) / 2)
                            - Fonts.clickfont.getStringWidth(category.name().toLowerCase()) / 2,
                    posY + 15.5, -1);
            x += (width - 5) / Category.values().length;
        }
        int addition = 0;
        float modY = posY + 32.5f;
        for (Module mod : Sleek.INSTANCE.getModuleManager().getModules()) {
            if (mod.getCategory() == selectedCategory) {
                if (!(modY + modoffset > posY + height - 7.5 || modY + modoffset < posY + 26.5f)) {
                    Fonts.clickfont.drawStringWithShadow(
                            (bindingmod == mod && binding) ? "Press any key to bind"
                                    : mod.getName().toLowerCase() + ((extended && extendedmod == mod) ? " -"
                                    : (mod.getValues().isEmpty() ? "" : " +")),
                            posX + 7.5, modY + modoffset, new Color(255, 255, 255, 255).getRGB());
                    RenderUtils.drawBorderedRect(posX + width - ((extended && extendedmod != null) ? 148 : 28),
                            modY - 3 + modoffset, posX + width - ((extended && extendedmod != null) ? 128 : 8),
                            modY + 3 + modoffset, 0.5, new Color(55, 55, 55, 255).getRGB(),
                            new Color(2, 2, 2, 255).getRGB());
                    if (mod.isToggled()) {
                        Gui.drawRect(posX + width - ((extended && extendedmod != null) ? 137.5 : 17.5),
                                modY - 2.5 + modoffset,
                                posX + width - ((extended && extendedmod != null) ? 128.5 : 8.5),
                                modY + 2.5 + modoffset, new Color(43, 203, 115, 255).getRGB());
                    } else {
                        Gui.drawRect(posX + width - ((extended && extendedmod != null) ? 147.5 : 27.5),
                                modY - 2.5 + modoffset, posX + width - ((extended && extendedmod != null) ? 138 : 18),
                                modY + 2.5 + modoffset, new Color(165, 38, 38, 255).getRGB());
                    }
                }
                modY += Fonts.clickfont.getHeight() * 6;
            }
        }
        if (extended && extendedmod != null) {
            Gui.drawRect((posX - 0.25) + width / 2, posY + 23, (posX + 0.25) + width / 2, posY + height - 2.5,
                    new Color(0, 0, 0, 255).getRGB());
            if (posY + 32.5f + valueoffset > posY + 24.5f)
                Fonts.clickfont.drawStringWithShadow(extendedmod.getName().toLowerCase() + "'s values",
                        posX + width / 2 + (width / 4)
                                - Fonts.clickfont.getStringWidth(extendedmod.getName().toLowerCase() + "'s values") / 2,
                        posY + 32.5f + valueoffset, new Color(255, 255, 255, 255).getRGB());
            float valY = posY + 32.5f + Fonts.clickfont.getHeight() * 6;
            for (Value value : extendedmod.getValues()) {
                if (!value.isVisible()) continue;
                if (value instanceof BooleanValue) {
                    if (!(valY + valueoffset < posY + 24.5f || valY + valueoffset > posY + height - 7.5)) {
                        Fonts.clickfont.drawStringWithShadow(value.getName().toLowerCase(), posX + 5 + width / 2,
                                valY + valueoffset, new Color(255, 255, 255, 255).getRGB());
                        RenderUtils.drawBorderedRect(posX + width - 28, valY - 3 + valueoffset, posX + width - 8,
                                valY + 3 + valueoffset, 0.5, new Color(55, 55, 55, 255).getRGB(),
                                new Color(2, 2, 2, 255).getRGB());
                        if (((BooleanValue) value).getValue()) {
                            Gui.drawRect(posX + width - 17.5, valY - 2.5 + valueoffset, posX + width - 8.5,
                                    valY + 2.5 + valueoffset, new Color(43, 203, 115, 255).getRGB());
                        } else {
                            Gui.drawRect(posX + width - 27.5, valY - 2.5 + valueoffset, posX + width - 18,
                                    valY + 2.5 + valueoffset, new Color(165, 38, 38, 255).getRGB());
                        }
                    }
                    valY += Fonts.clickfont.getHeight() * 6;
                } else if (value instanceof NumberValue) {
                    double slidewidth = (posX + width - 8) - (posX + width - 71.5);
                    NumberValue<Number> numberValue = (NumberValue<Number>) value;
                    float length = MathHelper.floor_double((numberValue.getValue().floatValue()
                            - ((NumberValue) value).getMin().floatValue())
                            / (((NumberValue) value).getMax().floatValue()
                            - ((NumberValue) value).getMin().floatValue())
                            * slidewidth);
                    if (!(valY + valueoffset < posY + 24.5f || valY + valueoffset > posY + height - 7.5)) {
                        Fonts.clickfont.drawStringWithShadow(value.getName().toLowerCase(), posX + 5 + width / 2,
                                valY + valueoffset, new Color(255, 255, 255, 255).getRGB());
                        RenderUtils.drawBorderedRect(posX + width - 72, valY - 3 + valueoffset, posX + width - 8,
                                valY + 3 + valueoffset, 0.5, new Color(58, 58, 58, 255).getRGB(),
                                new Color(2, 2, 2, 255).getRGB());
                        Gui.drawRect(posX + width - 71.5, valY - 2.5 + valueoffset, posX + width - 71.5 + length,
                                valY + 2.5 + valueoffset, new Color(80, 80, 80, 255).getRGB());
                        RenderUtils.drawBorderedRect(posX + width - 74 + length, valY - 4.5 + valueoffset,
                                posX + width - 69 + length, valY + 4.5 + valueoffset, 0.5,
                                new Color(80, 80, 80, 255).getRGB(), new Color(0, 0, 0, 255).getRGB());
                        Fonts.slidefont.drawStringWithShadow(value.getValue().toString(),
                                posX + width - 73
                                        - Fonts.slidefont.getStringWidth(value.getValue().toString()),
                                valY - 0.25 + valueoffset, -1);
                        if (MouseUtil.mouseWithinBounds(mouseX, mouseY, posX + width - 72, valY - 3 + valueoffset,
                                posX + width - 8, valY + 3 + valueoffset)) {
                            if (Keyboard.isKeyDown(Keyboard.KEY_LEFT) && timer.timeElapsed(100)) {
                                if (value.getValue() instanceof Double) {
                                    value
                                            .setValue(MathUtil.round((Double) value.getValue()
                                                    - ((NumberValue) value).getIncrement().doubleValue(), 1));
                                }
                                if (value.getValue() instanceof Float) {
                                    value.setValue(
                                            (float) MathUtil.round((Float) value.getValue()
                                                    - ((NumberValue) value).getIncrement().floatValue(), 1));

                                }
                                if (value.getValue() instanceof Long) {
                                    value.setValue(
                                            (long) MathUtil.round(((Long) value.getValue()).longValue()
                                                    - ((NumberValue) value).getIncrement().longValue(), 1));

                                }
                                if (value.getValue() instanceof Integer) {
                                    value
                                            .setValue((int) MathUtil.round(((Integer) value.getValue()).intValue()
                                                    - ((NumberValue) value).getIncrement().intValue(), 1));

                                }
                                if (value.getValue() instanceof Short) {
                                    value.setValue(
                                            (short) MathUtil.round(((Short) value.getValue()).shortValue()
                                                    - ((NumberValue) value).getIncrement().shortValue(), 1));
                                }
                                if (value.getValue() instanceof Byte) {
                                    value.setValue(
                                            (byte) MathUtil.round(((Byte) value.getValue()).byteValue()
                                                    - ((NumberValue) value).getIncrement().byteValue(), 1));
                                }
                            }
                            if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT) && timer.timeElapsed(100)) {
                                if (value.getValue() instanceof Double) {
                                    value
                                            .setValue(MathUtil.round(((Double) value.getValue()).doubleValue()
                                                    + ((NumberValue) value).getIncrement().doubleValue(), 1));
                                }
                                if (value.getValue() instanceof Float) {
                                    value.setValue(
                                            (float) MathUtil.round(((Float) value.getValue()).floatValue()
                                                    + ((NumberValue) value).getIncrement().floatValue(), 1));

                                }
                                if (value.getValue() instanceof Long) {
                                    value.setValue(
                                            (long) MathUtil.round(((Long) value.getValue()).longValue()
                                                    + ((NumberValue) value).getIncrement().longValue(), 1));

                                }
                                if (value.getValue() instanceof Integer) {
                                    value
                                            .setValue((int) MathUtil.round(((Integer) value.getValue()).intValue()
                                                    + ((NumberValue) value).getIncrement().intValue(), 1));

                                }
                                if (value.getValue() instanceof Short) {
                                    value.setValue(
                                            (short) MathUtil.round(((Short) value.getValue()).shortValue()
                                                    + ((NumberValue) value).getIncrement().shortValue(), 1));
                                }
                                if (value.getValue() instanceof Byte) {
                                    value.setValue(
                                            (byte) MathUtil.round(((Byte) value.getValue()).byteValue()
                                                    + ((NumberValue) value).getIncrement().byteValue(), 1));
                                }
                            }
                            if (slidedragging) {
                                if (value.getValue() instanceof Double) {
                                    value
                                            .setValue(
                                                    MathUtil.round(
                                                            ((mouseX - (posX + width - 72))
                                                                    * (((NumberValue) value).getMax().doubleValue()
                                                                    - ((NumberValue) value).getMin()
                                                                    .doubleValue())
                                                                    / slidewidth
                                                                    + ((NumberValue) value).getMin().doubleValue()),
                                                            1));
                                }
                                if (value.getValue() instanceof Float) {
                                    value
                                            .setValue(
                                                    (float) MathUtil.round(
                                                            ((mouseX - (posX + width - 72))
                                                                    * (((NumberValue) value).getMax().floatValue()
                                                                    - ((NumberValue) value).getMin()
                                                                    .floatValue())
                                                                    / slidewidth
                                                                    + ((NumberValue) value).getMin().floatValue()),
                                                            1));
                                }
                                if (value.getValue() instanceof Long) {
                                    value
                                            .setValue(
                                                    (long) MathUtil.round(
                                                            ((mouseX - (posX + width - 72))
                                                                    * (((NumberValue) value).getMax().longValue()
                                                                    - ((NumberValue) value).getMin()
                                                                    .longValue())
                                                                    / slidewidth
                                                                    + ((NumberValue) value).getMin().longValue()),
                                                            1));
                                }
                                if (value.getValue() instanceof Integer) {
                                    value
                                            .setValue(
                                                    (int) MathUtil.round(
                                                            ((mouseX - (posX + width - 72))
                                                                    * (((NumberValue) value).getMax().intValue()
                                                                    - ((NumberValue) value).getMin()
                                                                    .intValue())
                                                                    / slidewidth
                                                                    + ((NumberValue) value).getMin().intValue()),
                                                            1));
                                }
                                if (value.getValue() instanceof Short) {
                                    value
                                            .setValue(
                                                    (short) MathUtil.round(
                                                            ((mouseX - (posX + width - 72))
                                                                    * (((NumberValue) value).getMax().shortValue()
                                                                    - ((NumberValue) value).getMin()
                                                                    .shortValue())
                                                                    / slidewidth
                                                                    + ((NumberValue) value).getMin().shortValue()),
                                                            1));
                                }
                                if (value.getValue() instanceof Byte) {
                                    value
                                            .setValue(
                                                    (byte) MathUtil.round(
                                                            ((mouseX - (posX + width - 72))
                                                                    * (((NumberValue) value).getMax().byteValue()
                                                                    - ((NumberValue) value).getMin()
                                                                    .byteValue())
                                                                    / slidewidth
                                                                    + ((NumberValue) value).getMin().byteValue()),
                                                            1));
                                }
                            }
                        }
                    }
                    valY += Fonts.clickfont.getHeight() * 6;
                } else if (value instanceof EnumValue) {
                    if (!(valY + valueoffset < posY + 24.5f || valY + valueoffset > posY + height - 7.5)) {
                        Fonts.clickfont.drawStringWithShadow(value.getName().toLowerCase(), posX + 5 + width / 2,
                                valY + valueoffset, new Color(255, 255, 255, 255).getRGB());
                        RenderUtils.drawBorderedRect(posX + width - 72, valY - 5 + valueoffset, posX + width - 8,
                                valY + ((enumextended && selectedenum == value)
                                        ? 5 + (((EnumValue) value).getChoices().length - 1)
                                        * (Fonts.clickfont.getHeight() * 3)
                                        : 5) + valueoffset,
                                0.5, new Color(55, 55, 55, 255).getRGB(), new Color(2, 2, 2, 255).getRGB());
                        Fonts.clickfont.drawStringWithShadow(value.getValue().toString().toLowerCase(),
                                posX + width - 71, valY + valueoffset, new Color(255, 255, 255, 255).getRGB());
                        if ((enumextended && selectedenum == value)) {
                            Gui.drawRect(posX + width - 72, valY + valueoffset + Fonts.clickfont.getHeight() + 1.5,
                                    posX + width - 8, valY + valueoffset + Fonts.clickfont.getHeight() + 2,
                                    new Color(0, 0, 0, 255).getRGB());
                            double enumY = valY;
                            for (Enum enumvals : ((EnumValue) value).getChoices()) {
                                if (value.getValue() != enumvals) {
                                    enumY += (Fonts.clickfont.getHeight() * 3);
                                    Fonts.clickfont.drawStringWithShadow(enumvals.toString().toLowerCase(),
                                            posX + width - 71, enumY + valueoffset,
                                            new Color(255, 255, 255, 255).getRGB());
                                }
                            }
                            valY += (((EnumValue) value).getChoices().length - 1) * (Fonts.clickfont.getHeight() * 3);
                        }
                    }
                    valY += Fonts.clickfont.getHeight() * 6;
                } else if (value instanceof ColorValue) {
                    if (!(valY + valueoffset < posY + 24.5f || valY + valueoffset > posY + height - 7.5)) {
                        float[] huee = new float[]{((ColorValue) value).getHue()[0]};
                        Fonts.clickfont.drawStringWithShadow(value.getName().toLowerCase(), posX + 5 + width / 2, valY + valueoffset, new Color(255, 255, 255, 255).getRGB());
                        RenderUtils.drawBorderedRect(posX + width - 28, valY - 3 + valueoffset, posX + width - 8, valY + 3 + valueoffset, 0.5, (Integer) value.getValue(), new Color(2, 2, 2, 255).getRGB());
                        if (colorextended && selectedcolor == value) {
                            // main color box
                            RenderUtils.drawBorderedRect(posX + 25.5 + width / 2, valY + 5.5 + valueoffset, posX + 76.5 + width / 2, valY + 56.5 + valueoffset, 0.5, -1, new Color(0, 0, 0, 255).getRGB());

                            for (int i = 0; i < 50; i++) {
                                for (int k = 0; k < 50; k++) {

                                    int cur = Color.HSBtoRGB(((ColorValue) value).getHuecolor(), k < 25 ? k / 25.0f : 1, k > 25 ? (25 - (k - 25)) / 25.0f : 1);
                                    Color col1 = new Color(cur);
                                    Color col2 = new Color(col1.getRed(), col1.getGreen(), col1.getBlue(), ((ColorValue) value).getAlpha());
                                    RenderUtils.drawRect(posX + 26 + width / 2 + i, valY + 6 + valueoffset + k, posX + 26 + width / 2 + i + 1, valY + 6 + valueoffset + 1 + k, cur);
                                    if (MouseUtil.mouseWithinBounds(mouseX, mouseY, posX + 26f + width / 2 + i, valY + 6 + k + valueoffset, posX + 27f + width / 2 + i, valY + 7 + k + valueoffset) && Mouse.isButtonDown(0)) {
                                        value.setValue(col2.getRGB());
                                    }

                                    if ((Integer) value.getValue() == col2.getRGB())
                                        Gui.drawRect(posX + 26 + width / 2 + i, valY + 6 + valueoffset + k, posX + 26 + width / 2 + i + 1, valY + 6 + valueoffset + 1 + k, -1);
                                }
                            }

                            // hue box
                            RenderUtils.drawBorderedRect(posX + 77.5 + width / 2, valY + 5.5 + valueoffset, posX + 86.5 + width / 2, valY + 56.5 + valueoffset, 0.5, 0, new Color(0, 0, 0, 255).getRGB());

                            for (double i = valY + 6 + valueoffset; i < valY + 56 + valueoffset; i++) {
                                int color = Color.getHSBColor(huee[0] / 255.0f, 1, 1).getRGB();
                                if (isMouseOnColor(mouseX, mouseY, i, posX + 78 + width / 2, posX + 86 + width / 2) && Mouse.isButtonDown(0)) {
                                    ((ColorValue) value).setHuecolor(huee[0] / 255.0f);
                                    float[] hsbValues = new float[3];
                                    Color gaycolor = new Color((Integer) value.getValue());
									hsbValues = Color.RGBtoHSB( gaycolor.getRed(),gaycolor.getGreen(),gaycolor.getBlue(),hsbValues);
									value.setValue(Color.HSBtoRGB(huee[0] / 255.0f,hsbValues[1],hsbValues[2]));
                                }
                                if (((ColorValue) value).getHuecolor() == huee[0] / 255.0f) {
                                    ((ColorValue) value).setPosition(i);
                                }
                                Gui.drawRect(posX + 78 + width / 2, i, posX + 86 + width / 2, i + 1, color);
                                float[] arrf = huee;
                                arrf[0] = arrf[0] + 5.0f;
                                if (huee[0] > 255.0f) {
                                    huee[0] = huee[0] - 255.0f;
                                }
                            }
                            Gui.drawRect(posX + 78 + width / 2, ((ColorValue) value).getPosition(), posX + 86 + width / 2, ((ColorValue) value).getPosition() + 1, -1);
                            if (((ColorValue) value).getHue()[0] > 255.0f) {
                                ((ColorValue) value).getHue()[0] = ((ColorValue) value).getHue()[0] - 255.0f;
                            }
                            // opacity box
                            RenderUtils.drawBorderedRect(posX + 26 + width / 2, valY + 58 + valueoffset, posX + 76 + width / 2, valY + 66 + valueoffset, 0.5, 0, new Color(0, 0, 0, 255).getRGB());
                            double slidewidth = (posX + 76 + width / 2) - (posX + 26 + width / 2);
                            float length = (float) (slidewidth / 255) * ((ColorValue) value).getAlpha();
                            Gui.drawRect((posX + 26.5 + width / 2), valY + 58.5 + valueoffset, (posX + 26 + width / 2) + length, valY + 65.5 + valueoffset, new Color(((ColorValue) value).getAlpha(), ((ColorValue) value).getAlpha(), ((ColorValue) value).getAlpha(), 255).getRGB());
                            RenderUtils.drawBorderedRect(posX + 26 + width / 2, valY + 58 + valueoffset, posX + 76 + width / 2, valY + 66 + valueoffset, 0.5, 0, new Color(0, 0, 0, 255).getRGB());
                            if (MouseUtil.mouseWithinBounds(mouseX, mouseY, posX + 26 + width / 2, valY + 58 + valueoffset, posX + 76 + width / 2, valY + 66 + valueoffset) && Mouse.isButtonDown(0)) {
                                ((ColorValue) value).setAlpha((int) MathUtil.round((mouseX - (posX + 26 + width / 2)) * (255 / slidewidth), 1));
                                Color alphacolor = new Color((Integer) value.getValue());
                                value.setValue(new Color(alphacolor.getRed(),alphacolor.getGreen(),alphacolor.getBlue(),(int) MathUtil.round((mouseX - (posX + 26 + width / 2)) * (255 / slidewidth), 1)).getRGB());
                            }
                        }
                        valY += (colorextended && selectedcolor == value) ? 80 : (Fonts.clickfont.getHeight() * 6);
                    }
                }
            }
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (MouseUtil.mouseWithinBounds(mouseX, mouseY, posX, posY, posX + width, posY + 11)) {
            if (mouseButton == 0) {
                dragging = true;
                this.lastPosX = (posX - mouseX);
                this.lastPosY = (posY - mouseY);
            }
        }
        float x = 0;
        for (Category category : Category.values()) {
            if (MouseUtil.mouseWithinBounds(mouseX, mouseY, posX + 2.5f + x, posY + 10.5f,
                    posX + 2.5f + x + ((width - 5) / Category.values().length), posY + 23)) {
                if (mouseButton == 0) {
                    selectedCategory = category;
                    extended = false;
                    extendedmod = null;
                    modoffset = 0;
                    valueoffset = 0;
                    enumextended = false;
                    selectedenum = null;
                    binding = false;
                    bindingmod = null;
                }
            }
            x += (width - 5) / Category.values().length;
        }
        float modY = posY + 32.5f;
        for (Module mod : Sleek.INSTANCE.getModuleManager().getModules()) {
            if (mod.getCategory() == selectedCategory) {
                if (!(modY + modoffset > posY + height - 7.5 || modY + modoffset < posY + 26.5f)) {
                    if (!mod.getValues().isEmpty() && MouseUtil.mouseWithinBounds(mouseX, mouseY,
                            posX + 7.5f + Fonts.clickfont.getStringWidth(mod.getName().toLowerCase() + " "),
                            modY - 3 + modoffset,
                            posX + 7.5f
                                    + Fonts.clickfont.getStringWidth(mod.getName().toLowerCase()
                                    + ((extended && extendedmod == mod) ? " -" : " +")),
                            modY + 3 + modoffset)) {
                        if (mouseButton == 0 && !binding) {
                            if (extendedmod == mod && extended) {
                                extended = false;
                                extendedmod = null;
                                valueoffset = 0;
                                enumextended = false;
                                selectedenum = null;
                            } else {
                                enumextended = false;
                                selectedenum = null;
                                extendedmod = mod;
                                extended = true;
                            }
                        }
                    }
                    if (!(modY + modoffset > posY + height - 7.5 || modY + modoffset < posY + 26.5f)) {
                        if (MouseUtil.mouseWithinBounds(mouseX, mouseY, posX + 7.5f, modY - 3 + modoffset,
                                posX + 7.5f
                                        + Fonts.clickfont.getStringWidth((bindingmod == mod && binding)
                                        ? "Press any key to bind"
                                        : mod.getName().toLowerCase()
                                        + ((extended && extendedmod == mod) ? " -"
                                        : (mod.getValues().isEmpty() ? "" : " +"))),
                                modY + 3 + modoffset)) {
                            if (mouseButton == 2) {
                                if (bindingmod == mod && binding) {
                                    binding = false;
                                    bindingmod = null;
                                } else {
                                    bindingmod = mod;
                                    binding = true;
                                }
                            }
                        }
                    }
                    if (MouseUtil.mouseWithinBounds(mouseX, mouseY,
                            posX + width - ((extended && extendedmod != null) ? 148 : 28), modY - 3 + modoffset,
                            posX + width - ((extended && extendedmod != null) ? 128 : 8), modY + 3 + modoffset)) {
                        if (mouseButton == 0) {
                            mod.setToggled(!mod.isToggled());
                        }
                    }
                }
                modY += Fonts.clickfont.getHeight() * 6;
            }
        }
        if (extended && extendedmod != null) {
            float valY = posY + 32.5f + Fonts.clickfont.getHeight() * 6;
            for (Value value : extendedmod.getValues()) {
                if (!value.isVisible()) continue;
                if (value instanceof BooleanValue) {
                    if (!(valY + valueoffset < posY + 24.5f || valY + valueoffset > posY + height - 7.5)) {
                        if (MouseUtil.mouseWithinBounds(mouseX, mouseY, posX + width - 28, valY - 3 + valueoffset,
                                posX + width - 8, valY + 3 + valueoffset)) {
                            if (mouseButton == 0) {
                                value.setValue(!((BooleanValue) value).getValue());
                            }
                        }
                    }
                    valY += Fonts.clickfont.getHeight() * 6;
                } else if (value instanceof NumberValue) {
                    double slidewidth = (posX + width - 8) - (posX + width - 71.5);
                    NumberValue<Number> numberValue = (NumberValue<Number>) value;
                    float length = MathHelper.floor_double((numberValue.getValue().floatValue()
                            - ((NumberValue) value).getMin().floatValue())
                            / (((NumberValue) value).getMax().floatValue()
                            - ((NumberValue) value).getMin().floatValue())
                            * slidewidth);
                    if (!(valY + valueoffset < posY + 24.5f || valY + valueoffset > posY + height - 7.5)) {
                        if (MouseUtil.mouseWithinBounds(mouseX, mouseY, posX + width - 72, valY - 3 + valueoffset,
                                posX + width - 8, valY + 3 + valueoffset)) {
                            if (mouseButton == 0) {
                                slidedragging = true;
                            }
                        }
                    }
                    valY += Fonts.clickfont.getHeight() * 6;
                } else if (value instanceof EnumValue) {
                    if (!(valY + valueoffset < posY + 24.5f || valY + valueoffset > posY + height - 7.5)) {
                        if (MouseUtil.mouseWithinBounds(mouseX, mouseY, posX + width - 72, valY - 5 + valueoffset,
                                posX + width - 8, valY + 5 + valueoffset)) {
                            if (mouseButton == 0) {
                                if (enumextended) {
                                    selectedenum = null;
                                    enumextended = false;
                                } else {
                                    selectedenum = ((EnumValue) value);
                                    enumextended = true;
                                }
                            }
                        }
                        if ((enumextended && selectedenum == value)) {
                            float enumY = valY;
                            for (Enum enumvals : ((EnumValue) value).getChoices()) {
                                if (value.getValue() != enumvals) {
                                    enumY += (Fonts.clickfont.getHeight() * 3);
                                    if (MouseUtil.mouseWithinBounds(mouseX, mouseY, posX + width - 71,
                                            enumY + valueoffset - 3, posX + width - 8, enumY + valueoffset + 4)) {
                                        if (mouseButton == 0) {
                                            value.setValue(enumvals);
                                            enumextended = false;
                                            selectedenum = null;
                                        }
                                    }
                                }
                            }
                            valY += (((EnumValue) value).getChoices().length - 1) * (Fonts.clickfont.getHeight() * 3);
                        }
                    }
                    valY += Fonts.clickfont.getHeight() * 6;
                } else if (value instanceof ColorValue) {
                    if (!(valY + valueoffset < posY + 24.5f || valY + valueoffset > posY + height - 7.5)) {
                        if (MouseUtil.mouseWithinBounds(mouseX, mouseY, posX + width - 28, valY - 3 + valueoffset, posX + width - 8, valY + 3 + valueoffset) && mouseButton == 0) {
                            if (mouseButton == 0) {
                                if (selectedcolor == value && colorextended) {
                                    colorextended = false;
                                    selectedcolor = null;
                                } else {
                                    selectedcolor = ((ColorValue) value);
                                    colorextended = true;
                                }
                            }
                        }
                        if (MouseUtil.mouseWithinBounds(mouseX, mouseY, posX + 26 + width / 2, valY + 58 + valueoffset, posX + 76 + width / 2, valY + 66 + valueoffset) && mouseButton == 0) {
                            if (mouseButton == 0) {
                                colorsliding = true;
                            }
                        }
                        valY += (colorextended && selectedcolor == value) ? 80 : (Fonts.clickfont.getHeight() * 6);
                    }
                }
            }
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            dragging = false;
            slidedragging = false;
            colorsliding = false;
        }
    }

    @Override
    public String getLabel() {
        return label;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Category getSelectedCategory() {
        return selectedCategory;
    }

    public void prepareScissorBox(float x, float y, float x2, float y2) {
        ScaledResolution scale = new ScaledResolution(Minecraft.getMinecraft());
        int factor = scale.getScaleFactor();
        GL11.glScissor((int) (x * factor), (int) ((scale.getScaledHeight() - y2) * factor), (int) ((x2 - x) * factor),
                (int) ((y2 - y) * factor));
    }

    public void keyTyped(char typedChar, int key) {
        if (binding && key != Keyboard.KEY_ESCAPE) {
            bindingmod.setKeybind(Keyboard.KEY_BACK == key ? Keyboard.KEY_NONE : key);
            ChatUtil.log(
                    "Bound " + bindingmod.getName() + " to " + (key == Keyboard.KEY_BACK ? "none" : typedChar) + ".");
            binding = false;
            bindingmod = null;
        }
    }

    public boolean isMouseOnColor(final int mouseX, final int mouseY, double i, double posXStart, double posXEnd) {
        return mouseX > posXStart && mouseX < posXEnd && mouseY > i && mouseY < i + 1;
    }

    public boolean isMouseOnColor2(final int mouseX, final int mouseY, double i, double k) {
        return mouseX > i && mouseX < i + 1 && mouseY > k && mouseY < k + 1;
    }

    private Color chanceSat(Color incolor, float[] sat, float[] bright) {
        Color outColor = Color.getHSBColor(incolor.getRGB(), sat[0] / 255, bright[0] / 255f);
        return outColor;
    }
}