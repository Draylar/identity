package draylar.identity.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.jetbrains.annotations.Nullable;

public interface IdentitySwapCallback {
    Event<IdentitySwapCallback> EVENT = EventFactory.createArrayBacked(IdentitySwapCallback.class,
            (listeners) -> (player, to) -> {
                for (IdentitySwapCallback event : listeners) {
                    ActionResult swap = event.swap(player, to);
                    
                    // abort iteration early if PASS was not returned
                    if(swap != ActionResult.PASS) {
                        return swap;
                    }
                }

                return ActionResult.PASS;
            }
    );

    ActionResult swap(ServerPlayerEntity player, @Nullable LivingEntity to);
}
