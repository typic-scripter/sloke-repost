package cc.sleek.client.util;

/**
 * @author Kansio
 */
public class MouseUtil {

    public static boolean mouseWithinBounds(int mouseX, int mouseY, float x, float y, float x1, float y1) {
        return (mouseX >= x && mouseX <= x1) && (mouseY >= y && mouseY <= y1);
    }

}
