package draylar.identity.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

public interface UnlockIdentityCallback {
    Event<UnlockIdentityCallback> EVENT = EventFactory.createArrayBacked(UnlockIdentityCallback.class,
            (listeners) -> (player, to) -> {
                for (UnlockIdentityCallback event : listeners) {
                    ActionResult swap = event.unlock(player, to);

                    // abort iteration early if PASS was not returned
                    if(swap != ActionResult.PASS) {
                        return swap;
                    }
                }

                return ActionResult.PASS;
            }
    );

    ActionResult unlock(ServerPlayerEntity player, Identifier id);
}
