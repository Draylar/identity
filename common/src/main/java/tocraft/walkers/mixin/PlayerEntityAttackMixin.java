package tocraft.walkers.mixin;

import tocraft.walkers.api.WalkersGranting;
import tocraft.walkers.api.PlayerWalkers;
import tocraft.walkers.api.variant.WalkersType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityAttackMixin extends LivingEntity {

    private PlayerEntityAttackMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(
            method = "attack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;handleAttack(Lnet/minecraft/entity/Entity;)Z"), cancellable = true)
    private void walkersAttack(Entity target, CallbackInfo ci) {
        LivingEntity walkers = PlayerWalkers.getWalkers((PlayerEntity) (Object) this);

        if(walkers != null) {
            if(getMainHandStack().isEmpty()) {
                try {
                    walkers.tryAttack(target);
                    ci.cancel();

                    // If the target died, grant walkers
                    if(!target.isAlive() && target instanceof LivingEntity living) {
                        WalkersGranting.grantByAttack((PlayerEntity) (Object) this, WalkersType.from(living));
                    }
                } catch (Exception e) {
                    // FALL BACK TO DEFAULT BEHAVIOR.
                    // Some mobs do not override, so it defaults to attack damage attribute, but the walkers does not have any
                }
            }
        }
    }
}
