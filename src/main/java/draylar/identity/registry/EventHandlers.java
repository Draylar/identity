package draylar.identity.registry;

import draylar.identity.Identity;
import draylar.identity.api.event.PlayerJoinCallback;
import draylar.identity.network.ServerNetworking;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.ActionResult;

public class EventHandlers {

    public static void init() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, entityHitResult) -> {
            if(!world.isClient && entity instanceof HostileEntity) {
                Components.HOSTILITY.get(player).setHostility(Identity.CONFIG.hostilityTime);
            }

            return ActionResult.PASS;
        });

        PlayerJoinCallback.EVENT.register(ServerNetworking::updateClientConfig);
    }
}
