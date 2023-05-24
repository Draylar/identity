package tocraft.walkers.api;

import dev.architectury.event.EventResult;
import tocraft.walkers.api.event.UnlockWalkersCallback;
import tocraft.walkers.api.variant.WalkersType;
import tocraft.walkers.impl.PlayerDataProvider;
import tocraft.walkers.network.impl.UnlockPackets;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public class PlayerUnlocks {

    public static boolean unlock(ServerPlayerEntity player, WalkersType granted) {
        PlayerDataProvider provider = (PlayerDataProvider) player;
        EventResult unlock = UnlockWalkersCallback.EVENT.invoker().unlock(player, granted);

        if(unlock.asMinecraft() != ActionResult.FAIL && !provider.get2ndShape().contains(granted)) {
            provider.get2ndShape().add(granted);
            sync(player);
            PlayerAbilities.sync(player); // TODO: ???
            return true;
        } else {
            return false;
        }
    }

    public static boolean has(PlayerEntity player, WalkersType type) {
        return type.getEntityType().equals(EntityType.PLAYER) || (((PlayerDataProvider) player)).get2ndShape().contains(type);
    }

    public static void revoke(ServerPlayerEntity player, WalkersType type) {
        PlayerDataProvider provider = (PlayerDataProvider) player;

        if(provider.get2ndShape().contains(type)) {
            provider.get2ndShape().remove(type);
            sync(player);
            PlayerAbilities.sync(player); // TODO: ???
        }
    }

    public static void sync(ServerPlayerEntity player) {
        UnlockPackets.sendSyncPacket(player);
    }
}
