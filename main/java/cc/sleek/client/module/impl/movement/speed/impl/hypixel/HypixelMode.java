package cc.sleek.client.module.impl.movement.speed.impl.hypixel;

import cc.sleek.client.Sleek;
import cc.sleek.client.event.impl.StrafeEvent;
import cc.sleek.client.event.impl.UpdateEvent;
import cc.sleek.client.module.impl.combat.KillAura;
import cc.sleek.client.module.impl.combat.TargetStrafe;
import cc.sleek.client.module.impl.movement.Speed;
import cc.sleek.client.module.impl.movement.speed.SpeedMode;
import cc.sleek.client.util.PlayerUtil;
import cc.sleek.client.util.StringUtil;

public class HypixelMode extends SpeedMode {
    public HypixelMode() {
        super("Hypixel");
    }

    private float distanceToLastPos = 0.0F;
    private int stage = 0;
    private double spood = 0.0F;

    @Override
    public void handleStrafe(StrafeEvent strafeEvent) {
//        if (mc.thePlayer.isMoving()) {
//            switch (stage) {
//                case 0:
//                    distanceToLastPos = 0;
//                    stage++;
//                    break;
//                case 2:
//                    if (mc.thePlayer.isMovingOnGround()) {
//                        mc.thePlayer.motionY = 0.42F;
//                        spood = (PlayerUtil.getBaseMoveSpeedFloat() * 2.0F) * (getSpeed().getValue());
//
//                    }
//                    break;
//                case 3:
//
//                    spood = distanceToLastPos - (0.7F * (distanceToLastPos - PlayerUtil.getBaseMoveSpeedFloat()));
//                    break;
//
//                default:
//                    if (PlayerUtil.isOnGround() && stage > 0) {
//                        stage = !mc.thePlayer.isMoving() ? 0 : 1;
//                    }
//                    spood = distanceToLastPos - distanceToLastPos / 159.0F;
//                    break;
//            }
//        }
        if (mc.thePlayer.onGround) {
            mc.thePlayer.motionY = 0.3999659875124693;
            spood = PlayerUtil.getBaseMoveSpeed() * 2.149;
            stage = 0;
        } else {
            if (stage == 1) {
                spood = distanceToLastPos = PlayerUtil.getBaseMoveSpeedFloat() * 1.63F;
                double difference = 0.66 * (this.distanceToLastPos - PlayerUtil.getBaseMoveSpeed());
                this.spood = this.distanceToLastPos - difference;
            }
            else
                spood = distanceToLastPos - distanceToLastPos / 160F;
        }
        spood = Math.max(PlayerUtil.getBaseMoveSpeed(), spood);
        strafeEvent.setMotionStrafe((float) spood, 0.2375f);
        stage++;
//        if (mc.thePlayer.hurtTime > 8) {
//            spood += 0.25F;
//        }
//        spood = Math.max(PlayerUtil.getBaseMoveSpeedFloat(), spood);
//        TargetStrafe targetStrafe = Sleek.INSTANCE.getModuleManager().getModuleByName("Target Strafe");
//        boolean canStrafe = targetStrafe.canStrafe();
//        if (canStrafe) {
//            strafeEvent.setForward(mc.thePlayer.getDistanceToEntity(KillAura.target) <= targetStrafe.getRange().getValue() ? 0 : 1);
//            strafeEvent.setStrafe((float) targetStrafe.getDir());
//            strafeEvent.setYaw(PlayerUtil.getRotationsRandom(KillAura.target).getRotationYaw());
//        }
//        strafeEvent.setMotionStrafe(spood, 0.22F);
    }

    @Override
    public void handleUpdate(UpdateEvent updateEvent) {
        if (updateEvent.isPre()) {
            float xDifference = (float) (mc.thePlayer.posX - mc.thePlayer.lastTickPosX);
            float zDifference = (float) (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ);
            distanceToLastPos = (float) Math.sqrt(xDifference * xDifference + zDifference * zDifference);
        }
    }
}
