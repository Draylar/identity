package draylar.identity.mixin;

import draylar.identity.Identity;
import draylar.identity.impl.NearbySongAccessor;
import draylar.identity.registry.Components;
import draylar.identity.registry.EntityTags;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntityMixin implements NearbySongAccessor {

    @Shadow
    public abstract boolean isSpectator();

    @Shadow
    public abstract EntityDimensions getDimensions(EntityPose pose);

    @Shadow
    public abstract boolean isSwimming();

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
    protected void identity_hurtByWater(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = Components.CURRENT_IDENTITY.get(this).getIdentity();

        if (entity != null) {
            cir.setReturnValue(entity.hurtByWater());
        }
    }

    @Override
    protected void identity_canBreatheInWater(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = Components.CURRENT_IDENTITY.get(this).getIdentity();

        if (entity != null) {
            cir.setReturnValue(entity.canBreatheInWater() || entity instanceof DolphinEntity || EntityTags.UNDROWNABLE.contains(entity.getType()));
        }
    }

    /**
     * When a player turns into an Aquatic identity, they lose breath outside water.
     *
     * @param ci mixin callback info
     */
    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private void tickAquaticBreathingOutsideWater(CallbackInfo ci) {
        LivingEntity identity = Components.CURRENT_IDENTITY.get(this).getIdentity();

        if (identity != null) {
            if (Identity.isAquatic(identity)) {
                int air = this.getAir();

                // copy of WaterCreatureEntity#tickWaterBreathingAir
                if (this.isAlive() && !this.isInsideWaterOrBubbleColumn()) {
                    int i = EnchantmentHelper.getRespiration((LivingEntity) (Object) this);

                    // If the player has respiration, 50% chance to not consume air
                    if (i > 0) {
                        if (random.nextInt(i + 1) <= 0) {
                            this.setAir(air - 1);
                        }
                    }

                    // No respiration, decrease air as normal
                    else {
                        this.setAir(air - 1);
                    }

                    // Air has ran out, start drowning
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
    private boolean nearbySongPlaying = false;

    @Environment(EnvType.CLIENT)
    @Override
    protected void identity_setNearbySongPlaying(BlockPos songPosition, boolean playing, CallbackInfo ci) {
        nearbySongPlaying = playing;
    }

    @Override
    public boolean identity_isNearbySongPlaying() {
        return nearbySongPlaying;
    }

    @Override
    protected void identity_isUndead(CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity playerEntity = (PlayerEntity) (Object) this;
        LivingEntity identity = Components.CURRENT_IDENTITY.get(playerEntity).getIdentity();

        if (identity != null) {
            cir.setReturnValue(identity.isUndead());
        }
    }

    @Inject(method = "getActiveEyeHeight", at = @At("HEAD"), cancellable = true)
    private void identity_getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions, CallbackInfoReturnable<Float> cir) {
        PlayerEntity playerEntity = (PlayerEntity) (Object) this;

        // cursed
        try {
            LivingEntity identity = Components.CURRENT_IDENTITY.get(playerEntity).getIdentity();

            if (identity != null) {
                cir.setReturnValue(((LivingEntityAccessor) identity).callGetActiveEyeHeight(getPose(), getDimensions(getPose())));
            }
        } catch (Exception ignored) {

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
    protected void identity_allowSpiderClimbing(CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity playerEntity = (PlayerEntity) (Object) this;
        LivingEntity identity = Components.CURRENT_IDENTITY.get(playerEntity).getIdentity();

        if (identity instanceof SpiderEntity) {
            cir.setReturnValue(this.horizontalCollision);
        }
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


    // todo: separate mixin for ambient sounds
    private int identity_ambientSoundChance = 0;

    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private void tickAmbientSounds(CallbackInfo ci) {
        LivingEntity identity = Components.CURRENT_IDENTITY.get(this).getIdentity();

        if (!world.isClient && Identity.CONFIG.playAmbientSounds && identity instanceof MobEntity) {
            MobEntity mobIdentity = (MobEntity) identity;

            if (this.isAlive() && this.random.nextInt(1000) < this.identity_ambientSoundChance++) {
                // reset sound delay
                this.identity_ambientSoundChance = -mobIdentity.getMinAmbientSoundDelay();

                // play ambient sound
                SoundEvent sound = ((MobEntityAccessor) mobIdentity).callGetAmbientSound();
                if (sound != null) {
                    float volume = ((LivingEntityAccessor) mobIdentity).callGetSoundVolume();
                    float pitch = ((LivingEntityAccessor) mobIdentity).callGetSoundPitch();

                    // By default, players can not hear their own ambient noises.
                    // This is because ambient noises can be very annoying.
                    if(Identity.CONFIG.hearSelfAmbient) {
                        this.world.playSound(null, this.getX(), this.getY(), this.getZ(), sound, this.getSoundCategory(), volume, pitch);
                    } else {
                        this.world.playSound((PlayerEntity) (Object) this, this.getX(), this.getY(), this.getZ(), sound, this.getSoundCategory(), volume, pitch);
                    }
                }
            }
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

    @Override
    protected void identity_canWalkOnFluid(Fluid fluid, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity identity = Components.CURRENT_IDENTITY.get(this).getIdentity();

        if (identity != null && EntityTags.LAVA_WALKING.contains(identity.getType()) && fluid.isIn(FluidTags.LAVA)) {
            cir.setReturnValue(true);
        }
    }
}
