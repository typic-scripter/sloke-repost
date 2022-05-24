package cc.sleek.client.commands.impl;

import cc.sleek.client.commands.api.Command;
import cc.sleek.client.commands.api.CommandInfo;
import cc.sleek.client.util.ChatUtil;
import cc.sleek.client.util.IUtil;
import net.minecraft.client.Minecraft;

@CommandInfo(
        name = "vclip",
        description = "Vertical clipping",
        usage = ".vclip <amount>"
)
public class VClipCommand extends Command {
    @Override
    public void onCommand(String[] args) {
            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + Double.parseDouble(args[0]), mc.thePlayer.posZ);
    }
}
