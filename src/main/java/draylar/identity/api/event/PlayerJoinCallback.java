package draylar.identity.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * This event is called when a player joins the server.
 */
public interface PlayerJoinCallback {
    Event<PlayerJoinCallback> EVENT = EventFactory.createArrayBacked(PlayerJoinCallback.class,
            (listeners) -> (player) -> {
                for (PlayerJoinCallback event : listeners) {
                    event.onPlayerJoin(player);
                }
            }
    );

    void onPlayerJoin(ServerPlayerEntity player);
}

