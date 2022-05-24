package cc.sleek.client.gui.alt.util;

import cc.sleek.client.util.IUtil;
import com.mojang.authlib.Agent;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Session;
import org.apache.logging.log4j.LogManager;

import java.net.Proxy;

public class AltLoginThread extends Thread implements IUtil {

    private final String username;
    private final String password;
    private String status;

    public AltLoginThread(String name1, String password) {
        super("AltLoginThread");
        this.username = name1;
        this.password = password;
    }

    public Session createSession() {
        YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
        YggdrasilUserAuthentication authentication = new YggdrasilUserAuthentication(service, Agent.MINECRAFT);
        authentication.setUsername(username);
        authentication.setPassword(password);
        try {
            authentication.logIn();
            return new Session(authentication.getSelectedProfile().getName(), authentication.getSelectedProfile().getId().toString(), authentication.getAuthenticatedToken(), "mojang");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void run() {
        if (password.equals("")) {
            mc.session = new Session(username.replace("&", "\u00a7"), "", "", "mojang");
            status = EnumChatFormatting.GREEN + "Set username to " + username;
            return;
        }

        status = EnumChatFormatting.AQUA + "Authenticating...";
        Session session = createSession();
        if (session == null) {
            status = EnumChatFormatting.RED + "Failed";
        } else {
            status = EnumChatFormatting.GREEN + "Logged into " + session.getUsername();
            mc.session = session;
            LogManager.getLogger().info("Logged into {}", session.getUsername());
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
