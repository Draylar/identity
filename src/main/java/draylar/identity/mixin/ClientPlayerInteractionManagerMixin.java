package draylar.identity.mixin;

import draylar.identity.Identity;
import draylar.identity.registry.Components;
import draylar.identity.registry.EntityTags;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

//    @Shadow @Final private MinecraftClient client;
//
//    @Inject(
//            method = "setGameMode",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameMode;setAbilitites(Lnet/minecraft/entity/player/PlayerAbilities;)V", shift = At.Shift.AFTER)
//    )
//    private void keepCustomPlayerAbilities(GameMode gameMode, CallbackInfo ci) {
//        if(Identity.hasFlyingPermissions(client.player)) {
//            client.player.abilities.allowFlying = true;
//        }
//    }
//
//    @Inject(
//            method = "copyAbilities",
//            at = @At(value = "RETURN")
//    )
//    private void keepCustomPlayerAbilities(PlayerEntity player, CallbackInfo ci) {
//        if(Identity.hasFlyingPermissions(player)) {
//            client.player.abilities.allowFlying = true;
//        }
//    }
}
