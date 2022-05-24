package cc.sleek.client.commands.impl;

import cc.sleek.client.Sleek;
import cc.sleek.client.commands.api.Command;
import cc.sleek.client.commands.api.CommandInfo;
import cc.sleek.client.config.Config;
import cc.sleek.client.config.ConfigManager;
import cc.sleek.client.util.ChatUtil;
import org.apache.commons.io.FilenameUtils;

@CommandInfo(name = "config", description = "Configure Sleek", usage = ".config load <config> | .config save <name> | .config list"
)
public class ConfigCommand extends Command {

    @Override
    public void onCommand(String[] args) {
        switch (args[0]) {
            case "load":
                try {
                    Sleek.INSTANCE.getConfigManager().loadConfig(args[1]);
                    ChatUtil.log("Loaded config", args[1]);
                } catch (Exception e) {
                    ChatUtil.log("Config not found");
                }
                break;
            case "save":
                try {
                    Sleek.INSTANCE.getConfigManager().saveConfig(args[1]);
                    ChatUtil.log("Saved config", args[1]);
                } catch (Exception e) {
                    ChatUtil.log("Config not found");
                }
                break;
            case "list":
                for (Config c : Sleek.INSTANCE.getConfigManager().getConfigs()) {
                    System.out.println(c.getFile().toString());
                    ChatUtil.log("-", FilenameUtils.getName(c.getFile().getName()));
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid argument");
        }
    }
}
