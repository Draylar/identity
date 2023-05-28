package tocraft.walkers;

import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import tocraft.walkers.ability.AbilityRegistry;
import tocraft.walkers.api.WalkersTickHandlers;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.PlayerShapeChanger;
import tocraft.walkers.api.platform.WalkersConfig;
import tocraft.walkers.network.NetworkHandler;
import tocraft.walkers.network.ServerNetworking;
import tocraft.walkers.registry.WalkersCommands;
import tocraft.walkers.registry.WalkersEntityTags;
import tocraft.walkers.registry.WalkersEventHandlers;
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
import net.minecraft.registry.Registries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Walkers {

    public static final Logger LOGGER = LoggerFactory.getLogger(Walkers.class);

    public void initialize() {
        WalkersEntityTags.init();
        AbilityRegistry.init();
        WalkersEventHandlers.initialize();
        WalkersCommands.init();
        ServerNetworking.initialize();
        ServerNetworking.registerUseAbilityPacketHandler();
        registerJoinSyncPacket();
        WalkersTickHandlers.initialize();
    }

    public static void registerJoinSyncPacket() {
        PlayerEvent.PLAYER_JOIN.register(player -> {
            // Send config sync packet
            PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
            packet.writeBoolean(WalkersConfig.getInstance().showPlayerNametag());
            NetworkManager.sendToPlayer(player, NetworkHandler.CONFIG_SYNC, packet);

            // Sync unlocked Walkers
            PlayerShapeChanger.sync(player);
        });
    }

    public static Identifier id(String name) {
        return new Identifier("walkers", name);
    }

    public static boolean hasFlyingPermissions(ServerPlayerEntity player) {
        LivingEntity walkers = PlayerShape.getCurrentShape(player);

        if(walkers != null && WalkersConfig.getInstance().enableFlight() && walkers.getType().isIn(WalkersEntityTags.FLYING)) {
            List<String> requiredAdvancements = WalkersConfig.getInstance().advancementsRequiredForFlight();

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
        String id = Registries.ENTITY_TYPE.getId(type).toString();
        return WalkersConfig.getInstance().getAbilityCooldownMap().getOrDefault(id, 20);
    }
}
