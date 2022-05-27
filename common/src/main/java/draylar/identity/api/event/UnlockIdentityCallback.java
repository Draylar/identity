package draylar.identity.api.event;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;
import draylar.identity.api.variant.IdentityType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

public interface UnlockIdentityCallback {
    Event<UnlockIdentityCallback> EVENT = EventFactory.createEventResult(UnlockIdentityCallback.class);

    EventResult unlock(ServerPlayerEntity player, IdentityType type);
}
