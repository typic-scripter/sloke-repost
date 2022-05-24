package cc.sleek.client.util;

/**
 * @author Kansio
 */
public class StringUtil {

    public static String capitalize(String string) {
        string = string.toLowerCase();

        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    public static String getModeName(String name) {
        return String.join(" ", StringUtil.capitalize(name).replace('_', ' ').split(" "));
    }
}
