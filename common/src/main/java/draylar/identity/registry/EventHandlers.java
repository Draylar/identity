package draylar.identity.registry;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import draylar.identity.api.platform.IdentityConfig;
import draylar.identity.api.platform.PlayerHostility;
import draylar.identity.api.platform.PlayerIdentity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.RavagerEntity;

public class EventHandlers {

    public static void registerHostilityUpdateHandler() {
        InteractionEvent.INTERACT_ENTITY.register((player, entity, hand) -> {
            if(!player.world.isClient && entity instanceof HostileEntity) {
                PlayerHostility.set(player, IdentityConfig.getInstance().hostilityTime());
            }

            return EventResult.pass();
        });
    }

    // Players with an equipped Identity inside the `ravager_riding` entity tag should
    //   be able to ride Ravagers.
    public static void registerRavagerRidingHandler() {
        InteractionEvent.INTERACT_ENTITY.register((player, entity, hand) -> {
            if(entity instanceof RavagerEntity) {
                if(EntityTags.RAVAGER_RIDING.contains(PlayerIdentity.getIdentity(player).getType())) {
                    player.startRiding(entity);
                }
            }

            return EventResult.pass();
        });
    }
}
