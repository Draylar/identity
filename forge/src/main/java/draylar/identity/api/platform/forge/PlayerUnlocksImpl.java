package draylar.identity.api.platform.forge;

import dev.architectury.event.EventResult;
import dev.architectury.networking.NetworkManager;
import draylar.identity.api.Implements;
import draylar.identity.api.event.UnlockIdentityCallback;
import draylar.identity.api.platform.PlayerAbilities;
import draylar.identity.api.platform.PlayerUnlocks;
import draylar.identity.forge.impl.PlayerDataProvider;
import draylar.identity.network.NetworkHandler;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@Implements(PlayerUnlocks.class)
public class PlayerUnlocksImpl {

    public static boolean unlock(ServerPlayerEntity player, EntityType<?> granted) {
        PlayerDataProvider provider = (PlayerDataProvider) player;
        Identifier id = Registry.ENTITY_TYPE.getId(granted);
        EventResult unlock = UnlockIdentityCallback.EVENT.invoker().unlock(player, id);

        if(unlock.asMinecraft() != ActionResult.FAIL && !provider.getUnlocked().contains(id)) {
            provider.getUnlocked().add(id);
            sync(player);
            PlayerAbilities.sync(player); // TODO: ???
            return true;
        } else {
            return false;
        }
    }

    public static boolean has(PlayerEntity player, EntityType<?> type) {
        Identifier id = Registry.ENTITY_TYPE.getId(type);
        return type.equals(EntityType.PLAYER) || (((PlayerDataProvider) player)).getUnlocked().contains(id);
    }

    public static void revoke(ServerPlayerEntity player, EntityType<?> type) {
        PlayerDataProvider provider = (PlayerDataProvider) player;

        Identifier id = Registry.ENTITY_TYPE.getId(type);
        if(provider.getUnlocked().contains(id)) {
            provider.getUnlocked().remove(id);
            sync(player);
            PlayerAbilities.sync(player); // TODO: ???
        }
    }

    public static void sync(ServerPlayerEntity player) {
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());

        // Serialize unlocked to tag
        NbtCompound compound = new NbtCompound();
        NbtList idList = new NbtList();
        ((PlayerDataProvider) player).getUnlocked().forEach(entityId -> idList.add(NbtString.of(entityId.toString())));
        compound.put("UnlockedMorphs", idList);
        packet.writeNbt(compound);

        // Send to client
        NetworkManager.sendToPlayer(player, NetworkHandler.UNLOCK_SYNC, packet);
    }
}
