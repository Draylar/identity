package draylar.identity.registry;

import draylar.identity.Identity;
import draylar.identity.api.event.PlayerJoinCallback;
import draylar.identity.network.ServerNetworking;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.util.ActionResult;

public class EventHandlers {

    public static void init() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, entityHitResult) -> {
            if(!world.isClient && entity instanceof HostileEntity) {
                Components.HOSTILITY.get(player).setHostility(Identity.CONFIG.hostilityTime);
            }

            return ActionResult.PASS;
        });

        // Players with an equipped Identity inside the `ravager_riding` entity tag should
        //   be able to ride Ravagers.
        UseEntityCallback.EVENT.register((player, world, hand, entity, entityHitResult) -> {
            if(entity instanceof RavagerEntity) {
                LivingEntity identity = Components.CURRENT_IDENTITY.get(player).getIdentity();

                if(identity.getType().isIn(EntityTags.RAVAGER_RIDING)) {
                    player.startRiding(entity);
                }
            }

            return ActionResult.PASS;
        });
    }
}
