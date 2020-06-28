package draylar.identity.mixin;

import draylar.identity.Identity;
import draylar.identity.cca.UnlockedIdentitysComponent;
import draylar.identity.registry.Components;
import draylar.identity.registry.EntityTags;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow protected abstract int getNextAirOnLand(int air);

    @Shadow public abstract boolean hasStatusEffect(StatusEffect effect);

    private LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(
            method = "onDeath",
            at = @At("HEAD")
    )
    private void onDeath(DamageSource source, CallbackInfo ci) {
        Entity attacker = source.getAttacker();
        EntityType<?> thisType = this.getType();

        // check if attacker is a player to grant identity
        if(attacker instanceof PlayerEntity) {
            UnlockedIdentitysComponent unlocked = Components.UNLOCKED_IDENTITIES.get(attacker);

            // ensure type has not already been unlocked
            if(!unlocked.has(thisType)) {
                unlocked.unlock(thisType);

                // send unlock message to player if they aren't in creative and the config option is on
                if(Identity.CONFIG.overlayIdentityUnlocks && !((PlayerEntity) attacker).isCreative()) {
                    ((PlayerEntity) attacker).sendMessage(
                            new TranslatableText(
                                    "identity.unlock_entity",
                                    new TranslatableText(thisType.getTranslationKey())
                            ), true
                    );
                }
            }
        }
    }

    @Redirect(
            method = "baseTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setAir(I)V", ordinal = 2)
    )
    private void cancelAirIncrement(LivingEntity livingEntity, int air) {
        if ((Object) this instanceof PlayerEntity) {
            LivingEntity identity = Components.CURRENT_IDENTITY.get(this).getIdentity();

            if (identity != null) {
                if (identity instanceof WaterCreatureEntity && !(identity instanceof DolphinEntity)) {
                    return;
                }
            }
        }

        this.setAir(this.getNextAirOnLand(this.getAir()));
    }

    @Redirect(
            method = "travel",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;hasStatusEffect(Lnet/minecraft/entity/effect/StatusEffect;)Z", ordinal = 0)
    )
    private boolean chickenSlowFall(LivingEntity livingEntity, StatusEffect effect) {
        if((Object) this instanceof PlayerEntity) {
            LivingEntity Identity = Components.CURRENT_IDENTITY.get(this).getIdentity();

            if (Identity != null) {
                if (EntityTags.SLOW_FALLING.contains(Identity.getType())) {
                    return true;
                }
            }
        }
        return this.hasStatusEffect(StatusEffects.LEVITATION);
    }

//
//    @Inject(
//            method = "isClimbing",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;"),
//            cancellable = true,
//            locals = LocalCapture.CAPTURE_FAILHARD
//    )
//    public void allowSpiderClimbing(CallbackInfoReturnable<Boolean> cir, BlockState state) {
//        cir.setReturnValue(!state.isAir());
//    }
}
