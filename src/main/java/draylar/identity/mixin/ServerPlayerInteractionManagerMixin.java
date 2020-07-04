package draylar.identity.mixin;

import draylar.identity.Identity;
import draylar.identity.registry.Components;
import draylar.identity.registry.EntityTags;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {

    @Shadow public ServerPlayerEntity player;

    @Inject(
            method = "setGameMode",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;sendAbilitiesUpdate()V")
    )
    private void keepCustomPlayerAbilities(GameMode gameMode, GameMode gameMode2, CallbackInfo ci) {
        LivingEntity identity = Components.CURRENT_IDENTITY.get(player).getIdentity();

        if(identity != null && Identity.CONFIG.enableFlight && EntityTags.FLYING.contains(identity.getType())) {
            player.abilities.allowFlying = true;
        }
    }
}
