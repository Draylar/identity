package tocraft.walkers.api;

import dev.architectury.networking.NetworkManager;
import tocraft.walkers.api.variant.WalkersType;
import tocraft.walkers.impl.PlayerDataProvider;
import tocraft.walkers.network.NetworkHandler;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.registry.Registry;

public class PlayerWalkers {

    /**
     * Returns the walkers associated with the {@link PlayerEntity} this component is attached to.
     *
     * <p>Note that this method may return null, which represents "no walkers."
     *
     * @return the current {@link LivingEntity} walkers associated with this component's player owner, or null if they have no walkers equipped
     */
    public static LivingEntity getWalkers(PlayerEntity player) {
        return ((PlayerDataProvider) player).getWalkers();
    }

    public static WalkersType<?> getWalkersType(PlayerEntity player) {
        return ((PlayerDataProvider) player).getWalkersType();
    }

    /**
     * Sets the walkers of the specified player.
     *
     * <p>Setting a walkers refreshes the player's dimensions/hitbox, and toggles flight capabilities depending on the entity.
     * To clear this component's walkers, pass null.
     *
     * @param entity {@link LivingEntity} new walkers for this component, or null to clear
     */
    public static boolean updateWalkers(ServerPlayerEntity player, WalkersType<?> type, LivingEntity entity) {
        return ((PlayerDataProvider) player).updateWalkers(entity);
    }

    public static void sync(ServerPlayerEntity player) {
        sync(player, player);
    }

    public static void sync(ServerPlayerEntity changed, ServerPlayerEntity packetTarget) {
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
        NbtCompound entityTag = new NbtCompound();

        // serialize current walkers data to tag if it exists
        LivingEntity walkers = getWalkers(changed);
        if(walkers != null) {
            walkers.writeNbt(entityTag);
        }

        // put entity type ID under the key "id", or "minecraft:empty" if no walkers is equipped (or the walkers entity type is invalid)
        packet.writeUuid(changed.getUuid());
        packet.writeString(walkers == null ? "minecraft:empty" : Registry.ENTITY_TYPE.getId(walkers.getType()).toString());
        packet.writeNbt(entityTag);
        NetworkManager.sendToPlayer(packetTarget, NetworkHandler.IDENTITY_SYNC, packet);
    }
}
