package draylar.identity.forge.mixin;

import draylar.identity.api.platform.PlayerAbilities;
import draylar.identity.forge.impl.PlayerDataProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityTickMixin extends LivingEntity {

    public PlayerEntityTickMixin(EntityType<? extends LivingEntity> arg, World arg2) {
        super(arg, arg2);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void serverTick(CallbackInfo info) {
        if(!world.isClient) {
            PlayerDataProvider data = (PlayerDataProvider) this;
            data.setRemainingHostilityTime(Math.max(0, data.getRemainingHostilityTime() - 1));

            // Update cooldown & Sync
            ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
            PlayerAbilities.setCooldown(player, Math.max(0, data.getAbilityCooldown() - 1));
            PlayerAbilities.sync(player);
        }
    }
}
