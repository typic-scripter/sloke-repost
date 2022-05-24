package cc.sleek.client.module.impl.movement.speed.impl.misc;

import cc.sleek.client.event.impl.MoveEvent;
import cc.sleek.client.module.impl.movement.speed.SpeedMode;
import cc.sleek.client.util.PlayerUtil;

public class FrictionMode extends SpeedMode {
    private float spood = 0.0F;

    public FrictionMode() {
        super("Friction");
    }

    @Override
    public void handleMove(MoveEvent event) {
        if (mc.thePlayer.isMovingOnGround()) {
            event.setMotionY(mc.thePlayer.motionY = 0.42F);
            spood = getSpeed().getValue();
        }
        spood = getSpeedMod().handleFriction(spood);
        PlayerUtil.setSpeed(event, spood);
    }

    @Override
    public void onEnable() {
        spood = 0.0F;
    }
}
