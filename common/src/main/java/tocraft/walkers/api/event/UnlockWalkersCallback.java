package tocraft.walkers.api.event;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;
import tocraft.walkers.api.variant.ShapeType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

public interface UnlockWalkersCallback {
    Event<UnlockWalkersCallback> EVENT = EventFactory.createEventResult(UnlockWalkersCallback.class);

    EventResult unlock(ServerPlayerEntity player, ShapeType type);
}
