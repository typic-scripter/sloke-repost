package cc.sleek.client.commands.impl;

import cc.sleek.client.commands.api.Command;
import cc.sleek.client.commands.api.CommandInfo;

@CommandInfo(
        name = "gc",
        description = "runs java garbage collector",
        usage = ".gc"
)
public class GCCommand extends Command {
    @Override
    public void onCommand(String[] args) {
        System.gc();
    }
}
