package cc.sleek.client.module;

import cc.sleek.client.Sleek;
import cc.sleek.client.module.api.Category;
import cc.sleek.client.module.api.ModuleInfo;
import cc.sleek.client.property.Value;
import cc.sleek.client.property.impl.EnumValue;
import cc.sleek.client.util.Player;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public abstract class Module {

    private boolean toggled;
    private int keybind;
    private boolean hidden;
    private String name;
    private final Category category;
    private String description;
    private String suffix;
    public static final Minecraft mc = Minecraft.getMinecraft();
    private final ArrayList<Value<?>> values = new ArrayList<>();
    private static final transient Player player = new Player();

    public Module(String name, Category category, int keybind) {
        this.keybind = keybind;
        this.name = name;
        this.category = category;
    }

    public Module(String name, Category category) {
        this(name, category, Keyboard.KEY_NONE);
    }

    public Module() {
        name = getClass().getAnnotation(ModuleInfo.class).name();
        keybind = getClass().getAnnotation(ModuleInfo.class).bind();
        category = getClass().getAnnotation(ModuleInfo.class).category();
        description = getClass().getAnnotation(ModuleInfo.class).description();
        hidden = !getClass().getAnnotation(ModuleInfo.class).visibility();
    }

    public void onEnable() {

    }
    public void onDisable() {

    }

    @Deprecated //This doesn't do anything anymore because all the values are loaded with reflection.
    public void register(Value<?>... values) {
    }

    public void registerValues(Value<?>... values) {
        Collections.addAll(this.values, values);
    }

    public boolean isToggled() {
        return toggled;
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;

        if (toggled) {
            Sleek.INSTANCE.getEventBus().subscribe(this);
            onEnable();
        } else {
            Sleek.INSTANCE.getEventBus().unsubscribe(this);
            onDisable();
        }
    }

    public int getKeybind() {
        return keybind;
    }

    public void setKeybind(int keybind) {
        this.keybind = keybind;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }


    public void toggle() {
        toggled = !toggled;
        if (toggled) {
            Sleek.INSTANCE.getEventBus().subscribe(this);
            onEnable();
        } else {
            Sleek.INSTANCE.getEventBus().unsubscribe(this);
            onDisable();
        }
    }

    public Value getValue(String name) {
        for (Value val : values) {
            if (val.getName().equalsIgnoreCase(name)) {
                return val;
            }
        }
        return null;
    }
    public ArrayList<Value<?>> getValues() {
        return values;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public JsonObject save() {
        JsonObject json = new JsonObject();
        json.addProperty("name", name);
        json.addProperty("keybind", keybind);
        json.addProperty("toggled", toggled);
        JsonArray values = new JsonArray();
        for (Value val : this.values) {
            JsonObject value = new JsonObject();
            value.addProperty("name", val.getName());
            if (val.getValue() instanceof Boolean) {
                value.addProperty("value", (boolean) val.getValue());
            } else if (val.getValue() instanceof Integer) {
                value.addProperty("value", (int) val.getValue());
            } else if (val.getValue() instanceof Float) {
                value.addProperty("value", (float) val.getValue());
            } else if (val.getValue() instanceof Double) {
                value.addProperty("value", (double) val.getValue());
            } else if (val.getValue() instanceof String) {
                value.addProperty("value", (String) val.getValue());
            } else if (val.getValue() instanceof Enum) {
                value.addProperty("value", ((Enum) val.getValue()).name());
            }
            values.add(value);
        }
        json.add("values", values);
        return json;

    }

    public void load(JsonObject json, boolean keys) {
        json.entrySet().forEach(element -> {
            if (element.getKey().equals("name")) {
                return; // return is the same as continue for a forEach loop
            }
            if (element.getKey().equals("keybind") && keys) {
                setKeybind(json.get("keybind").getAsInt());
                return; // return is the same as continue for a forEach loop
            }
            if (element.getKey().equals("toggled")) {
                boolean toggled = json.get("toggled").getAsBoolean();
                if (!this.toggled && toggled || this.toggled && !toggled) {
                    setToggled(toggled);
                }
                return; // return is the same as continue for a forEach loop
            }
            if (element.getKey().equals("values")) {
                JsonArray values = json.get("values").getAsJsonArray();
                values.forEach(value -> {
                    JsonObject v = value.getAsJsonObject();
                    String name = v.get("name").getAsString();
                    Value val = getValue(name);
                    if (val == null) {
                        return;
                    }
                    if (val.getValue() instanceof Boolean) {
                        val.setValue(v.get("value").getAsBoolean());
                    } else if (val.getValue() instanceof Integer) {
                        val.setValue(v.get("value").getAsInt());
                    } else if (val.getValue() instanceof Float) {
                        val.setValue(v.get("value").getAsFloat());
                    } else if (val.getValue() instanceof Double) {
                        val.setValue(v.get("value").getAsDouble());
                    } else if (val.getValue() instanceof String) {
                        val.setValue(v.get("value").getAsString());
                    } else if (val.getValue() instanceof Enum) {
                        ((EnumValue)val).setValueString(v.get("value").getAsString());
                    }
                });
            }
        });
    }

    public boolean isHidden() {
        return hidden;
    }
}
