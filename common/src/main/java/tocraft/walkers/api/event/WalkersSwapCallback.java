package tocraft.walkers.api.event;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public interface WalkersSwapCallback {
    Event<WalkersSwapCallback> EVENT = EventFactory.createEventResult(WalkersSwapCallback.class);

    EventResult swap(ServerPlayerEntity player, @Nullable LivingEntity to);
}
