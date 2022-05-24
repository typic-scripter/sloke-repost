package cc.sleek.client.module.impl.misc;

import cc.sleek.client.Sleek;
import cc.sleek.client.event.impl.JoinServerEvent;
import cc.sleek.client.module.Module;
import cc.sleek.client.module.api.Category;
import cc.sleek.client.module.api.ModuleInfo;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;

@ModuleInfo(name = "Discord RPC", description = "Loads discord rpc", category = Category.MISC, visibility = false)
public class DiscordRPC extends Module {

    @Override
    public void onEnable() {
        try {
            Sleek.INSTANCE.connectRPC();
            Sleek.INSTANCE.updateRPC();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        try {
            Sleek.INSTANCE.disconnectRPC();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @EventLink
    Listener<JoinServerEvent> listener = event -> {
        Sleek.INSTANCE.updateRPC();
    };

}
