package cc.sleek.client.commands.api;

import net.minecraft.client.Minecraft;

public abstract class Command {

    private String name;
    private String description;
    private String usage;
    private String[] aliases;
    public Minecraft mc = Minecraft.getMinecraft();

    public Command(String name, String description, String[] aliases, String usage) {
        this.name = name;
        this.description = description;
        this.usage = usage;
        this.aliases = aliases;
    }

    public Command() {
        name = getClass().getAnnotation(CommandInfo.class).name();
        aliases = getClass().getAnnotation(CommandInfo.class).aliases();
        description = getClass().getAnnotation(CommandInfo.class).description();
        usage = getClass().getAnnotation(CommandInfo.class).usage();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public String[] getAliases() {
        return aliases;
    }

    public void setAliases(String[] aliases) {
        this.aliases = aliases;
    }

    public abstract void onCommand(String[] args);

}
