package draylar.identity.mixin;

import draylar.identity.api.IdentityGranting;
import draylar.identity.api.PlayerIdentity;
import draylar.identity.api.variant.IdentityType;
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
    private void identityAttack(Entity target, CallbackInfo ci) {
        LivingEntity identity = PlayerIdentity.getIdentity((PlayerEntity) (Object) this);

        if(identity != null) {
            if(getMainHandStack().isEmpty()) {
                try {
                    identity.tryAttack(target);
                    ci.cancel();

                    // If the target died, grant identity
                    if(!target.isAlive() && target instanceof LivingEntity living) {
                        IdentityGranting.grantByAttack((PlayerEntity) (Object) this, IdentityType.from(living));
                    }
                } catch (Exception e) {
                    // FALL BACK TO DEFAULT BEHAVIOR.
                    // Some mobs do not override, so it defaults to attack damage attribute, but the identity does not have any
                }
            }
        }
    }
}
