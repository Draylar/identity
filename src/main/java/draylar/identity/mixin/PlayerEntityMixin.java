package draylar.identity.mixin;

import draylar.identity.Identity;
import draylar.identity.impl.NearbySongAccessor;
import draylar.identity.registry.Components;
import draylar.identity.registry.EntityTags;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements NearbySongAccessor {

    @Shadow public abstract boolean isSpectator();
    @Shadow public abstract EntityDimensions getDimensions(EntityPose pose);

    @Shadow public abstract boolean isSwimming();

    private PlayerEntityMixin(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    @Inject(
            method = "getDimensions",
            at = @At("HEAD"),
            cancellable = true
    )
    private void getDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        LivingEntity entity = Components.CURRENT_IDENTITY.get(this).getIdentity();

        if (entity != null) {
            cir.setReturnValue(entity.getDimensions(pose));
        }
    }

    @Override
    public boolean hurtByWater() {
        LivingEntity entity = Components.CURRENT_IDENTITY.get(this).getIdentity();

        if (entity != null) {
            return entity.hurtByWater();
        }

        return super.hurtByWater();
    }

    @Override
    public boolean canBreatheInWater() {
        LivingEntity entity = Components.CURRENT_IDENTITY.get(this).getIdentity();

        if (entity != null) {
            return entity.canBreatheInWater();
        }

        return super.canBreatheInWater();
    }

    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private void tickAquaticBreathing(CallbackInfo ci) {
        LivingEntity identity = Components.CURRENT_IDENTITY.get(this).getIdentity();

        if (identity != null) {
            if (Identity.isAquatic(identity) && !(identity instanceof DolphinEntity)) {
                int air = this.getAir();

                // copy of WaterCreatureEntity#tickWaterBreathingAir
                if (this.isAlive() && !this.isInsideWaterOrBubbleColumn()) {
                    this.setAir(air - 1);

                    // damage at threshold
                    if (this.getAir() == -20) {
                        this.setAir(0);
                        this.damage(DamageSource.DROWN, 2.0F);
                    }
                } else {
                    this.setAir(300);
                }
            }
        }
    }

    // todo: separate into other mixin?
    private boolean isNearbySongPlaying = false;

    @Environment(EnvType.CLIENT)
    @Override
    public void setNearbySongPlaying(BlockPos songPosition, boolean playing) {
        isNearbySongPlaying = playing;
    }

    @Override
    public boolean isNearbySongPlaying() {
        return isNearbySongPlaying;
    }

    @Override
    public boolean isUndead() {
        PlayerEntity playerEntity = (PlayerEntity) (Object) this;
        LivingEntity identity = Components.CURRENT_IDENTITY.get(playerEntity).getIdentity();

        return identity != null && identity.isUndead();
    }

    @Override
    public float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        PlayerEntity playerEntity = (PlayerEntity) (Object) this;

        // cursed
        try {
            LivingEntity Identity = Components.CURRENT_IDENTITY.get(playerEntity).getIdentity();

            if (Identity != null) {
                return ((LivingEntityAccessor) Identity).callGetActiveEyeHeight(getPose(), getDimensions(getPose()));
            }
        } catch (Exception ignored) {

        }

        switch (pose) {
            case SWIMMING:
            case FALL_FLYING:
            case SPIN_ATTACK:
                return 0.4F;
            case CROUCHING:
                return 1.27F;
            default:
                return 1.62F;
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public float getEyeHeight(EntityPose pose) {
        PlayerEntity playerEntity = (PlayerEntity) (Object) this;
        LivingEntity identity = Components.CURRENT_IDENTITY.get(playerEntity).getIdentity();

        if (identity != null) {
            return identity.getEyeHeight(pose);
        } else {
            return this.getEyeHeight(pose, this.getDimensions(pose));
        }
    }

    @Override
    public float getEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        PlayerEntity playerEntity = (PlayerEntity) (Object) this;

        // this is cursed
        try {
            LivingEntity Identity = Components.CURRENT_IDENTITY.get(playerEntity).getIdentity();

            if (Identity != null) {
                return Identity.getEyeHeight(pose, dimensions);
            }

        } catch (Exception ignored) {

        }

        return super.getEyeHeight(pose, dimensions);
    }

    @Override
    public float getStandingEyeHeight() {
        PlayerEntity playerEntity = (PlayerEntity) (Object) this;
        LivingEntity identity = Components.CURRENT_IDENTITY.get(playerEntity).getIdentity();

        if (identity != null) {
            return identity.getStandingEyeHeight();
        }

        return super.getStandingEyeHeight();
    }

    @Override
    public boolean isClimbing() {
        PlayerEntity playerEntity = (PlayerEntity) (Object) this;
        LivingEntity identity = Components.CURRENT_IDENTITY.get(playerEntity).getIdentity();

        if (identity instanceof SpiderEntity) {
            return this.horizontalCollision;
        }

        return super.isClimbing();
    }

    @Inject(
            method = "getHurtSound",
            at = @At("HEAD"),
            cancellable = true
    )
    private void getHurtSound(DamageSource source, CallbackInfoReturnable<SoundEvent> cir) {
        LivingEntity identity = Components.CURRENT_IDENTITY.get(this).getIdentity();

        if (Identity.CONFIG.useIdentitySounds && identity != null) {
            cir.setReturnValue(((LivingEntityAccessor) identity).callGetHurtSound(source));
        }
    }

    @Inject(
            method = "getDeathSound",
            at = @At("HEAD"),
            cancellable = true
    )
    private void getDeathSound(CallbackInfoReturnable<SoundEvent> cir) {
        LivingEntity identity = Components.CURRENT_IDENTITY.get(this).getIdentity();

        if (Identity.CONFIG.useIdentitySounds && identity != null) {
            cir.setReturnValue(((LivingEntityAccessor) identity).callGetDeathSound());
        }
    }

    @Inject(
            method = "getFallSound",
            at = @At("HEAD"),
            cancellable = true
    )
    private void getFallSound(int distance, CallbackInfoReturnable<SoundEvent> cir) {
        LivingEntity identity = Components.CURRENT_IDENTITY.get(this).getIdentity();

        if (Identity.CONFIG.useIdentitySounds && identity != null) {
            cir.setReturnValue(((LivingEntityAccessor) identity).callGetFallSound(distance));
        }
    }
}
