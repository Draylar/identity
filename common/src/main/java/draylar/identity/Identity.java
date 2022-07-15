package draylar.identity;

import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import draylar.identity.ability.AbilityRegistry;
import draylar.identity.api.PlayerFavorites;
import draylar.identity.api.PlayerIdentity;
import draylar.identity.api.PlayerUnlocks;
import draylar.identity.api.platform.IdentityConfig;
import draylar.identity.network.NetworkHandler;
import draylar.identity.network.ServerNetworking;
import draylar.identity.registry.IdentityCommands;
import draylar.identity.registry.IdentityEntityTags;
import draylar.identity.registry.IdentityEventHandlers;
import io.netty.buffer.Unpooled;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Identity {

    public static final Logger LOGGER = LoggerFactory.getLogger(Identity.class);

    public void initialize() {
        IdentityEntityTags.init();
        AbilityRegistry.init();
        IdentityEventHandlers.initialize();
        IdentityCommands.init();
        ServerNetworking.initialize();
        ServerNetworking.registerUseAbilityPacketHandler();
        registerJoinSyncPacket();
    }

    public static void registerJoinSyncPacket() {
        PlayerEvent.PLAYER_JOIN.register(player -> {
            // Send config sync packet
            PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
            packet.writeBoolean(IdentityConfig.getInstance().enableClientSwapMenu());
            packet.writeBoolean(IdentityConfig.getInstance().showPlayerNametag());
            NetworkManager.sendToPlayer(player, NetworkHandler.CONFIG_SYNC, packet);

            // Sync unlocked Identity
            PlayerUnlocks.sync(player);

            // Sync favorites
            PlayerFavorites.sync(player);
        });
    }

    public static Identifier id(String name) {
        return new Identifier("identity", name);
    }

    public static boolean hasFlyingPermissions(ServerPlayerEntity player) {
        LivingEntity identity = PlayerIdentity.getIdentity(player);

        if(identity != null && IdentityConfig.getInstance().enableFlight() && identity.getType().isIn(IdentityEntityTags.FLYING)) {
            List<String> requiredAdvancements = IdentityConfig.getInstance().advancementsRequiredForFlight();

            // requires at least 1 advancement, check if player has them
            if(!requiredAdvancements.isEmpty()) {

                boolean hasPermission = true;
                for (String requiredAdvancement : requiredAdvancements) {
                    Advancement advancement = player.server.getAdvancementLoader().get(new Identifier(requiredAdvancement));
                    AdvancementProgress progress = player.getAdvancementTracker().getProgress(advancement);

                    if(!progress.isDone()) {
                        hasPermission = false;
                    }
                }

                return hasPermission;
            }


            return true;
        }

        return false;
    }

    public static boolean isAquatic(LivingEntity entity) {
        return entity instanceof WaterCreatureEntity || entity instanceof GuardianEntity;
    }

    public static int getCooldown(EntityType<?> type) {
        String id = Registry.ENTITY_TYPE.getId(type).toString();
        return IdentityConfig.getInstance().getAbilityCooldownMap().getOrDefault(id, 20);
    }
}
