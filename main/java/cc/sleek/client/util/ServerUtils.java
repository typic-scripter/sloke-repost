package cc.sleek.client.util;

import java.util.Arrays;
import java.util.List;

public class ServerUtils implements IUtil {

    public static List<String> KILL_MESSAGES = Arrays.asList(
            "was killed by",
            "was slain by",
            "was thrown into the void by",
            "a asesinado"
    );

    public static String getServer() {
        if (mc.isSingleplayer()) {
            return "Singleplayer";
        }
        if (mc.getCurrentServerData() != null) {
            return mc.getCurrentServerData().serverIP;
        }
        return "Menu";
    }

}
