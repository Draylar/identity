package tocraft.walkers.mixin.player;

import tocraft.walkers.api.PlayerUnlocks;
import tocraft.walkers.impl.PlayerDataProvider;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class RespawnDataCopyMixin {

    @Inject(method = "copyFrom", at = @At("TAIL"))
    private void copyWalkersData(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        PlayerDataProvider oldData = ((PlayerDataProvider) oldPlayer);
        PlayerDataProvider newData = ((PlayerDataProvider) this);

        // Transfer data from the old ServerPlayerEntity -> new ServerPlayerEntity
        newData.setAbilityCooldown(oldData.getAbilityCooldown());
        newData.setRemainingHostilityTime(oldData.getRemainingHostilityTime());
        newData.setWalkers(oldData.getWalkers());
        newData.setUnlocked(oldData.getUnlocked());
        newData.setFavorites(oldData.getFavorites());

        PlayerUnlocks.sync((ServerPlayerEntity) (Object) this);
    }
}
