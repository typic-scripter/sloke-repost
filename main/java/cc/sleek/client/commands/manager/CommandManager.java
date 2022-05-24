package cc.sleek.client.commands.manager;

import cc.sleek.client.Sleek;
import cc.sleek.client.commands.api.Command;
import cc.sleek.client.commands.impl.*;
import cc.sleek.client.event.impl.ChatEvent;
import cc.sleek.client.util.ChatUtil;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

public class CommandManager {

    private final CopyOnWriteArrayList<Command> commands = new CopyOnWriteArrayList<>();

    public CommandManager() {
        Sleek.INSTANCE.getEventBus().subscribe(this);
    }


    @EventLink
    Listener<ChatEvent> chatEventListener = event -> {
        if (!event.getMessage().startsWith(".")) return;
        event.setCancelled(true);
        String cmd = event.getMessage();
        String[] split = cmd.split(" ");
        String command = split[0];
        String args = cmd.substring(command.length()).trim();

        for (Command c : commands) {
            // .forEach doesnt exist for arrays
            ArrayList<String> aliases = new ArrayList<>(Arrays.asList(c.getAliases()));
            if (c.getName().equalsIgnoreCase(command.replace(".", "")) || aliases.contains(command.replace(".", ""))) {
                try {
                    c.onCommand(args.split(" "));
                } catch (Exception e) {
                    ChatUtil.log("Error: " + e.getMessage());
                    ChatUtil.log("Usage: " + c.getUsage());
                }
                return;
            }
        }
    };

    public void registerCommands() {
        commands.add(new BindCommand());
        commands.add(new ToggleCommand());
        commands.add(new PropCommand());
        commands.add(new VClipCommand());
        commands.add(new ReloadCommand());
        commands.add(new ConfigCommand());
        commands.add(new HelpCommand());
        commands.add(new GCCommand());
    }

    public CopyOnWriteArrayList<Command> getCommands() {
        return commands;
    }
}
