package cc.sleek.client.module.impl.movement.speed.impl.verus;

import cc.sleek.client.event.impl.StrafeEvent;
import cc.sleek.client.module.impl.movement.speed.SpeedMode;
import cc.sleek.client.util.PlayerUtil;

public class VerusMode extends SpeedMode {
    public VerusMode() {
        super("Verus");
    }

    @Override
    public void handleStrafe(StrafeEvent strafeEvent) {
        // credits to haiku#6872
        double yaw = PlayerUtil.getDirection();
        if (mc.thePlayer.onGround) {
            if (!mc.gameSettings.keyBindJump.pressed && mc.thePlayer.isSprinting()) {
                mc.thePlayer.jump();
                strafeEvent.setMotion(PlayerUtil.getBaseMoveSpeedFloat() * 1.4F);
                mc.thePlayer.motionZ = Math.cos(yaw) * (PlayerUtil.getBaseMoveSpeedFloat() * 1.4F);
            }
        } else {
            if (!(mc.thePlayer.fallDistance > 0.1)) mc.thePlayer.motionY = -0.0784000015258789;
            strafeEvent.setMotion(PlayerUtil.getBaseMoveSpeedFloat());
            strafeEvent.setMotion(PlayerUtil.getBaseMoveSpeedFloat());
        }
    }
}
