package cc.sleek.client.module.impl.player;


import cc.sleek.client.event.impl.SafewalkEvent;
import cc.sleek.client.event.impl.UpdateEvent;
import cc.sleek.client.module.Module;
import cc.sleek.client.module.api.Category;
import cc.sleek.client.module.api.ModuleInfo;
import cc.sleek.client.property.impl.BooleanValue;
import cc.sleek.client.property.impl.NumberValue;
import cc.sleek.client.util.ChatUtil;
import cc.sleek.client.util.IPacketUtil;
import cc.sleek.client.util.PlayerUtil;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.Priorities;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

import java.util.Arrays;
import java.util.List;

@ModuleInfo(name = "Scaffold", description = "Allows you to walk on scaffolds", category = Category.PLAYER)
public class Scaffold extends Module {


    private final BooleanValue keepY = new BooleanValue("Keep Y", false);
    private final BooleanValue safewalk = new BooleanValue("Safewalk", false);
    private final BooleanValue sprint = new BooleanValue("Sprint", false);
    private final BooleanValue swap = new BooleanValue("Swap Item", true);
    private final BooleanValue customSpeed = new BooleanValue("Use Custom Speed", false);
    private final NumberValue<Float> customSpod = new NumberValue<>("Custom Speed", 0.22F, 0.05F, 2.0F, 0.1F);
    private final List<Block> invalidBlocks;
    private int slot;
    private BlockDataOld data = null;
    private int y;
    @EventLink(Priorities.HIGH)
    Listener<UpdateEvent> updateEventListener = event -> {
        y = keepY.getValue() ? PlayerUtil.isOnGround() || !mc.thePlayer.isMoving() ? (int) (mc.thePlayer.posY - 1) : y : (int) (mc.thePlayer.posY - 1);
        if (customSpeed.getValue()) {
            PlayerUtil.setSpeed(customSpod.getValue());
        }
        mc.thePlayer.setSprinting(sprint.getValue());
        if (event.isPre()) {
            if (data == null) {
                event.setPitch(90.0F);
            }
            final BlockPos block = new BlockPos(mc.thePlayer.posX, y, mc.thePlayer.posZ);
            if (mc.theWorld.getBlockState(block).getBlock() == Blocks.air) {
                data = getBlockData(block);
            }
            if (data != null) {
                float[] rotations = getBlockRotations(data.pos, data.face);
                event.setYaw(rotations[0]);
                event.setPitch(rotations[1]);
            }
        } else {
            if (data != null) {
                if (swap.getValue() && (mc.thePlayer.getHeldItem() == null || !mc.thePlayer.getHeldItem().getItem().getUnlocalizedName().startsWith("tile"))) {

                    for (short i = 0; i < 9; i++) { // short bc yes
                        if (mc.thePlayer.inventory.getStackInSlot(i) != null) {
                            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
                            if (stack.getItem().getUnlocalizedName().startsWith("tile")) {
                                mc.thePlayer.inventory.currentItem = i;
                                mc.playerController.updateController();
//                                mc.thePlayer.inventory.setCurrentItem();
                                break;
                            }
                        }
                    }
                }
                if (mc.thePlayer.ticksExisted % 3 == 0) {
                    if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), data.pos, data.face, getVec3(data))) {
                        mc.thePlayer.swingItem();
                    }
                }
            }
        }


    };

    @EventLink
    Listener<SafewalkEvent> safewalkEventListener = event -> event.setSafewalk(safewalk.getValue());

    public void onEnable() {
        slot = mc.thePlayer.inventory.currentItem;
        data = null;
    }

    @Override
    public void onDisable() {
        if (swap.getValue()) {
            mc.thePlayer.inventory.currentItem = slot;
            mc.playerController.updateController();
        }
    }

    public Scaffold() {
        this.invalidBlocks = Arrays.asList(Blocks.enchanting_table, Blocks.furnace, Blocks.carpet, Blocks.crafting_table, Blocks.trapped_chest, Blocks.chest, Blocks.dispenser, Blocks.air, Blocks.water, Blocks.lava, Blocks.flowing_water, Blocks.flowing_lava, Blocks.sand, Blocks.snow_layer, Blocks.torch, Blocks.anvil, Blocks.jukebox, Blocks.stone_button, Blocks.wooden_button, Blocks.lever, Blocks.noteblock, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.wooden_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.stone_slab, Blocks.wooden_slab, Blocks.stone_slab2, Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.yellow_flower, Blocks.red_flower, Blocks.anvil, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.cactus, Blocks.ladder, Blocks.web, Blocks.gravel, Blocks.tnt);
        this.slot = -1;
    }

    private float[] getBlockRotations(BlockPos blockPos, EnumFacing enumFacing) {
        if (blockPos == null && enumFacing == null) {
            return null;
        } else {
            Vec3 positionEyes = mc.thePlayer.getPositionEyes(2.0F);
            Vec3 add = (new Vec3((double) blockPos.getX() + 0.5D, (double) blockPos.getY() + 0.5D, (double) blockPos.getZ() + 0.5D));
            double n = add.xCoord - positionEyes.xCoord;
            double n2 = add.yCoord - positionEyes.yCoord;
            double n3 = add.zCoord - positionEyes.zCoord;
            return new float[]{(float) (Math.atan2(n3, n) * 180.0D / 3.141592653589793D - 90.0D), -((float) (Math.atan2(n2, (float) Math.hypot(n, n3)) * 180.0D / 3.141592653589793D))};
        }
    }

    private Vec3 getVec3(final BlockDataOld data) {
        final BlockPos pos = data.pos;
        final EnumFacing face = data.face;
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;
        x += face.getFrontOffsetX() / 2.0;
        z += face.getFrontOffsetZ() / 2.0;
        y += face.getFrontOffsetY() / 2.0;
        if (face == EnumFacing.UP || face == EnumFacing.DOWN) {
            x += this.randomNumber(0.3, -0.3);
            z += this.randomNumber(0.3, -0.3);
        } else {
            y += this.randomNumber(0.49, 0.5);
        }
        if (face == EnumFacing.WEST || face == EnumFacing.EAST) {
            z += this.randomNumber(0.3, -0.3);
        }

        if (face == EnumFacing.SOUTH || face == EnumFacing.NORTH) {
            x += this.randomNumber(0.3, -0.3);
        }
        return new Vec3(x, y, z);
    }

    private double randomNumber(final double max, final double min) {
        return Math.random() * (max - min) + min;
    }

    /**
     * Bro i dont know what this does
     *
     * @param pos
     * @return some shit
     */
    private BlockDataOld getBlockData(final BlockPos pos) {
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos.add(0, -1, 0))).getBlock())) {
            return new BlockDataOld(pos.add(0, -1, 0), EnumFacing.UP);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos.add(-1, 0, 0))).getBlock())) {
            return new BlockDataOld(pos.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos.add(1, 0, 0))).getBlock())) {
            return new BlockDataOld(pos.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos.add(0, 0, 1))).getBlock())) {
            return new BlockDataOld(pos.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos.add(0, 0, -1))).getBlock())) {
            return new BlockDataOld(pos.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos1 = pos.add(-1, 0, 0);
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos1.add(0, -1, 0))).getBlock())) {
            return new BlockDataOld(pos1.add(0, -1, 0), EnumFacing.UP);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos1.add(-1, 0, 0))).getBlock())) {
            return new BlockDataOld(pos1.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos1.add(1, 0, 0))).getBlock())) {
            return new BlockDataOld(pos1.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos1.add(0, 0, 1))).getBlock())) {
            return new BlockDataOld(pos1.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos1.add(0, 0, -1))).getBlock())) {
            return new BlockDataOld(pos1.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos2 = pos.add(1, 0, 0);
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos2.add(0, -1, 0))).getBlock())) {
            return new BlockDataOld(pos2.add(0, -1, 0), EnumFacing.UP);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos2.add(-1, 0, 0))).getBlock())) {
            return new BlockDataOld(pos2.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos2.add(1, 0, 0))).getBlock())) {
            return new BlockDataOld(pos2.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos2.add(0, 0, 1))).getBlock())) {
            return new BlockDataOld(pos2.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos2.add(0, 0, -1))).getBlock())) {
            return new BlockDataOld(pos2.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos3 = pos.add(0, 0, 1);
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos3.add(0, -1, 0))).getBlock())) {
            return new BlockDataOld(pos3.add(0, -1, 0), EnumFacing.UP);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos3.add(-1, 0, 0))).getBlock())) {
            return new BlockDataOld(pos3.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos3.add(1, 0, 0))).getBlock())) {
            return new BlockDataOld(pos3.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos3.add(0, 0, 1))).getBlock())) {
            return new BlockDataOld(pos3.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos3.add(0, 0, -1))).getBlock())) {
            return new BlockDataOld(pos3.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos4 = pos.add(0, 0, -1);
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos4.add(0, -1, 0))).getBlock())) {
            return new BlockDataOld(pos4.add(0, -1, 0), EnumFacing.UP);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos4.add(-1, 0, 0))).getBlock())) {
            return new BlockDataOld(pos4.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos4.add(1, 0, 0))).getBlock())) {
            return new BlockDataOld(pos4.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos4.add(0, 0, 1))).getBlock())) {
            return new BlockDataOld(pos4.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos4.add(0, 0, -1))).getBlock())) {
            return new BlockDataOld(pos4.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos19 = pos.add(-2, 0, 0);
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos1.add(0, -1, 0))).getBlock())) {
            return new BlockDataOld(pos1.add(0, -1, 0), EnumFacing.UP);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos1.add(-1, 0, 0))).getBlock())) {
            return new BlockDataOld(pos1.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos1.add(1, 0, 0))).getBlock())) {
            return new BlockDataOld(pos1.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos1.add(0, 0, 1))).getBlock())) {
            return new BlockDataOld(pos1.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos1.add(0, 0, -1))).getBlock())) {
            return new BlockDataOld(pos1.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos2.add(0, -1, 0))).getBlock())) {
            return new BlockDataOld(pos2.add(0, -1, 0), EnumFacing.UP);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos2.add(-1, 0, 0))).getBlock())) {
            return new BlockDataOld(pos2.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos2.add(1, 0, 0))).getBlock())) {
            return new BlockDataOld(pos2.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos2.add(0, 0, 1))).getBlock())) {
            return new BlockDataOld(pos2.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos2.add(0, 0, -1))).getBlock())) {
            return new BlockDataOld(pos2.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos3.add(0, -1, 0))).getBlock())) {
            return new BlockDataOld(pos3.add(0, -1, 0), EnumFacing.UP);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos3.add(-1, 0, 0))).getBlock())) {
            return new BlockDataOld(pos3.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos3.add(1, 0, 0))).getBlock())) {
            return new BlockDataOld(pos3.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos3.add(0, 0, 1))).getBlock())) {
            return new BlockDataOld(pos3.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos3.add(0, 0, -1))).getBlock())) {
            return new BlockDataOld(pos3.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos4.add(0, -1, 0))).getBlock())) {
            return new BlockDataOld(pos4.add(0, -1, 0), EnumFacing.UP);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos4.add(-1, 0, 0))).getBlock())) {
            return new BlockDataOld(pos4.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos4.add(1, 0, 0))).getBlock())) {
            return new BlockDataOld(pos4.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos4.add(0, 0, 1))).getBlock())) {
            return new BlockDataOld(pos4.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos4.add(0, 0, -1))).getBlock())) {
            return new BlockDataOld(pos4.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos5 = pos.add(0, -1, 0);
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos5.add(0, -1, 0))).getBlock())) {
            return new BlockDataOld(pos5.add(0, -1, 0), EnumFacing.UP);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos5.add(-1, 0, 0))).getBlock())) {
            return new BlockDataOld(pos5.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos5.add(1, 0, 0))).getBlock())) {
            return new BlockDataOld(pos5.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos5.add(0, 0, 1))).getBlock())) {
            return new BlockDataOld(pos5.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos5.add(0, 0, -1))).getBlock())) {
            return new BlockDataOld(pos5.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos6 = pos5.add(1, 0, 0);
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos6.add(0, -1, 0))).getBlock())) {
            return new BlockDataOld(pos6.add(0, -1, 0), EnumFacing.UP);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos6.add(-1, 0, 0))).getBlock())) {
            return new BlockDataOld(pos6.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos6.add(1, 0, 0))).getBlock())) {
            return new BlockDataOld(pos6.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos6.add(0, 0, 1))).getBlock())) {
            return new BlockDataOld(pos6.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos6.add(0, 0, -1))).getBlock())) {
            return new BlockDataOld(pos6.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos7 = pos5.add(-1, 0, 0);
        if (!invalidBlocks.contains(mc.theWorld.getBlockState((pos7.add(0, -1, 0))).getBlock())) {
            return new BlockDataOld(pos7.add(0, -1, 0), EnumFacing.UP);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState(pos7.add(-1, 0, 0)).getBlock())) {
            return new BlockDataOld(pos7.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState(pos7.add(1, 0, 0)).getBlock())) {
            return new BlockDataOld(pos7.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState(pos7.add(0, 0, 1)).getBlock())) {
            return new BlockDataOld(pos7.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState(pos7.add(0, 0, -1)).getBlock())) {
            return new BlockDataOld(pos7.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos8 = pos5.add(0, 0, 1);
        if (!invalidBlocks.contains(mc.theWorld.getBlockState(pos8.add(0, -1, 0)).getBlock())) {
            return new BlockDataOld(pos8.add(0, -1, 0), EnumFacing.UP);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState(pos8.add(-1, 0, 0)).getBlock())) {
            return new BlockDataOld(pos8.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState(pos8.add(1, 0, 0)).getBlock())) {
            return new BlockDataOld(pos8.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState(pos8.add(0, 0, 1)).getBlock())) {
            return new BlockDataOld(pos8.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState(pos8.add(0, 0, -1)).getBlock())) {
            return new BlockDataOld(pos8.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos9 = pos5.add(0, 0, -1);
        if (!invalidBlocks.contains(mc.theWorld.getBlockState(pos9.add(0, -1, 0)).getBlock())) {
            return new BlockDataOld(pos9.add(0, -1, 0), EnumFacing.UP);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState(pos9.add(-1, 0, 0)).getBlock())) {
            return new BlockDataOld(pos9.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState(pos9.add(1, 0, 0)).getBlock())) {
            return new BlockDataOld(pos9.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState(pos9.add(0, 0, 1)).getBlock())) {
            return new BlockDataOld(pos9.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!invalidBlocks.contains(mc.theWorld.getBlockState(pos9.add(0, 0, -1)).getBlock())) {
            return new BlockDataOld(pos9.add(0, 0, -1), EnumFacing.SOUTH);
        }
        return null;
    }

    private boolean isValidItem(final Item item) {
        if (item instanceof ItemBlock) {
            final ItemBlock iBlock = (ItemBlock) item;
            final Block block = iBlock.getBlock();
            return !this.invalidBlocks.contains(block);
        }
        return false;
    }

    private static class BlockDataOld {
        public final BlockPos pos;
        public final EnumFacing face;

        private BlockDataOld(final BlockPos pos, final EnumFacing face) {
            this.pos = pos;
            this.face = face;
        }
    }

    public BooleanValue getSprint() {
        return sprint;
    }
}
