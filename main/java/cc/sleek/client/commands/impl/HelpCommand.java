package cc.sleek.client.commands.impl;

import cc.sleek.client.Sleek;
import cc.sleek.client.commands.api.Command;
import cc.sleek.client.commands.api.CommandInfo;
import cc.sleek.client.util.ChatUtil;

import java.util.Arrays;
import cc.sleek.client.commands.api.Command;
import cc.sleek.client.commands.api.CommandInfo;
import cc.sleek.client.commands.manager.CommandManager;
import cc.sleek.client.util.ChatUtil;

//import static cc.sleek.client.commands.manager.CommandManager.commands;


@CommandInfo(name = "help", description = "Lists all commands and usage", usage = ".help")
public class HelpCommand extends Command {

    @Override
    public void onCommand(String[] args) {
        for (Command c : Sleek.INSTANCE.getCommandManager().getCommands()) {
            ChatUtil.log(String.format("%s - %s (%s) %s", c.getName(), c.getDescription(), c.getUsage(), c.getAliases().length > 0 ? Arrays.toString(c.getAliases()) : ""));
        }
    }
}