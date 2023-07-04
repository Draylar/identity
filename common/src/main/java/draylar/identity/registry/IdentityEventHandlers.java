package draylar.identity.registry;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import draylar.identity.api.PlayerHostility;
import draylar.identity.api.PlayerIdentity;
import draylar.identity.api.platform.IdentityConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.RavagerEntity;

public class IdentityEventHandlers {

    public static void initialize() {
        IdentityEventHandlers.registerHostilityUpdateHandler();
        IdentityEventHandlers.registerRavagerRidingHandler();
    }

    public static void registerHostilityUpdateHandler() {
        InteractionEvent.INTERACT_ENTITY.register((player, entity, hand) -> {
            if(!player.getWorld().isClient && entity instanceof HostileEntity) {
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
                LivingEntity identity = PlayerIdentity.getIdentity(player);
                if(identity != null) {
                    if(identity.getType().isIn(IdentityEntityTags.RAVAGER_RIDING)) {
                        player.startRiding(entity);
                    }
                }
            }

            return EventResult.pass();
        });
    }
}
