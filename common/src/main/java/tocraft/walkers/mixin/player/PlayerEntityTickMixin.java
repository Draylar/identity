package tocraft.walkers.mixin.player;

import tocraft.walkers.api.WalkersTickHandler;
import tocraft.walkers.api.WalkersTickHandlers;
import tocraft.walkers.api.PlayerAbilities;
import tocraft.walkers.api.PlayerWalkers;
import tocraft.walkers.impl.PlayerDataProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityTickMixin extends LivingEntity {

    private PlayerEntityTickMixin(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    @SuppressWarnings({"unchecked", "rawtypes", "ConstantConditions"})
    @Inject(method = "tick", at = @At("HEAD"))
    private void serverTick(CallbackInfo info) {
        // Tick WalkersTickHandlers on the client & server.
        @Nullable LivingEntity walkers = PlayerWalkers.getWalkers((PlayerEntity) (Object) this);
        if(walkers != null) {
            @Nullable WalkersTickHandler handler = WalkersTickHandlers.getHandlers().get(walkers.getType());
            if(handler != null) {
                handler.tick((PlayerEntity) (Object) this, walkers);
            }
        }

        // Update misc. server-side entity properties for the player.
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
