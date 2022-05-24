package cc.sleek.client.commands.impl;

import cc.sleek.client.Sleek;
import cc.sleek.client.commands.api.Command;
import cc.sleek.client.commands.api.CommandInfo;
import cc.sleek.client.module.Module;
import cc.sleek.client.util.ChatUtil;
import org.lwjgl.input.Keyboard;

@CommandInfo(
        name = "bind",
        description = "Binds mods",
        usage = ".bind [module] [key] | .bind del [module] | .bind list"
)
public class BindCommand extends Command {

    @Override
    public void onCommand(String[] args) {
        if (args[0].equalsIgnoreCase("list")) {
            ChatUtil.log("The Current Binds Are:");
            for (Module module : Sleek.INSTANCE.getModuleManager().getModules()) {
                if (module.getKeybind() != 0) {
                    ChatUtil.log(module.getName() + " - " + Keyboard.getKeyName(module.getKeybind()));
                }
            }
        } else if (args[0].equalsIgnoreCase("del")) {
            Module module = Sleek.INSTANCE.getModuleManager().getModuleByName(args[0].replace('_', ' '));
            ChatUtil.log("Deleted the bind.");
            module.setKeybind(0);
        } else if (args[0].isEmpty()) {
            throw new IllegalArgumentException("Invalid argument");
        } else {
            Module module = Sleek.INSTANCE.getModuleManager().getModuleByName(args[0].replace('_', ' '));
            if (module != null) {
                int key = Keyboard.getKeyIndex(args[1].toUpperCase());
                if (key != -1) {
                    ChatUtil.log("You've set the bind to " + Keyboard.getKeyName(key) + ".");
                    module.setKeybind(key);
                }
            }
        }
    }
}
