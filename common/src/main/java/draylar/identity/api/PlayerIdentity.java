package draylar.identity.api;

import dev.architectury.networking.NetworkManager;
import draylar.identity.api.variant.IdentityType;
import draylar.identity.impl.PlayerDataProvider;
import draylar.identity.network.NetworkHandler;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerIdentity {

    /**
     * Returns the identity associated with the {@link PlayerEntity} this component is attached to.
     *
     * <p>Note that this method may return null, which represents "no identity."
     *
     * @return the current {@link LivingEntity} identity associated with this component's player owner, or null if they have no identity equipped
     */
    public static LivingEntity getIdentity(PlayerEntity player) {
        return ((PlayerDataProvider) player).getIdentity();
    }

    public static IdentityType<?> getIdentityType(PlayerEntity player) {
        return ((PlayerDataProvider) player).getIdentityType();
    }

    /**
     * Sets the identity of the specified player.
     *
     * <p>Setting a identity refreshes the player's dimensions/hitbox, and toggles flight capabilities depending on the entity.
     * To clear this component's identity, pass null.
     *
     * @param entity {@link LivingEntity} new identity for this component, or null to clear
     */
    public static boolean updateIdentity(ServerPlayerEntity player, IdentityType<?> type, LivingEntity entity) {
        return ((PlayerDataProvider) player).updateIdentity(entity);
    }

    public static void sync(ServerPlayerEntity player) {
        sync(player, player);
    }

    public static void sync(ServerPlayerEntity changed, ServerPlayerEntity packetTarget) {
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
        NbtCompound entityTag = new NbtCompound();

        // serialize current identity data to tag if it exists
        LivingEntity identity = getIdentity(changed);
        if(identity != null) {
            identity.writeNbt(entityTag);
        }

        // put entity type ID under the key "id", or "minecraft:empty" if no identity is equipped (or the identity entity type is invalid)
        packet.writeUuid(changed.getUuid());
        packet.writeString(identity == null ? "minecraft:empty" : Registries.ENTITY_TYPE.getId(identity.getType()).toString());
        packet.writeNbt(entityTag);
        NetworkManager.sendToPlayer(packetTarget, NetworkHandler.IDENTITY_SYNC, packet);
    }
}
