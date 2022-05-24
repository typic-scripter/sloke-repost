package cc.sleek.client.module.manager;

import cc.sleek.client.module.Module;
import cc.sleek.client.module.api.Category;
import cc.sleek.client.module.impl.combat.*;
import cc.sleek.client.module.impl.combat.FastBow;
import cc.sleek.client.module.impl.combat.TargetStrafe;
import cc.sleek.client.module.impl.combat.Velocity;
import cc.sleek.client.module.impl.exploit.Blink;
import cc.sleek.client.module.impl.exploit.Crasher;
import cc.sleek.client.module.impl.exploit.Disabler;
import cc.sleek.client.module.impl.exploit.Transactions;
import cc.sleek.client.module.impl.misc.AntiExploit;
import cc.sleek.client.module.impl.misc.AutoMine;
import cc.sleek.client.module.impl.movement.*;
import cc.sleek.client.module.impl.player.*;
import cc.sleek.client.module.impl.combat.KillAura;
import cc.sleek.client.module.impl.misc.DiscordRPC;
import cc.sleek.client.module.impl.render.*;
import cc.sleek.client.property.Value;
import com.google.gson.*;
import org.reflections.Reflections;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ModuleManager {

    private final File file;
    private final CopyOnWriteArrayList<Module> modules = new CopyOnWriteArrayList<>();

    public ModuleManager(File parentDir) {
        registerModules();
        file = new File(parentDir, "modules.json");
        loadModules();
    }

    public void registerModules() {
        Reflections reflections = new Reflections("cc.sleek.client.module.impl");
        for (Class<? extends Module> clazz : reflections.getSubTypesOf(Module.class)) {
            System.out.println("[Reflections] Registering module: " + clazz.getSimpleName());
            registerModule(clazz);
        }

        AntiExploit antiExploit = getModuleByClass(AntiExploit.class);
        antiExploit.setToggled(true);

        DiscordRPC discordRPC = getModuleByName("Discord RPC");
        discordRPC.setToggled(true);

        HUD hud = getModuleByName("HUD");
        hud.setToggled(true);
    }

    public void registerModule(Class<? extends Module> clazz) {
        try {
            Module module = clazz.newInstance();
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                Object obj = field.get(module);
                if (obj instanceof Value) {
                    Value value = (Value) obj;
                    module.registerValues(value);
                }
            }
            modules.add(module);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Deprecated // deprecated because of the new module registration system
    public void registerModule(Module module) {
        modules.add(module);
    }

    public void unregisterModule(Module module) {
        modules.remove(module);
    }

    public void unregisterModules() {
        modules.clear();
    }

    public <T extends Module> T getModuleByName(String moduleName) {
        for (Module module : modules) {
            if (module.getName().equalsIgnoreCase(moduleName)) {
                return (T) module;
            }
        }
        return null;
    }

    public List<Module> getModulesInCategory(Category category) {
        List<Module> modules = new ArrayList<>();
        for (Module module : this.modules) {
            if (module.getCategory() == category) {
                modules.add(module);
            }
        }
        return modules;
    }

    public void saveModules() {
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            Writer writer = new FileWriter(file);
            JsonObject json = new JsonObject();
            JsonArray arr = new JsonArray();
            modules.forEach(mod -> arr.add(mod.save()));
            json.add("modules", arr);
            writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(json));
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // copilot made this
    public void loadModules() {
        if (file.exists()) {
            try {
                JsonElement element = new JsonParser().parse(new FileReader(file));
                element.getAsJsonObject().get("modules").getAsJsonArray().forEach(jsonElement -> {
                    JsonObject obj = jsonElement.getAsJsonObject(); // get the module object
                    Module module = getModuleByName(obj.get("name").getAsString()); // get the module
                    if (module != null) { // if the module exists
                        module.load(obj, true); // load the module
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // copilot made this
    public <T extends Module> T getModuleByClass(Class<T> clazz) {
        for (Module module : modules) {
            if (module.getClass() == clazz) {
                return (T) module;
            }
        }
        return null;
    }

    public CopyOnWriteArrayList<Module> getModules() {
        return modules;
    }
}
