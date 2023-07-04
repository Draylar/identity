package draylar.identity.network;

import dev.architectury.networking.NetworkManager;
import draylar.identity.ability.AbilityRegistry;
import draylar.identity.api.PlayerIdentity;
import draylar.identity.api.PlayerAbilities;
import draylar.identity.network.impl.FavoritePackets;
import draylar.identity.network.impl.SwapPackets;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerNetworking implements NetworkHandler {

    public static void initialize() {
        FavoritePackets.registerFavoriteRequestHandler();
        SwapPackets.registerIdentityRequestPacketHandler();
        SwapPackets.registerIdentityRequestPacketHandler();
    }

    public static void registerUseAbilityPacketHandler() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, USE_ABILITY, (buf, context) -> {
            PlayerEntity player = context.getPlayer();

            context.getPlayer().getServer().execute(() -> {
                LivingEntity identity = PlayerIdentity.getIdentity(player);

                // Verify we should use ability for the player's current identity
                if(identity != null) {
                    EntityType<?> identityType = identity.getType();

                    if(AbilityRegistry.has(identityType)) {

                        // Check cooldown
                        if(PlayerAbilities.canUseAbility(player)) {
                            AbilityRegistry.get(identityType).onUse(player, identity, context.getPlayer().getWorld());
                            PlayerAbilities.setCooldown(player, AbilityRegistry.get(identityType).getCooldown(identity));
                            PlayerAbilities.sync((ServerPlayerEntity) player);
                        }
                    }
                }
            });
        });
    }
}
