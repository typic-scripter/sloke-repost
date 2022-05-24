package cc.sleek.client.module.impl.movement.speed.impl.misc;

import cc.sleek.client.event.impl.StrafeEvent;
import cc.sleek.client.module.impl.movement.speed.SpeedMode;

public class BhopMode extends SpeedMode {

    public BhopMode() {
        super("Bhop");
    }

    @Override
    public void handleStrafe(StrafeEvent strafeEvent) {
        if (mc.thePlayer.isMovingOnGround()) {
            mc.thePlayer.motionY = 0.42f;
        }
        strafeEvent.setMotion(getSpeed().getValue());
    }
}
