package cc.sleek.client.commands.impl;

import cc.sleek.client.Sleek;
import cc.sleek.client.commands.api.Command;
import cc.sleek.client.commands.api.CommandInfo;
import cc.sleek.client.module.Module;
import cc.sleek.client.util.ChatUtil;

@CommandInfo(name="toggle", description="Toggles module", aliases={"t"}, usage = ".toggle <module>")
public class ToggleCommand extends Command {
    @Override
    public void onCommand(String[] args) {
        String modName = args[0].replace('_', ' ');
        Module mod = Sleek.INSTANCE.getModuleManager().getModuleByName(modName);
        if (mod != null) {
            mod.toggle();
            ChatUtil.log(String.format("Toggled '%s'", modName));
        } else {
            ChatUtil.log(String.format("Module '%s' not found", modName));
        }
    }
}
