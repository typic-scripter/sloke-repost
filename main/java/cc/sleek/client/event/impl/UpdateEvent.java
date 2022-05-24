package cc.sleek.client.event.impl;

import cc.sleek.client.event.Event;
import net.minecraft.client.Minecraft;

public class UpdateEvent extends Event {

    private double x, y, z;
    private float yaw, pitch;
    private boolean onGround;
    private final boolean pre;

    public UpdateEvent(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        this(true);
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    public UpdateEvent(boolean pre) {
        this.pre = pre;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        Minecraft.getMinecraft().thePlayer.renderYawOffset = yaw;
        Minecraft.getMinecraft().thePlayer.rotationYawHead = yaw;
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        Minecraft.getMinecraft().thePlayer.renderPitch = pitch;
        this.pitch = pitch;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public boolean isPre() {
        return pre;
    }
}
