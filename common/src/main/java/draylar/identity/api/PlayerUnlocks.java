package draylar.identity.api;

import dev.architectury.event.EventResult;
import draylar.identity.api.event.UnlockIdentityCallback;
import draylar.identity.api.variant.IdentityType;
import draylar.identity.impl.PlayerDataProvider;
import draylar.identity.network.impl.UnlockPackets;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public class PlayerUnlocks {

    public static boolean unlock(ServerPlayerEntity player, IdentityType granted) {
        PlayerDataProvider provider = (PlayerDataProvider) player;
        EventResult unlock = UnlockIdentityCallback.EVENT.invoker().unlock(player, granted);

        if(unlock.asMinecraft() != ActionResult.FAIL && !provider.getUnlocked().contains(granted)) {
            provider.getUnlocked().add(granted);
            sync(player);
            PlayerAbilities.sync(player); // TODO: ???
            return true;
        } else {
            return false;
        }
    }

    public static boolean has(PlayerEntity player, IdentityType type) {
        return type.getEntityType().equals(EntityType.PLAYER) || (((PlayerDataProvider) player)).getUnlocked().contains(type);
    }

    public static void revoke(ServerPlayerEntity player, IdentityType type) {
        PlayerDataProvider provider = (PlayerDataProvider) player;

        if(provider.getUnlocked().contains(type)) {
            provider.getUnlocked().remove(type);
            sync(player);
            PlayerAbilities.sync(player); // TODO: ???
        }
    }

    public static void sync(ServerPlayerEntity player) {
        UnlockPackets.sendSyncPacket(player);
    }
}
