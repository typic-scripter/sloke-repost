package cc.sleek.client.module.impl.movement.speed.impl.hypixel;

import cc.sleek.client.event.impl.StrafeEvent;
import cc.sleek.client.event.impl.UpdateEvent;
import cc.sleek.client.module.impl.movement.speed.SpeedMode;
import cc.sleek.client.util.PlayerUtil;

public class HypixelDosMode extends SpeedMode {// dos bc español
    public HypixelDosMode() {
        super("Hypixel 2");
    }

    int stage = 0;
    double moveSpeed = 0;
    double lastDistance = 0;
    double weird = 0.0F;

    @Override
    public void handleStrafe(StrafeEvent event) {
        if (mc.thePlayer.ticksExisted % 2 == 0) {
            weird += 1e-7;
        }
        if (mc.thePlayer.onGround) {
            mc.thePlayer.motionY = 0.3999659875124693;
            moveSpeed = PlayerUtil.getBaseMoveSpeed() * 2.149;
            stage = 0;
        } else {
            if (stage == 1) {
                moveSpeed = lastDistance = PlayerUtil.getBaseMoveSpeed() * 1.63;
                double difference = 0.66 * (this.lastDistance - PlayerUtil.getBaseMoveSpeed());
                this.moveSpeed = this.lastDistance - difference;
            }
            else
                moveSpeed = lastDistance - lastDistance / 160F;
        }
        moveSpeed = Math.max(PlayerUtil.getBaseMoveSpeed(), moveSpeed);
        event.setMotionStrafe((float) moveSpeed, 0.2375f + weird);
        stage++;
    }

    @Override
    public void handleUpdate(UpdateEvent event) {
        double xd = mc.thePlayer.posX - mc.thePlayer.prevPosX;
        double zd = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
        lastDistance = Math.sqrt((xd * xd) + (zd * zd));
    }
}
