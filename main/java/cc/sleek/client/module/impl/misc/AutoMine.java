package cc.sleek.client.module.impl.misc;

import cc.sleek.client.event.impl.PacketEvent;
import cc.sleek.client.event.impl.Render2DEvent;
import cc.sleek.client.event.impl.UpdateEvent;
import cc.sleek.client.module.Module;
import cc.sleek.client.module.api.Category;
import cc.sleek.client.module.api.ModuleInfo;
import cc.sleek.client.util.ChatUtil;
import cc.sleek.client.util.RenderUtils;
import cc.sleek.client.util.ScoreboardUtil;
import com.mojang.authlib.GameProfile;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.block.BlockAir;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S3DPacketDisplayScoreboard;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.BlockPos;
import org.apache.commons.lang3.RandomUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Kansio
 */
@ModuleInfo(name = "AutoMine", description = "Automatically mines ores for an unban on viper :)", category = Category.MISC)
public class AutoMine extends Module {

    private BlockPos lastMined = null;
    private final BlockPos lastSpot = null;

    private int blocksBrokenThisSession = 0;
    private int blocksMinedTotal;

    private final int needToMine = 3000;

    @Override
    public void onEnable() {
        EntityOtherPlayerMP fakePlayer = new EntityOtherPlayerMP(mc.theWorld, new GameProfile(UUID.randomUUID(), "§c§lAttack me with target strafe"));
        fakePlayer.copyLocationAndAnglesFrom(mc.thePlayer);
        mc.theWorld.addEntityToWorld(58438, fakePlayer);
    }

    @Override
    public void onDisable() {
        mc.theWorld.removeEntityFromWorld(58438);
    }

    @EventLink
    private final Listener<Render2DEvent> renderOverlay = event -> {
        mc.fontRendererObj.drawStringWithShadow("§7Mined blocks: §e" + blocksMinedTotal + "/" + needToMine, RenderUtils.getResolution().getScaledWidth() / 2, 30, 0xFFFFFF);
        mc.fontRendererObj.drawStringWithShadow("§7This session: §e" + blocksBrokenThisSession, RenderUtils.getResolution().getScaledWidth() / 2, 40, 0xFFFFFF);

        if (findNearestCoal() == null)
        mc.fontRendererObj.drawStringWithShadow("§c§lNo coal in range", RenderUtils.getResolution().getScaledWidth() / 2, RenderUtils.getResolution().getScaledHeight() / 2, 0xFFFFFF);
    };


    @EventLink
    private final Listener<PacketEvent> packetEventListener = event -> {
        Packet packet = event.getPacket();
        if (event.getPacket() instanceof S02PacketChat) {
            if (((S02PacketChat) event.getPacket()).getChatComponent().getUnformattedText().contains("Jail Credit")) {
                blocksBrokenThisSession++;
            }
        }
    };


    @EventLink
    private final Listener<UpdateEvent> updateEventListener = event -> {
        BlockPos nearestCoal = findNearestCoal();

        //speed mine
        mc.playerController.blockHitDelay = 0;

        if (mc.playerController.curBlockDamageMP > 1 - 0.5) {
            mc.playerController.curBlockDamageMP = 1;
        }

        if (nearestCoal == null) {
            ChatUtil.log("no coal found...");
            teleportToNextCoal();
            return;
        }


        mc.playerController.onPlayerDamageBlock(nearestCoal, mc.thePlayer.getHorizontalFacing());
        lastMined = nearestCoal;

        Scoreboard scoreboard = mc.theWorld.getScoreboard();
        if (scoreboard == null) {
            return;
        }


        List<String> sidebarScores = ScoreboardUtil.getSidebarScores(scoreboard);
        for (String sidebarScore : sidebarScores) {
            if (sidebarScore.contains("Your Credits")) {
                int shit = Integer.parseInt(sidebarScore.replace("§bYour Credits§7: §3", ""));
                blocksMinedTotal = shit;
            }
        }
    };

    public void teleportToNextCoal() {

    }

    public BlockPos findNearestCoal() {
        BlockPos foundBlock = null;
        for (float x = 6; x >= -6; x -= 1.0f) {
            for (float y = 6; y >= -6; y -= 1.0f) {
                for (float z = 6; z >= -6; z -= 1.0f) {
                    BlockPos pos = new BlockPos(mc.thePlayer.posX + (double)x, mc.thePlayer.posY + (double)y, mc.thePlayer.posZ + (double)z);
                    double dist = mc.thePlayer.getDistance(pos.getX(), pos.getY(), pos.getZ());

                    if (lastMined == pos)
                        continue;

                    //found coal!
                    if (mc.theWorld.getBlockState(pos).getBlock() == Blocks.coal_ore) {
                        foundBlock = pos;
                    }
                }
            }
        }

        return foundBlock;
    }

}
