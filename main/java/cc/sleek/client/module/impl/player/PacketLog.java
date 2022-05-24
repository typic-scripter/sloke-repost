package cc.sleek.client.module.impl.player;

import cc.sleek.client.event.impl.PacketEvent;
import cc.sleek.client.module.Module;
import cc.sleek.client.module.api.ModuleInfo;
import cc.sleek.client.module.api.Category;
import cc.sleek.client.property.impl.EnumValue;
import cc.sleek.client.util.ChatUtil;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;

@ModuleInfo(
        name = "PacketLog",
        description = "Logs all packets",
        category = Category.PLAYER
)
public class PacketLog extends Module {

    private final EnumValue<Mode> mode = new EnumValue<>("Mode", Mode.values());

    public PacketLog() {
        register(mode);
    }

    @EventLink
    Listener<PacketEvent> listener = event -> {
        switch (mode.getValue()) {
            case Sent:
                if (event.isSending()) {
                    String packetName = event.getPacket().getClass().getSimpleName();
                    ChatUtil.log("Sent Packet: " + packetName + " - " + event.getPacket().toString());
                }
                break;
            case Received:
                if (!event.isSending()) {
                    String packetName = event.getPacket().getClass().getSimpleName();
                    ChatUtil.log("Received packet: " + packetName + " - " + event.getPacket().toString());
                }
                break;
        }
    };

    public enum Mode {
        Sent, Received
    }
}