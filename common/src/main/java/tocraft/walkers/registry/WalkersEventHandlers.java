package tocraft.walkers.registry;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import tocraft.walkers.api.PlayerHostility;
import tocraft.walkers.api.PlayerWalkers;
import tocraft.walkers.api.platform.WalkersConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.RavagerEntity;

public class WalkersEventHandlers {

    public static void initialize() {
        WalkersEventHandlers.registerHostilityUpdateHandler();
        WalkersEventHandlers.registerRavagerRidingHandler();
    }

    public static void registerHostilityUpdateHandler() {
        InteractionEvent.INTERACT_ENTITY.register((player, entity, hand) -> {
            if(!player.world.isClient && entity instanceof HostileEntity) {
                PlayerHostility.set(player, WalkersConfig.getInstance().hostilityTime());
            }

            return EventResult.pass();
        });
    }

    // Players with an equipped Walkers inside the `ravager_riding` entity tag should
    //   be able to ride Ravagers.
    public static void registerRavagerRidingHandler() {
        InteractionEvent.INTERACT_ENTITY.register((player, entity, hand) -> {
            if(entity instanceof RavagerEntity) {
                LivingEntity walkers = PlayerWalkers.getWalkers(player);
                if(walkers != null) {
                    if(walkers.getType().isIn(WalkersEntityTags.RAVAGER_RIDING)) {
                        player.startRiding(entity);
                    }
                }
            }

            return EventResult.pass();
        });
    }
}
