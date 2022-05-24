package cc.sleek.client.util;

import net.minecraft.util.ChatComponentText;

public class ChatUtil implements IUtil {

    public static void log(String msg) {
        if (mc.thePlayer == null) return;
        String prefix = "§7[§bSleek§7]";
        mc.thePlayer.addChatMessage(new ChatComponentText(String.format("%s %s", prefix, msg)));
    }

    public static void logNoPrefix(String msg) {
        mc.thePlayer.addChatMessage(new ChatComponentText(String.format("%s", msg)));
    }

    public static void log(Object... toLog) {
        String prefix = "§7[§bSleek§7]";
        StringBuilder msg = new StringBuilder();
        for (Object object : toLog) msg.append(String.format("%s ", object));
        mc.thePlayer.addChatMessage(new ChatComponentText(String.format("%s %s", prefix, msg)));

    }
}
