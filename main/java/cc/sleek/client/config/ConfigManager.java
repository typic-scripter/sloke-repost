package cc.sleek.client.config;

import cc.sleek.client.Sleek;
import cc.sleek.client.module.Module;
import com.google.gson.*;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;

public class ConfigManager {

    private final ArrayList<Config> configs = new ArrayList<>();
    private final File dir;

    public ConfigManager(File parentDir) {
        this.dir = new File(parentDir, "configs");
        loadConfigs();
    }

    public void loadConfigs() {
        if (!dir.exists()) {
            dir.mkdirs();
        }
        for (File file : dir.listFiles()) {
            if (FilenameUtils.isExtension(file.getName(), ".json")) {
                try {
                    JsonElement parsed = new JsonParser().parse(new FileReader(file));
                    JsonObject json = parsed.getAsJsonObject();
                    JsonObject data = json.get("data").getAsJsonObject();
                    configs.add(new Config(
                            file,
                            data.get("author").getAsString(),
                            data.get("version").getAsString(),
                            data.get("last_updated").getAsString()
                    ));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void saveConfig(String configName) {
        try {
            File file = new File(dir, configName + ".json");
            if (!file.exists()) {
                file.createNewFile();
            }
            Writer writer = new FileWriter(file);
            JsonObject obj = new JsonObject();
            JsonObject data = new JsonObject();
            data.addProperty("author", "Divine"); // TODO: hardcode until auth
            data.addProperty("version", "1.0");
            data.addProperty("last_updated", System.currentTimeMillis() / 1000L);
            obj.add("data", data);
            JsonArray modules = new JsonArray();
            Sleek.INSTANCE.getModuleManager().getModules().forEach(mod -> {
                modules.add(mod.save());
            });
            obj.add("modules", modules);
            writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(obj));
            writer.close();
            configs.add(new Config(file, "Divine", "1.0", "System.currentTimeMillis() / 1000L"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadConfig(String name) throws FileNotFoundException {
        File file = new File(dir, name + ".json");
        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        JsonElement element = new JsonParser().parse(new FileReader(file));
        JsonObject obj = element.getAsJsonObject();
        JsonArray mods = obj.get("modules").getAsJsonArray();
        mods.forEach(m -> {
            JsonObject module = m.getAsJsonObject();
            Module mod = Sleek.INSTANCE.getModuleManager().getModuleByName(module.get("name").getAsString());
            if (mod != null) {
                mod.load(module, false);

            }
        });
    }

    public ArrayList<Config> getConfigs() {
        return configs;
    }
}
