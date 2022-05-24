package cc.sleek.client;

import cc.sleek.client.commands.manager.CommandManager;
import cc.sleek.client.config.ConfigManager;
import cc.sleek.client.elements.HudPropertyApi;
import cc.sleek.client.elements.PropertyScreen;
import cc.sleek.client.elements.targethud.TargetHudRenderer;
import cc.sleek.client.event.Event;
import cc.sleek.client.event.impl.KeyboardEvent;
import cc.sleek.client.event.impl.PacketEvent;
import cc.sleek.client.event.impl.PlayerKillEvent;
import cc.sleek.client.module.Module;
import cc.sleek.client.module.impl.combat.KillAura;
import cc.sleek.client.module.manager.ModuleManager;
import cc.sleek.client.util.ChatUtil;
import cc.sleek.client.util.ServerUtils;
import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.entities.DiscordBuild;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.exceptions.NoDiscordClientException;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import io.github.nevalackin.homoBus.bus.impl.EventBus;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S02PacketChat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import viamcp.ViaMCP;

import java.io.File;
import java.time.OffsetDateTime;

/**
 * @author Kansio
 */
public enum Sleek {

    INSTANCE;

    private File dir;
    private final EventBus<Event> eventBus = new EventBus<>();
    private ModuleManager moduleManager;
    private CommandManager commandManager;
    private HudPropertyApi elementManager;
    private ConfigManager configManager;
    private final String version = "1.0";
    private final IPCClient client = new IPCClient(920326501348560926L);
    private static final Logger LOGGER = LogManager.getLogger();


    public void onStart() {
        // Set the directory
        dir = new File(Minecraft.getMinecraft().mcDataDir, "Sleek");
        // inits module manager
        moduleManager = new ModuleManager(dir);
        // inits command manager
        commandManager = new CommandManager();
        // reg da commandos
        commandManager.registerCommands();
        // init config manager
        configManager = new ConfigManager(dir);

        // inits element manager
        elementManager = HudPropertyApi.newInstance();

        //registers elements
        elementManager.register(new TargetHudRenderer());

        // subscribe to the event bus
        eventBus.subscribe(this);
        // start viaversion
        try {
            ViaMCP.getInstance().start();
            ViaMCP.getInstance().initAsyncSlider(); // For top left aligned slider
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventLink
    Listener<KeyboardEvent> keyboardEventListener = event -> {
        if (event.getKeyCode() == Keyboard.KEY_INSERT) {
            Minecraft.getMinecraft().displayGuiScreen(new PropertyScreen(elementManager));
        }

        if (event.getKeyCode() == Keyboard.KEY_HOME) {
            PlayerKillEvent killEvent = new PlayerKillEvent(Minecraft.getMinecraft().thePlayer);
            eventBus.post(killEvent);
        }

        moduleManager.getModules().stream().filter(module -> module.getKeybind() == event.getKeyCode()).forEach(Module::toggle);
    };

    @EventLink
    private final Listener<PacketEvent> packetEventListener = event -> {
        if (event.getPacket() instanceof S02PacketChat) {
            if (Minecraft.getMinecraft().thePlayer == null) return;
            S02PacketChat chatPacket = event.getPacket();
            try {
                ServerUtils.KILL_MESSAGES.forEach(it -> {
                    if (chatPacket.getChatComponent().getUnformattedText().contains(it) && chatPacket.getChatComponent().getUnformattedText().contains(Minecraft.getMinecraft().thePlayer.getName())) {
                        PlayerKillEvent killEvent = new PlayerKillEvent((EntityPlayer) KillAura.lastTarget);
                        eventBus.post(killEvent);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public void onStop() {
        moduleManager.saveModules();
    }

    public EventBus<Event> getEventBus() {
        return eventBus;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public String getVersion() {
        return version;
    }

    public File getDir() {
        return dir;
    }

    public void setDir(File dir) {
        this.dir = dir;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public void setModuleManager(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public void setCommandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    public HudPropertyApi getElementManager() {
        return elementManager;
    }

    public void setElementManager(HudPropertyApi elementManager) {
        this.elementManager = elementManager;
    }

    public void connectRPC() {
        try {
            client.connect(DiscordBuild.ANY);
        } catch (NoDiscordClientException e) {
            LOGGER.error("Failed to start discord rpc", e);
        }
    }

    public void updateRPC() {
        client.sendRichPresence(new RichPresence.Builder()
                .setState(ServerUtils.getServer())
                .setDetails("Divine [1]") // TODO: unhardcode when auth is added
                .setLargeImage("sleeks")
                .setSmallImage("sleek_cc")
                .setStartTimestamp(OffsetDateTime.now())
                .build());
    }

    public void disconnectRPC() {
        try {
            client.close();
        } catch (Exception e) {
            LOGGER.error("Failed to close discord rpc", e);
        }
    }
}
