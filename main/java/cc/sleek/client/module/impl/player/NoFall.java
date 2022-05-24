package cc.sleek.client.module.impl.player;

import cc.sleek.client.event.Event;
import cc.sleek.client.event.impl.UpdateEvent;
import cc.sleek.client.module.Module;
import cc.sleek.client.module.api.Category;
import cc.sleek.client.module.api.ModuleInfo;
import cc.sleek.client.property.impl.EnumValue;
import cc.sleek.client.util.Player;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;

/**
 * @author Kansio
 */
@ModuleInfo(name = "NoFall", description = "Prevents you from taking fall damage", category = Category.PLAYER)
public class NoFall extends Module {

    private final EnumValue<NoFallModes> noFallMode = new EnumValue<>("Mode: ", NoFallModes.values());

    public NoFall(){
        register(noFallMode);
    }

    @EventLink
    private final Listener<UpdateEvent> updateEvent = event -> {
        switch (noFallMode.getValue()) {
            case VANILLA:
//                if (mc.thePlayer.fallDistance > 3.0F) {
                    event.setOnGround(true);
//                }
                break;
        }
    };


    private enum NoFallModes {
        VANILLA
    }

}
