package cc.sleek.client.gui.alt;

import cc.sleek.client.gui.alt.util.AltLoginThread;
import cc.sleek.client.util.RenderUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import org.apache.commons.lang3.RandomStringUtils;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class GuiAltManager extends GuiScreen {

    private GuiTextField usernameField;
    private PasswordField passwordField;
    private AltLoginThread altLoginThread;
    private final GuiScreen previousScreen;

    public GuiAltManager(GuiScreen previousScreen) {
        this.previousScreen = previousScreen;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtils.drawRect(0, 0, width, height, 0xFF_10_10_10);
        usernameField.drawTextBox();
        passwordField.drawTextBox();
        String status = EnumChatFormatting.GRAY + "Waiting...";
        if (altLoginThread != null) {
            status = altLoginThread.getStatus();
        }
        drawCenteredString(fontRendererObj, status, width / 2, 40, -1);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 1:
                mc.displayGuiScreen(previousScreen);
                break;
            case 0:
                altLoginThread = new AltLoginThread(usernameField.getText(), passwordField.getText());
                altLoginThread.start();
                break;
            case 2:
                String data = null;
                try {
                    data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
                } catch (UnsupportedFlavorException e) {
                    e.printStackTrace();
                }
                if (!data.contains(":")) break;
                String[] credentials = data.split(":");
                usernameField.setText(credentials[0]);
                passwordField.setText(credentials[1]);
                break;
            case 3:
                altLoginThread = null;
                altLoginThread = new AltLoginThread(RandomStringUtils.random(14, true, true), "");
                altLoginThread.start();
                break;
        }
    }

    @Override
    public void initGui() {
        int var3 = height / 4 + 24;
        buttonList.add(new GuiButton(0, width / 2 - 100, var3 + 72 + 12, 203, 20, "Login"));
        buttonList.add(new GuiButton(1, width / 2 - 100, var3 + 72 + 12 + 24, 203, 20, I18n.format("gui.cancel")));
        buttonList.add(new GuiButton(2, width / 2 - 100, var3 + 72 + 12 + 48, 203, 20, "Clipboard"));
        buttonList.add(new GuiButton(3, width / 2 - 100, var3 + 72 + 12 + 48 + 24, 203, 20, "Generate Cracked Account"));
        usernameField = new GuiTextField(var3, mc.fontRendererObj, width / 2 - 100, 60, 200, 20);
        passwordField = new PasswordField(var3, mc.fontRendererObj, width / 2 - 100, 100, 200, 20);
        usernameField.setFocused(true);
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    protected void keyTyped(char character, int key) {
        try {
            super.keyTyped(character, key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (character == '\t') {
            if (!usernameField.isFocused() && !passwordField.isFocused()) {
                usernameField.setFocused(true);
            } else {
                usernameField.setFocused(passwordField.isFocused());
                passwordField.setFocused(!usernameField.isFocused());
            }
        }
        if (character == '\r') {
            try {
                actionPerformed(buttonList.get(0));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        usernameField.textboxKeyTyped(character, key);
        passwordField.textboxKeyTyped(character, key);
    }

    @Override
    protected void mouseClicked(int x, int y2, int button) {
        try {
            super.mouseClicked(x, y2, button);
        } catch (IOException e) {
            e.printStackTrace();
        }
        usernameField.mouseClicked(x, y2, button);
        passwordField.mouseClicked(x, y2, button);
    }
}
