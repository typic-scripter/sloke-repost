package cc.sleek.client.event.impl;

import cc.sleek.client.event.Event;

public class MoveEvent extends Event {

    private double motionX, motionY, motionZ;

    public MoveEvent(double motionX, double motionY, double motionZ) {
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
    }

    public double getMotionX() {
        return motionX;
    }

    public void setMotionX(double motionX) {
        this.motionX = motionX;
    }

    public double getMotionY() {
        return motionY;
    }

    public void setMotionY(double motionY) {
        this.motionY = motionY;
    }

    public double getMotionZ() {
        return motionZ;
    }

    public void setMotionZ(double motionZ) {
        this.motionZ = motionZ;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        super.setCancelled(cancelled);
        if (cancelled) {
            // i rather do this here instead of in EntityPlayerSP.java
            this.motionX = 0.0;
            this.motionY = 0.0;
            this.motionZ = 0.0;
        }
    }
}
