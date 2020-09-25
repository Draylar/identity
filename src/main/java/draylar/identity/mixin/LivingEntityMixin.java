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
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.BatEntity;
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
                if (Identity.isAquatic(identity) && !(identity instanceof DolphinEntity)) {
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
    private boolean slowFall(LivingEntity livingEntity, StatusEffect effect) {
        if((Object) this instanceof PlayerEntity) {
            LivingEntity identity = Components.CURRENT_IDENTITY.get(this).getIdentity();

            if (identity != null) {
                if (!this.isSneaking() && EntityTags.SLOW_FALLING.contains(identity.getType())) {
                    return true;
                }
            }
        }

        return this.hasStatusEffect(StatusEffects.SLOW_FALLING);
    }

    @Redirect(
            method = "travel",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;hasStatusEffect(Lnet/minecraft/entity/effect/StatusEffect;)Z", ordinal = 1)
    )
    private boolean applyWaterCreatureSwimSpeedBoost(LivingEntity livingEntity, StatusEffect effect) {
        if((Object) this instanceof PlayerEntity) {
            LivingEntity identity = Components.CURRENT_IDENTITY.get(this).getIdentity();

            // Apply 'Dolphin's Grace' status effect benefits if the player's Identity is a water creature
            if (identity instanceof WaterCreatureEntity) {
                return true;
            }
        }

        return this.hasStatusEffect(StatusEffects.DOLPHINS_GRACE);
    }

    @Inject(
            method = "handleFallDamage",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void handleFallDamage(float fallDistance, float damageMultiplier, CallbackInfoReturnable<Boolean> cir) {
        if((Object) this instanceof PlayerEntity) {
            LivingEntity identity = Components.CURRENT_IDENTITY.get(this).getIdentity();

            if (identity != null) {
                boolean takesFallDamage = identity.handleFallDamage(fallDistance, damageMultiplier);
                int damageAmount = ((LivingEntityAccessor) identity).callComputeFallDamage(fallDistance, damageMultiplier);

                if (takesFallDamage && damageAmount > 0) {
                    this.playSound(((LivingEntityAccessor) identity).callGetFallSound(damageAmount), 1.0F, 1.0F);
                    ((LivingEntityAccessor) identity).callPlayBlockFallSound();
                    this.damage(DamageSource.FALL, (float) damageAmount);
                    cir.setReturnValue(true);
                } else {
                    cir.setReturnValue(false);
                }
            }
        }
    }

    @Inject(
            method = "hasStatusEffect",
            at = @At("HEAD"),
            cancellable = true
    )
    private void returnHasNightVision(StatusEffect effect, CallbackInfoReturnable<Boolean> cir) {
        if(effect.equals(StatusEffects.NIGHT_VISION)) {
            LivingEntity identity = Components.CURRENT_IDENTITY.get(this).getIdentity();

            // Apply 'Night Vision' status effect to player if they are a Bat
            if (identity instanceof BatEntity) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(
            method = "getStatusEffect",
            at = @At("HEAD"),
            cancellable = true
    )
    private void returnNightVisionInstance(StatusEffect effect, CallbackInfoReturnable<StatusEffectInstance> cir) {
        if (effect.equals(StatusEffects.NIGHT_VISION)) {
            LivingEntity identity = Components.CURRENT_IDENTITY.get(this).getIdentity();

            // Apply 'Night Vision' status effect to player if they are a Bat
            if (identity instanceof BatEntity) {
                cir.setReturnValue(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 100000, 0, false, false));
            }
        }
    }

    @Inject(
            method = "getMaxHealth",
            at = @At("RETURN"),
            cancellable = true
    )
    private void modifyMaxHealth(CallbackInfoReturnable<Float> cir) {
        if(Identity.CONFIG.scalingHealth) {
            if ((Object) this instanceof PlayerEntity) {
                LivingEntity identity = Components.CURRENT_IDENTITY.get(this).getIdentity();

                if (identity != null) {
                    cir.setReturnValue(identity.getMaxHealth());
                }
            }
        }
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
