package draylar.identity.mixin.accessor;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Accessor
    boolean isJumping();

    @Invoker
    float callGetActiveEyeHeight(EntityPose pose, EntityDimensions dimensions);

    @Invoker
    void callTickActiveItemStack();

    @Invoker
    SoundEvent callGetHurtSound(DamageSource source);

    @Invoker
    SoundEvent callGetDeathSound();

    @Invoker
    void callPlayBlockFallSound();

    @Invoker
    int callComputeFallDamage(float fallDistance, float damageMultiplier);

    @Invoker
    float callGetSoundVolume();

    @Invoker
    float callGetSoundPitch();

    @Invoker
    void callSetLivingFlag(int mask, boolean value);

    @Invoker
    float callGetEyeHeight(EntityPose pose, EntityDimensions dimensions);
}
