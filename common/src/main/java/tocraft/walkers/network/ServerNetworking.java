package tocraft.walkers.network;

import dev.architectury.networking.NetworkManager;
import tocraft.walkers.ability.AbilityRegistry;
import tocraft.walkers.api.PlayerWalkers;
import tocraft.walkers.api.platform.WalkersConfig;
import tocraft.walkers.api.PlayerAbilities;
import tocraft.walkers.network.impl.SwapPackets;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.registry.Registry;

public class ServerNetworking implements NetworkHandler {

    public static void initialize() {
        SwapPackets.registerWalkersRequestPacketHandler();
        SwapPackets.registerWalkersRequestPacketHandler();
    }

    public static void registerUseAbilityPacketHandler() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, USE_ABILITY, (buf, context) -> {
            PlayerEntity player = context.getPlayer();

            context.getPlayer().getServer().execute(() -> {
                LivingEntity walkers = PlayerWalkers.getCurrentShape(player);

                // Verify we should use ability for the player's current walkers
                if(walkers != null) {
                    EntityType<?> walkersType = walkers.getType();

                    if(AbilityRegistry.has(walkersType)) {

                        // Check cooldown
                        if(PlayerAbilities.canUseAbility(player)) {
                            AbilityRegistry.get(walkersType).onUse(player, walkers, context.getPlayer().world);
                            PlayerAbilities.setCooldown(player, AbilityRegistry.get(walkersType).getCooldown(walkers));
                            PlayerAbilities.sync((ServerPlayerEntity) player);
                        }
                    }
                }
            });
        });
    }
}
