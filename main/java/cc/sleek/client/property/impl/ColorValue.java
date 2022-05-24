package cc.sleek.client.property.impl;

/**
 * @author Kansio
 */

import cc.sleek.client.property.Value;

public class ColorValue extends Value<Integer> {
    private float[] hue = new float[]{0.0f};
    private float[] brightness = new float[]{255.0f};
    private float[] saturation = new float[]{255.0f};
    private double position;
    private double posX;
    private double posY;
    private float huecolor;
    private int alpha = 255;

    public ColorValue(String label, Integer value) {
        super(label, value);
        position = -1111;
    }

    public double getPosition() {
        return this.position;
    }

    public void setPosition(double position) {
        this.position = position;
    }

    public double getPosX() {
        return this.posX;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public double getPosY() {
        return this.posY;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public float getHuecolor() {
        return this.huecolor;
    }

    public void setHuecolor(float huecolor) {
        this.huecolor = huecolor;
    }

    public float[] getHue() {
        return this.hue;
    }

    public void setHue(float[] hue) {
        this.hue = hue;
    }

    public int getAlpha() {
        return this.alpha;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public float[] getBrightness() {
        return this.brightness;
    }

    public void setBrightness(float[] brightness) {
        this.brightness = brightness;
    }

    public float[] getSaturation() {
        return this.saturation;
    }

    public void setSaturation(float[] saturation) {
        this.saturation = saturation;
    }
}