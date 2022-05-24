package cc.sleek.client.module.impl.combat;

import cc.sleek.client.event.impl.UpdateEvent;
import cc.sleek.client.module.Module;
import cc.sleek.client.module.api.Category;
import cc.sleek.client.module.api.ModuleInfo;
import cc.sleek.client.property.impl.BooleanValue;
import cc.sleek.client.property.impl.EnumValue;
import cc.sleek.client.property.impl.NumberValue;
import cc.sleek.client.util.ChatUtil;
import cc.sleek.client.util.IPacketUtil;
import cc.sleek.client.util.Stopwatch;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.MathHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@ModuleInfo(name = "FightBot", category = Category.COMBAT, description = "Shitty mc-shitface fightbot")
public class FightBot extends Module {

    private final EnumValue<Mode> mode = new EnumValue<>("Mode", Mode.values());
    // works okay for now ig fixing soon. hopefully tmr. try not to break it too much.
    private final NumberValue<Integer> cpsVal = new NumberValue<>("CPS", 12, 1, 20, 1);
    // make randomization bool actually do smth
    private final BooleanValue randomizationBool = new BooleanValue("Randomization (recommended)", true);
    private final NumberValue<Integer> randomizationMinVal = new NumberValue<>("Random Min", 9, 1, 20, 1);
    private final NumberValue<Integer> randomizationMaxVal = new NumberValue<>("Random Max", 15, 1, 20, 1);
    private final NumberValue<Double> Areach = new NumberValue<>("Attack Reach", 3.0, 0.0, 6.0, 0.1);
    private final NumberValue<Integer> Sreach = new NumberValue<>("Search Reach", 30, 10, 120, 1);
    private final BooleanValue players = new BooleanValue("Players", true);
    private final BooleanValue invis = new BooleanValue("Invisibles", false);
    private final BooleanValue animals = new BooleanValue("Animals", false);
    private final BooleanValue monsters = new BooleanValue("Monsters", true);
    private final BooleanValue walls = new BooleanValue("Walls", false);

    private final Stopwatch stopwatch = new Stopwatch();
    private int cps = cpsVal.getValue();

    @EventLink
    Listener<UpdateEvent> updateEventListener = event -> {
        switch (mode.getValue()) {
            case LEGIT: {
                if (event.isPre()) {
                    //fetch all possible targets
                    List<EntityLivingBase> entityLivingBases = getTargets();

                    //sort them by distance
                    entityLivingBases.sort(Comparator.comparingInt(e -> (int) mc.thePlayer.getDistanceToEntity(e)));

                    if (!entityLivingBases.isEmpty()) {
                        EntityLivingBase target = entityLivingBases.get(0);
                        // Rotations
                        float[] rots = doRotations(target);
                        event.setYaw(mc.thePlayer.rotationYaw = rots[0]);
                        event.setPitch(mc.thePlayer.rotationPitch = rots[1]);
                        // "Ai" its just conditions
                        if (mc.thePlayer.getDistanceToEntity(target) > 3) {
                            if (!mc.thePlayer.isSprinting()) {
                                mc.thePlayer.setSprinting(true);
                            }
                            mc.gameSettings.keyBindForward.pressed = true;
                            ChatUtil.log("Entity beyond reach distance, moving...");
                        } else {
                            mc.thePlayer.setSprinting(false);
                        }
                        if (Math.round(mc.thePlayer.posY) < Math.round(target.posY) && (mc.thePlayer.onGround || mc.thePlayer.isInWater())) {
                            // turn off when on flat land with target, otherwise it will jump on target hit and lose
//                    mc.thePlayer.jump();
                            ChatUtil.log("Jumped to try and correct y level.");
                        }
                        // gonna try smth here soon
//                if(target.hurtTime == 0 && mc.thePlayer.hurtTime > 0){
//                }
                        // Remember to add booleans for logging cps and ^ up there
                        ChatUtil.log(cps);
                        if (stopwatch.timeElapsed(1000 / cps)) {
                            cps += ThreadLocalRandom.current().nextInt(-1, 2); // spikes ig
                            if (cps < randomizationMinVal.getValue()) {
                                cps = cpsVal.getValue();
                            }
                            if (cps > randomizationMaxVal.getValue()) {
                                cps = cpsVal.getValue();
                            }
                            if (mc.thePlayer.getDistanceToEntity(target) < Areach.getValue()) {
                                mc.thePlayer.swingItem();
                                IPacketUtil.sendPacket(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
                            }
                            stopwatch.resetTime();
                        }
                    }
                }
                break;
            }
            case RAGE: {
                if (event.isPre()) {
                    //fetch all possible targets
                    List<EntityLivingBase> entityLivingBases = getTargets();

                    //sort them by distance
                    entityLivingBases.sort(Comparator.comparingInt(e -> (int) mc.thePlayer.getDistanceToEntity(e)));

                    if (!entityLivingBases.isEmpty()) {
                        EntityLivingBase target = entityLivingBases.get(0);
                        // Rotations
                        float[] rots = doRotations(target);
                        event.setYaw(mc.thePlayer.rotationYaw = rots[0]);
                        event.setPitch(mc.thePlayer.rotationPitch = rots[1]);
                        // "Ai" its just conditions
                        if (mc.thePlayer.getDistanceToEntity(target) > 3) {
                            if (!mc.thePlayer.isSprinting()) {
                                mc.thePlayer.setSprinting(true);
                            }
                            mc.gameSettings.keyBindForward.pressed = true;
                            mc.gameSettings.keyBindJump.pressed = true;
                            ChatUtil.log("Entity beyond reach distance, moving...");
                        } else {
                            mc.thePlayer.setSprinting(false);
                        }
                        ChatUtil.log(cps);
                        if (stopwatch.timeElapsed(1000 / cps)) {
                            IPacketUtil.sendPacketNoEvent(new C0APacketAnimation());
                            IPacketUtil.sendPacketNoEvent(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
                        }
                    } else {
                        float[] rots = rotationToSpawn();
                        event.setYaw(mc.thePlayer.rotationYaw = rots[0]);
                        event.setPitch(mc.thePlayer.rotationPitch = rots[1]);
                        mc.gameSettings.keyBindForward.pressed = true;
                        mc.gameSettings.keyBindJump.pressed = true;
                    }
                }
                break;
            }
        }
    };

    private float[] doRotations(Entity entity) {
        // new better rots
        final double posX = entity.posX - mc.thePlayer.posX;
        final double posY = entity.posY + entity.getEyeHeight() - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight() + 0.1);
        final double posZ = entity.posZ - mc.thePlayer.posZ;
        final float yaw = (float) (Math.atan2(posZ, posX) * 180.0 / Math.PI) - 90.0f;
        final double posMulti = MathHelper.sqrt_double(posX * posX + posZ * posZ);
        final float pitch = (float) (-(Math.atan2(posY, posMulti) * 180.0 / Math.PI));
        return new float[]{yaw, pitch};
    }

    private float[] rotationToSpawn() {
        final double posX = 0 - mc.thePlayer.posX;
        final double posZ = 0 - mc.thePlayer.posZ;
        final float yaw = (float) (Math.atan2(posZ, posX) * 180.0 / Math.PI) - 90.0f;
        return new float[] {yaw, mc.thePlayer.rotationPitch};
    }

    private List<EntityLivingBase> getTargets() {
        List<EntityLivingBase> entityLivingBases = new ArrayList<>();
        for (Entity e : mc.theWorld.loadedEntityList) {
            if (e instanceof EntityLivingBase) {
                EntityLivingBase entity = (EntityLivingBase) e;
                if (entity.isDead ||
                        entity.getHealth() == 0 ||
                        (entity.isInvisible() && !invis.getValue()) ||
                        (!entity.canEntityBeSeen(mc.thePlayer) && !walls.getValue()) ||
                        entity == mc.thePlayer ||
                        mc.thePlayer.getDistanceToEntity(entity) > Sreach.getValue() ||
                        (entity instanceof EntityAnimal && !animals.getValue()) ||
                        (entity instanceof EntityMob && !monsters.getValue()) ||
                        (entity instanceof EntityPlayer && !players.getValue())) {
                    continue;
                }
                entityLivingBases.add(entity);
            }
        }

        return entityLivingBases;
    }

    private enum Mode {
        LEGIT, RAGE
    }

}
