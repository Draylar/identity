package draylar.identity.network;

import dev.architectury.networking.NetworkManager;
import draylar.identity.ability.AbilityRegistry;
import draylar.identity.api.PlayerFavorites;
import draylar.identity.api.PlayerIdentity;
import draylar.identity.api.platform.IdentityConfig;
import draylar.identity.api.PlayerAbilities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.registry.Registry;

public class ServerNetworking implements NetworkHandler {


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
                            AbilityRegistry.get(identityType).onUse(player, identity, context.getPlayer().world);
                            PlayerAbilities.setCooldown(player, AbilityRegistry.get(identityType).getCooldown(identity));
                            PlayerAbilities.sync((ServerPlayerEntity) player);
                        }
                    }
                }
            });
        });
    }

    public static void registerIdentityRequestPacketHandler() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, IDENTITY_REQUEST, (buf, context) -> {
            EntityType<?> type = Registry.ENTITY_TYPE.get(buf.readIdentifier());

            context.getPlayer().getServer().execute(() -> {
                // Ensure player has permission to switch identities
                if(IdentityConfig.getInstance().enableSwaps() || context.getPlayer().hasPermissionLevel(3)) {
                    if(type.equals(EntityType.PLAYER)) {
                        PlayerIdentity.updateIdentity((ServerPlayerEntity) context.getPlayer(), null);
                    } else {
                        PlayerIdentity.updateIdentity((ServerPlayerEntity) context.getPlayer(), (LivingEntity) type.create(context.getPlayer().world));
                    }

                    // Refresh player dimensions
                    context.getPlayer().calculateDimensions();
                }
            });
        });
    }

    public static void registerFavoritePacketHandler() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, FAVORITE_UPDATE, (buf, context) -> {
            EntityType<?> type = Registry.ENTITY_TYPE.get(buf.readIdentifier());
            boolean favorite = buf.readBoolean();
            ServerPlayerEntity player = (ServerPlayerEntity) context.getPlayer();

            context.getPlayer().getServer().execute(() -> {
                if(favorite) {
                    PlayerFavorites.favorite(player, type);
                } else {
                    PlayerFavorites.unfavorite(player, type);
                }
            });
        });
    }
}
