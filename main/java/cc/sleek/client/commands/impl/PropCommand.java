package cc.sleek.client.commands.impl;

import cc.sleek.client.Sleek;
import cc.sleek.client.commands.api.Command;
import cc.sleek.client.commands.api.CommandInfo;
import cc.sleek.client.module.Module;
import cc.sleek.client.property.Value;
import cc.sleek.client.property.impl.BooleanValue;
import cc.sleek.client.property.impl.EnumValue;
import cc.sleek.client.property.impl.NumberValue;
import cc.sleek.client.property.impl.StringValue;
import cc.sleek.client.util.ChatUtil;

import java.util.Arrays;

@CommandInfo(
        name = "property",
        description = "Edits property",
        aliases = {"prop", "value", "p"},
        usage = ".prop <module> <property> <value>\n.prop <module> list"
)
public class PropCommand extends Command {
    @Override
    public void onCommand(String[] args) {

        Module mod = Sleek.INSTANCE.getModuleManager().getModuleByName(args[0].replace('_', ' '));

        switch (args[1]) {
            case "list":
                ChatUtil.log(String.format("\247f%s has %s properties", mod.getName(), mod.getValues().size()));
                for (Value value : mod.getValues()) {
                    ChatUtil.log(String.format("\247f%s \2477- \247f%s", value.getName(), value.getValue()));
                }
                break;
            default:
                Value val = mod.getValue(args[1].replace('_', ' '));
                if (val instanceof BooleanValue) {
                    val.setValue(Boolean.parseBoolean(args[2]));
                } else if (val instanceof NumberValue) {
                    if (((NumberValue<?>) val).getIncrement() instanceof Double) {
                        double value = Double.parseDouble(args[2]);
                        val.setValue(value);
                        ChatUtil.log(String.format("Set property %s to %s", val.getName(), value));
                    }
                    if (((NumberValue<?>) val).getIncrement() instanceof Float) {
                        float value = (float) Double.parseDouble(args[2]);
                        val.setValue(value);
                        ChatUtil.log(String.format("Set property %s to %s", val.getName(), value));
                    }
                    if (((NumberValue<?>) val).getIncrement() instanceof Long) {
                        long value = (long) Double.parseDouble(args[2]);
                        val.setValue(value);
                        ChatUtil.log(String.format("Set property %s to %s", val.getName(), value));
                    }
                    if (((NumberValue<?>) val).getIncrement() instanceof Integer) {
                        int value = (int) Double.parseDouble(args[2]);
                        val.setValue(value);
                        ChatUtil.log(String.format("Set property %s to %s", val.getName(), value));
                    }
                } else if (val instanceof EnumValue) {
                    String value = args[2];
                    ((EnumValue) val).setValueString(value);
                    ChatUtil.log(String.format("Set property %s to %s", val.getName(), val.getValue()));

                } else if (val instanceof StringValue) {
                    String value = args[2];
                    val.setValue(value);
                    ChatUtil.log(String.format("Set property %s to %s", val.getName(), value));
                }
                break;
        }
    }
}
