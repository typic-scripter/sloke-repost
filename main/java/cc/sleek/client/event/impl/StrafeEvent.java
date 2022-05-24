package cc.sleek.client.event.impl;

import cc.sleek.client.Sleek;
import cc.sleek.client.event.Event;
import cc.sleek.client.module.impl.combat.KillAura;
import cc.sleek.client.module.impl.combat.TargetStrafe;
import cc.sleek.client.util.IUtil;
import cc.sleek.client.util.PlayerUtil;

/**
 * @author Dort
 * @see net.minecraft.entity.Entity#moveFlying
 */
public class StrafeEvent extends Event implements IUtil {


    private float strafe;
    private float forward;
    private float friction;
    private float yaw;
    private boolean silent;

    public StrafeEvent(float strafe, float forward, float friction, float yaw) {
        this.strafe = strafe;
        this.forward = forward;
        this.friction = friction;
        this.yaw = yaw;
    }

    public float getStrafe() {
        return strafe;
    }

    public void setStrafe(float strafe) {
        this.strafe = strafe;
    }

    public float getForward() {
        return forward;
    }

    public void setForward(float forward) {
        this.forward = forward;
    }

    public float getFriction() {
        return friction;
    }

    public void setFriction(float friction) {
        this.friction = friction;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public boolean isSilent() {
        return silent;
    }

    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    public void setMotion(double speed) {
        mc.thePlayer.motionX = 0;
        mc.thePlayer.motionZ = 0;
        TargetStrafe targetStrafe = Sleek.INSTANCE.getModuleManager().getModuleByName("Target Strafe");
        boolean canStrafe = targetStrafe.canStrafe();
        if (canStrafe) {
            // from PlayerUtil#setMotion
            forward = mc.thePlayer.getDistanceToEntity(KillAura.target) <= targetStrafe.getRange().getValue() ? 0 : 1;
            strafe = (float) targetStrafe.getDir();
            yaw = PlayerUtil.getRotationsRandom(KillAura.target).getRotationYaw();
        }
        speed *= strafe != 0 && forward != 0 ? 0.91F : 1F;
        setFriction((float) speed);
    }

    /**
     * Sets motion with an illegitimate strafe & legitimate forward component
     *
     * @param friction        - The friction
     * @param strafeComponent - Strafe component value ranging from 0.0 to 1.0
     */
    public void setMotionStrafe(double friction, double strafeComponent) {
        double remainder = 1F - strafeComponent;
        if (forward != 0 && strafe != 0)
            friction *= 0.91;
        if (mc.thePlayer.onGround) {
            setMotion(friction);
        } else {
            mc.thePlayer.motionX *= strafeComponent;
            mc.thePlayer.motionZ *= strafeComponent;
            setFriction((float) (friction * remainder));
        }
    }
}


