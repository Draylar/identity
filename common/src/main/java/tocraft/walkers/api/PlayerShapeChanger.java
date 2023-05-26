package tocraft.walkers.api;

import dev.architectury.event.EventResult;
import tocraft.walkers.api.event.UnlockWalkersCallback;
import tocraft.walkers.api.variant.ShapeType;
import tocraft.walkers.impl.PlayerDataProvider;
import tocraft.walkers.network.impl.UnlockPackets;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public class PlayerShapeChanger {

    public static boolean changeShape(ServerPlayerEntity player, ShapeType newShape) {
        PlayerDataProvider provider = (PlayerDataProvider) player;
        EventResult unlock = UnlockWalkersCallback.EVENT.invoker().unlock(player, newShape);

        if(unlock.asMinecraft() != ActionResult.FAIL && provider.get2ndShape() != newShape) {
            provider.set2ndShape(newShape);
            sync(player);
            PlayerAbilities.sync(player); // TODO: ???
            return true;
        } else {
            return false;
        }
    }

    public static void sync(ServerPlayerEntity player) {
        UnlockPackets.sendSyncPacket(player);
    }
}
