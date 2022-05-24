package cc.sleek.client.module.impl.combat;

import cc.sleek.client.Sleek;
import cc.sleek.client.event.impl.PacketEvent;
import cc.sleek.client.event.impl.UpdateEvent;
import cc.sleek.client.module.Module;
import cc.sleek.client.module.api.Category;
import cc.sleek.client.module.api.ModuleInfo;
import cc.sleek.client.property.impl.BooleanValue;
import cc.sleek.client.property.impl.EnumValue;
import cc.sleek.client.property.impl.NumberValue;
import cc.sleek.client.util.IPacketUtil;
import cc.sleek.client.util.Stopwatch;
import cc.sleek.client.util.StringUtil;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.*;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@ModuleInfo(
        name = "KillAura",
        description = "Automatically attacks targets",
        category = Category.COMBAT
)
public class KillAura extends Module {

    public static EntityLivingBase target;
    public static EntityLivingBase lastTarget;
    private static float spinbot = 0; // dont cry
    private final EnumValue<AuraMode> modeValue = new EnumValue<>("Mode", AuraMode.values());

    private final NumberValue<Integer> switchDelay = new NumberValue<>("Target Switch Delay", 250, 1, 10000, 1);


    private final EnumValue<SwingMode> swingMode = new EnumValue<>("Swing Mode", SwingMode.values());
    private final EnumValue<AttackMode> attackMode = new EnumValue<>("Attack Method", AttackMode.values());
    private final NumberValue<Integer> cps = new NumberValue<>("CPS", 10, 3, 20, 1);
    private final NumberValue<Double> reach = new NumberValue<>("Reach", 3.0, 0.0, 6.0, 0.1);
    private final EnumValue<AttackState> attackState = new EnumValue<>("Attack State", AttackState.values());
    private final EnumValue<Rotations> rotationMode = new EnumValue<>("Rotations", Rotations.values());
    private final BooleanValue hvh = new BooleanValue("HvH", true);
    private final BooleanValue players = new BooleanValue("Players", true);
    private final BooleanValue invis = new BooleanValue("Invisibles", true);
    private final BooleanValue animals = new BooleanValue("Animals", true);
    private final BooleanValue monsters = new BooleanValue("Monsters", true);
    private final BooleanValue walls = new BooleanValue("Walls", true);
    private final BooleanValue autoblock = new BooleanValue("Autoblock", false);
    private final EnumValue<AutoblockMode> autoblockMode = new EnumValue<>("Autoblock Mode", AutoblockMode.values(), autoblock::getValue);
    private final EnumValue<AutoBlockState> autoblockState = new EnumValue<>("When Autoblock", AutoBlockState.values(), autoblock::getValue);
    private final BooleanValue sprintCheck = new BooleanValue("Sprint Check", true);
    private final BooleanValue noEntAction = new BooleanValue("Action Check", true);
    private final BooleanValue gcd = new BooleanValue("Follow GCD", true);
    private final Stopwatch watch = new Stopwatch();
    private final Stopwatch switchTimer = new Stopwatch();
    @EventLink
    private final Listener<PacketEvent> eventListener = event -> {
        if (noEntAction.getValue() && event.getPacket() instanceof C0BPacketEntityAction) {
            event.setCancelled(true);
        }

        if (gcd.getValue() && target != null && event.getPacket() instanceof C03PacketPlayer && ((C03PacketPlayer) event.getPacket()).getRotating()) {
            C03PacketPlayer p = event.getPacket();
            float m = (float) (0.005 * mc.gameSettings.mouseSensitivity / 0.005);
            double gcd = m * m * m * 1.2;
            p.pitch -= p.pitch % gcd;
            p.yaw -= p.yaw % gcd;
        }
    };
    int switchState;
    //used for displaying the blocking animation
    private boolean shouldBlock;
    private boolean hasBeenBlocking;
    @EventLink
    Listener<UpdateEvent> updateEventListener = event -> {
        setSuffix(StringUtil.getModeName(modeValue.getValue().name()));

        if (target != null)
            lastTarget = target;

        target = null;


        //fetch all possible targets
        List<EntityLivingBase> entityLivingBases = getTargets();

        //sort them by distance
//        entityLivingBases.sort(Comparator.comparingInt(e -> (int) mc.thePlayer.getDistanceToEntity(e)));
        entityLivingBases.sort(Comparator.comparingInt(e -> e.hurtTime));

        if (!entityLivingBases.isEmpty()) {
            //target switching (switch aura)
            if (switchTimer.timeElapsed(modeValue.getValue() == AuraMode.SWITCH ? switchDelay.getValue().longValue() : 20L)) {
                if (switchState <= entityLivingBases.size()) {
                    switchState++;
                } else {
                    switchState = 0;
                }
                switchTimer.resetTime();
            }

            switch (modeValue.getValue()) {
                case SWITCH:
                case MULTI:
                    try {
                        target = entityLivingBases.get(switchState);
                    } catch (Exception e) {
                        target = entityLivingBases.get(0);
                    }
                    break;
                case SINGLE:
                    target = entityLivingBases.get(0);
                    break;
            }
        }

        if (target == null) {
            shouldBlock = false;

            if (hasBeenBlocking && (autoblockMode.getValue() == AutoblockMode.VANILLA || autoblockMode.getValue() == AutoblockMode.HVH_HYPIXEL)) {
                IPacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                hasBeenBlocking = false;
            }
            return;
        }


        //check if we should display the blocking animation
        shouldBlock = autoblock.getValue();

        //set the rotation
        float[] rotations = getRotations(target);
        event.setYaw(rotations[0]);
        event.setPitch(rotations[1]);

        if ((event.isPre() && attackState.getValue() == AttackState.PRE) || (!event.isPre() && attackState.getValue() == AttackState.POST)) {

            //get randomized cps
            int minCps = cps.getValue() > 4 ? -3 : 1; // dont ask just shutup and let it work
            int cps = ThreadLocalRandom.current().nextInt(minCps, 4) + this.cps.getValue();

            if ((target.hurtTime <= 5 && hvh.getValue()) || watch.timeElapsed(1000 / cps)) {
                preSwing();
                swingItem();
                preHit();
                attackTarget(target);
                afterHit();
                watch.resetTime();
            }
        }

    };

    @Override
    public void onEnable() {
        shouldBlock = false;


        switchState = 0;
    }

    @Override
    public void onDisable() {
        shouldBlock = false;
        target = null;
    }

    private void preSwing() {
        if (autoblock.getValue()) {
            if (autoblockMode.getValue() == AutoblockMode.HVH_HYPIXEL) {
                unBlock();
            }
        }

    }

    private void preHit() {
        if (autoblock.getValue()) {
            if (autoblockState.getValue() == AutoBlockState.PRE) {
                doBlock();
            }

        }
        if (sprintCheck.getValue()) {
            IPacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
        }

        Criticals crits = Sleek.INSTANCE.getModuleManager().getModuleByName("Criticals");
        if (crits != null && crits.isToggled()) {
            mc.thePlayer.onGround = false;
        }
    }

    private void doBlock() {
        autoblockMode.getValue().retardCode.block();
    }

    private void unBlock() {
        IPacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
    }

    private void afterHit() {
        if (sprintCheck.getValue()) {
            IPacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
        }
        if (autoblock.getValue() && autoblockState.getValue() == AutoBlockState.POST) {
            doBlock();
        }
    }

    private void swingItem() {
        switch (swingMode.getValue()) {
            case CLIENT:
                mc.thePlayer.swingItem();
                break;

            case SERVER:
                IPacketUtil.sendPacketNoEvent(new C0APacketAnimation());
                break;
        }
    }

    private void attackTarget(EntityLivingBase target) {
        switch (attackMode.getValue()) {
            case PACKET:
                IPacketUtil.sendPacketNoEvent(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
                break;
            case CLIENT:
                mc.playerController.attackEntity(mc.thePlayer, target);
                break;
        }
    }

    private float[] getRotations(EntityLivingBase entity) {

        return rotationMode.getValue().rot.doRotation(entity); // "shitty code" cry about it
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
                        mc.thePlayer.getDistanceToEntity(entity) > reach.getValue() ||
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

    public boolean isShouldBlock() {
        return shouldBlock;
    }

    private enum Rotations {
        DEFAULT(entity -> {
            // new better rots
            final double posX = entity.posX - mc.thePlayer.posX;
            final double posY = entity.posY + entity.getEyeHeight() - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight() + 0.1);
            final double posZ = entity.posZ - mc.thePlayer.posZ;
            final float yaw = (float) (Math.atan2(posZ, posX) * 180.0 / Math.PI) - 90.0f;
            final double posMulti = MathHelper.sqrt_double(posX * posX + posZ * posZ);
            final float pitch = (float) (-(Math.atan2(posY, posMulti) * 180.0 / Math.PI));
            return new float[]{yaw, pitch};
        }),
        CRAZY(entity -> {
            spinbot += 45;
            if (spinbot > 180F) {
                spinbot = -180F;
            }
            return new float[]{spinbot, 90};
        }),
        NONE(entity -> new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch}),
        DOWN((entity) -> new float[]{mc.thePlayer.rotationYaw, 89.9f});


        final Rotation rot;

        Rotations(Rotation rot) {
            this.rot = rot;
        }

        private interface Rotation {
            float[] doRotation(EntityLivingBase entity);
        }

    }

    private enum AutoblockMode {
        FAKE(() -> {
        }), NCP(() -> {
            IPacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
            IPacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
        }), HVH_HYPIXEL(() -> {
            IPacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
        }), VANILLA(() -> IPacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem())));
        private final Block retardCode;

        AutoblockMode(Block retardCode) {
            this.retardCode = retardCode;
        }

        private interface Block {
            void block();
        }
    }

    private enum AutoBlockState {
        PRE, POST
    }

    private enum AttackState {
        PRE, POST
    }

    private enum SwingMode {
        CLIENT, SERVER, NONE
    }

    private enum AttackMode {
        PACKET, CLIENT
    }

    private enum AuraMode {
        SWITCH, MULTI, SINGLE
    }
}
