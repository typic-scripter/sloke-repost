package cc.sleek.client.util.fonts;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.awt.*;


/**
 * Made by Anthony A.
 * 11/11/2018 / 9:09 AM
 * HTB
 **/
public class Fonts {

    public static final MCFontRenderer WaterMark = new MCFontRenderer(
            new Font("Helmet-Regular", Font.PLAIN, 32), true, true);
    public static final MCFontRenderer WaterMarkNumber = new MCFontRenderer(
            new Font("Helmet-Regular", Font.PLAIN, 16), true, true);
    public static final MCFontRenderer namefont = new MCFontRenderer(new Font("Comic Sans", Font.PLAIN, 18), false, true);
    public static final MCFontRenderer mainfont = new MCFontRenderer(new Font("Comic Sans", Font.PLAIN, 18), true, true);
    public static final MCFontRenderer asdsadadsadadasda = new MCFontRenderer(new Font("Arial", Font.BOLD, 18), true, true);
    public static final MCFontRenderer clickfont = new MCFontRenderer(new Font("Arial", Font.ROMAN_BASELINE, 12), true, true);
    public static final MCFontRenderer slidefont = new MCFontRenderer(new Font("Arial", Font.ROMAN_BASELINE, 10), true, true);
    public static final MCFontRenderer ArrayList = new MCFontRenderer(new Font("Product Sans", Font.PLAIN, 20), true, true);
    public static final MCFontRenderer productSans20 = new MCFontRenderer(new Font("Product Sans", Font.PLAIN, 20), true, true);
    public static final MCFontRenderer productSans24 = new MCFontRenderer(new Font("Product Sans", Font.PLAIN, 24), true, true);

    public static Font fontFromTTF(ResourceLocation fontLocation, float fontSize, int fontType) {
        Font output = null;
        try {
            output = Font.createFont(fontType, Minecraft.getMinecraft().getResourceManager().getResource(fontLocation).getInputStream());
            output = output.deriveFont(fontSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

}
