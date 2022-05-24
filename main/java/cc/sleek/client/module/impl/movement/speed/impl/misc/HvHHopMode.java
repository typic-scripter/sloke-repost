package cc.sleek.client.module.impl.movement.speed.impl.misc;

import cc.sleek.client.event.impl.StrafeEvent;
import cc.sleek.client.module.impl.movement.speed.SpeedMode;

public class HvHHopMode extends SpeedMode {
    private float floaterManXDHackerJimBobWay = 0.0F;

    public HvHHopMode() {
        super("HvH Hop");
    }

    @Override
    public void handleStrafe(StrafeEvent event) {

        if (mc.thePlayer.onGround) {
            mc.thePlayer.motionY = 0.48F; // flashy server momento
            floaterManXDHackerJimBobWay = 0.3F;

        } else {
            floaterManXDHackerJimBobWay = getSpeed().getValue();
        }
        event.setMotion(floaterManXDHackerJimBobWay);

    }
}
