package draylar.identity.mixin;

import draylar.identity.api.PlayerIdentity;
import net.minecraft.block.BlockState;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SweetBerryBushBlock.class)
public class SweetBerryBushBlockMixin {

    @Inject(
            method = "onEntityCollision",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void onDamage(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci) {
        if(entity instanceof PlayerEntity player) {
            LivingEntity identity = PlayerIdentity.getIdentity(player);

            // Cancel damage if the player's identity is a fox
            if(identity instanceof FoxEntity || identity instanceof BeeEntity) {
                ci.cancel();
            }
        }
    }
}
