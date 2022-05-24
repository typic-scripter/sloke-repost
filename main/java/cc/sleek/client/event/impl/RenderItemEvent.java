package cc.sleek.client.event.impl;

import cc.sleek.client.event.Event;

/**
 * @author Kansio
 */
public class RenderItemEvent extends Event {

    private final float swingProgress;
    private final float useProgress;
    private final float f2;
    private final float f3;

    private boolean overriding;

    public boolean isOverriding() {
        return overriding;
    }

    public RenderItemEvent(float swingProgress, float useProgress, float f2, float f3) {
        this.swingProgress = swingProgress;
        this.useProgress = useProgress;
        this.f2 = f2;
        this.f3 = f3;
    }

    public float getSwingProgress() {
        return swingProgress;
    }

    public float getF2() {
        return f2;
    }

    public float getF3() {
        return f3;
    }

    public float getUseProgress() {
        return useProgress;
    }

    public void setOverriding(boolean overriding) {
        this.overriding = overriding;
    }
}
