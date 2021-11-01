package draylar.identity.api.platform.forge;

import dev.architectury.networking.NetworkManager;
import draylar.identity.api.Implements;
import draylar.identity.api.platform.PlayerIdentity;
import draylar.identity.forge.impl.PlayerDataProvider;
import draylar.identity.network.NetworkHandler;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.registry.Registry;

@Implements(PlayerIdentity.class)
public class PlayerIdentityImpl {

    public static LivingEntity getIdentity(PlayerEntity player) {
        return ((PlayerDataProvider) player).getIdentity();
    }

    public static boolean updateIdentity(ServerPlayerEntity player, LivingEntity entity) {
        return ((PlayerDataProvider) player).updateIdentity(entity);
    }

    public static void sync(ServerPlayerEntity player) {
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
        NbtCompound entityTag = new NbtCompound();

        // serialize current identity data to tag if it exists
        LivingEntity identity = getIdentity(player);
        if(identity != null) {
            identity.writeNbt(entityTag);
        }

        // put entity type ID under the key "id", or "minecraft:empty" if no identity is equipped (or the identity entity type is invalid)
        packet.writeString(identity == null ? "minecraft:empty" : Registry.ENTITY_TYPE.getId(identity.getType()).toString());
        packet.writeNbt(entityTag);
        NetworkManager.sendToPlayer(player, NetworkHandler.IDENTITY_SYNC, packet);
    }
}
