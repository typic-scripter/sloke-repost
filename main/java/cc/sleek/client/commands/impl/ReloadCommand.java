package cc.sleek.client.commands.impl;

import cc.sleek.client.Sleek;
import cc.sleek.client.commands.api.Command;
import cc.sleek.client.commands.api.CommandInfo;
import cc.sleek.client.module.Module;

@CommandInfo(name="reload", description="Reloads the client", usage=".reload")
public class ReloadCommand extends Command {

    @Override
    public void onCommand(String[] args) {
        for (Module mod : Sleek.INSTANCE.getModuleManager().getModules()) {
            if (mod.isToggled()) {
                mod.toggle();
            }
        }
        Sleek.INSTANCE.getModuleManager().unregisterModules();
        Sleek.INSTANCE.getModuleManager().registerModules();
        Sleek.INSTANCE.getModuleManager().loadModules();
    }
}
