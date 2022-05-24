package cc.sleek.client.util;

import cc.sleek.client.Sleek;
import cc.sleek.client.event.impl.MoveEvent;
import cc.sleek.client.module.impl.combat.KillAura;
import cc.sleek.client.module.impl.combat.TargetStrafe;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovementInput;

import java.util.concurrent.ThreadLocalRandom;

public class PlayerUtil implements IUtil {


    public static double getDirection() {
        float rotationYaw = mc.thePlayer.rotationYaw;

        if (mc.thePlayer.moveForward < 0F) rotationYaw += 180F;

        float forward = 1F;
        if (mc.thePlayer.moveForward < 0F) forward = -0.5F;
        else if (mc.thePlayer.moveForward > 0F) forward = 0.5F;

        if (mc.thePlayer.moveStrafing > 0F) rotationYaw -= 90F * forward;

        if (mc.thePlayer.moveStrafing < 0F) rotationYaw += 90F * forward;
        return Math.toRadians(rotationYaw);
    }

    public static boolean isHoldingSword() {
        return mc.thePlayer.ticksExisted > 3 && PlayerUtil.mc.thePlayer.getCurrentEquippedItem() != null && PlayerUtil.mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword;
    }

    public static Rotation getRotationsRandom(EntityLivingBase entity) {

        ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
        double randomXZ = threadLocalRandom.nextDouble(-0.05, 0.1);
        double randomY = threadLocalRandom.nextDouble(-0.05, 0.1);
        double x = entity.posX + randomXZ;
        double y = entity.posY + (entity.getEyeHeight() / 2.05) + randomY;
        double z = entity.posZ + randomXZ;
        return attemptFacePosition(x, y, z);
    }

    public static Rotation attemptFacePosition(double x, double y, double z) {
        double xDiff = x - mc.thePlayer.posX;
        double yDiff = y - mc.thePlayer.posY - 1.2;
        double zDiff = z - mc.thePlayer.posZ;

        double dist = Math.hypot(xDiff, zDiff);
        float yaw = (float) (Math.atan2(zDiff, xDiff) * 180 / Math.PI) - 90;
        float pitch = (float) -(Math.atan2(yDiff, dist) * 180 / Math.PI);
        return new Rotation(yaw, pitch);
    }


    public static void setSpeed(double moveSpeed) {
        //EntityLivingBase entity = KillAura.currentTarget;
        EntityLivingBase entity = KillAura.target;
        TargetStrafe targetStrafe = Sleek.INSTANCE.getModuleManager().getModuleByName("Target Strafe");
        boolean canStrafe = targetStrafe.canStrafe();
        MovementInput movementInput = mc.thePlayer.movementInput;

        double forward = canStrafe ? mc.thePlayer.getDistanceToEntity(entity) <= targetStrafe.getRange().getValue() ? 0 : 1 : movementInput.moveForward;
        double strafe = canStrafe ? targetStrafe.getDir() : movementInput.moveStrafe;
        double rotationYaw = canStrafe ? getRotationsRandom(entity).getRotationYaw() : mc.thePlayer.rotationYaw;

        if ((forward == 0.0D && strafe == 0.0D) || moveSpeed == 0.0F) {
            mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
        } else {
            strafe = strafe > 0 ? 1 : strafe < 0 ? -1 : strafe;
            if (forward != 0.0D) {
                rotationYaw += strafe > 0.0D ? (forward > 0.0D ? -45 : 45) : (strafe < 0.0D) ? (forward > 0.0D ? 45 : -45) : 0.0D;
                strafe = 0.0D;
                forward = forward > 0 ? 1 : forward < 0 ? -1 : forward;
            }
            double cos = Math.cos(Math.toRadians(rotationYaw + 90.0F));
            double sin = Math.sin(Math.toRadians(rotationYaw + 90.0F));
            mc.thePlayer.motionX = forward * moveSpeed * cos
                    + strafe * moveSpeed * sin;
            mc.thePlayer.motionZ = forward * moveSpeed * sin
                    - strafe * moveSpeed * cos;
        }
    }

    public static void setSpeed(MoveEvent event, double moveSpeed) {

        EntityLivingBase entity = KillAura.target;
        TargetStrafe targetStrafe = Sleek.INSTANCE.getModuleManager().getModuleByName("Target Strafe");
        boolean canStrafe = targetStrafe.canStrafe();
        MovementInput movementInput = mc.thePlayer.movementInput;

        double forward = canStrafe ? mc.thePlayer.getDistanceToEntity(entity) <= targetStrafe.getRange().getValue() ? 0 : 1 : movementInput.moveForward;
        double strafe = canStrafe ? targetStrafe.getDir() : movementInput.moveStrafe;
        double rotationYaw = canStrafe ? getRotationsRandom(entity).getRotationYaw() : mc.thePlayer.rotationYaw;

        if (forward == 0.0D && strafe == 0.0D) {
            event.setMotionX(0);
            event.setMotionZ(0);
        } else {
            strafe = strafe > 0 ? 1 : strafe < 0 ? -1 : strafe;
            if (forward != 0.0D) {
                rotationYaw += strafe > 0.0D ? (forward > 0.0D ? -45 : 45) : (strafe < 0.0D) ? (forward > 0.0D ? 45 : -45) : 0.0D;
                strafe = 0.0D;
                forward = forward > 0 ? 1 : forward < 0 ? -1 : forward;
            }
            double cos = Math.cos(Math.toRadians(rotationYaw + 90.0F));
            double sin = Math.sin(Math.toRadians(rotationYaw + 90.0F));
            event.setMotionX(forward * moveSpeed * cos
                    + strafe * moveSpeed * sin);
            event.setMotionZ(forward * moveSpeed * sin
                    - strafe * moveSpeed * cos);
        }
    }


    public static void teleportOnGround(double speed, double y) {
        final double posX = mc.thePlayer.posX;
        final double posY = mc.thePlayer.posY;
        final double posZ = mc.thePlayer.posZ;
        final double x = -Math.sin(getDirection()) * speed;
        final double z = Math.cos(getDirection()) * speed;
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX + x, posY + y, posZ + z, true));
        mc.thePlayer.setPosition(posX + x, posY + y, posZ + z);
    }

    public static void teleport(double speed, double y) {
        final double posX = mc.thePlayer.posX;
        final double posY = mc.thePlayer.posY;
        final double posZ = mc.thePlayer.posZ;
        final double x = -Math.sin(getDirection()) * speed;
        final double z = Math.cos(getDirection()) * speed;
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX + x, posY + y, posZ + z, mc.thePlayer.onGround));
        mc.thePlayer.setPosition(posX + x, posY + y, posZ + z);
    }

    public static void damageHypixel() {
        double damage = .05; // should be =<1.0
        double blocks = 3 + damage;
        double x = mc.thePlayer.posX, y = mc.thePlayer.posY, z = mc.thePlayer.posZ;
        ChatUtil.log("Sending " + ((blocks / 0.0625) * 2) + " packets " + ((blocks / 0.0625) * 0.0625));
        for (double i = 0; i <= blocks / 0.0625; i++) {
            IPacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.0625, z, false));
            IPacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false));
        }

        IPacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, true));
    }

    // verus damage
    public static void damageVerus() {
        IPacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(mc.thePlayer.posX,mc.thePlayer.posY-1.5,mc.thePlayer.posZ),1,new ItemStack(Blocks.stone.getItem(mc.theWorld,new BlockPos(-1,-1,-1))),0,0.94f,0));
        IPacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,mc.thePlayer.posY + 3.05,mc.thePlayer.posZ,false));
        IPacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,mc.thePlayer.posY,mc.thePlayer.posZ,false));
        IPacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,mc.thePlayer.posY + 0.42,mc.thePlayer.posZ,true));
        //idk who made this but its weird asf and flags so im recoding it - Cade ^^
        /*
        IPacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));

        double val1 = 0;
        // go up 3 blocks
        for (int i = 0; i <= 6; i++) {
            val1 += 0.5;
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + val1, mc.thePlayer.posZ, true));
        }

        //        float[] valz = {
//                0.07840000152587834F,
//                0.07840000152587834F,
//                0.07840000152587834F,
//                0.23052736891295922F,
//                0.30431682745754074F,
//                0.37663049823865435F,
//                0.44749789698342113F,
//                0.5169479491049742F,
//                0.5850090015087517F,
//                0.6517088341626192F,
//                0.1537296175885956F
//        };
//        float sadafwadf = 4.086577f;


        double val2 = mc.thePlayer.posY + val1 - 4.086577f;

        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, val2, mc.thePlayer.posZ, false));
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));

        IPacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));

         */

    }
    // Where movement util

    public static double getBaseMoveSpeed() {
        double baseSpeed = 0.2873D; // ew doubles and not floats
        if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed)) {
            int amplifier = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0D + 0.2D * (double) (amplifier + 1);
        }

        return baseSpeed;
    }


    public static float getBaseMoveSpeedFloat() {
        float baseSpeed = 0.2873F;
        if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed)) {
            int amplifier = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0F + (0.2F * (float) (amplifier + 1));
        }

        return baseSpeed;
    }

    public static boolean isMoving() {
        return mc.thePlayer != null && (mc.thePlayer.movementInput.moveForward != 0F || mc.thePlayer.movementInput.moveStrafe != 0F);
    }

    /**
     * Checks if the player is truly on ground
     *
     * @return true if the player is on ground
     */
    public static boolean isOnGround() {
        return mc.thePlayer != null && (mc.thePlayer.onGround || mc.thePlayer.isCollidedVertically);
    }

    public static double getBPS() {
        return (mc.thePlayer.getDistance(mc.thePlayer.lastTickPosX, mc.thePlayer.lastTickPosY, mc.thePlayer.lastTickPosZ)) * (mc.timer.timerSpeed * mc.timer.ticksPerSecond);
    }
}
