package draylar.identity.mixin;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Invoker
    float callGetActiveEyeHeight(EntityPose pose, EntityDimensions dimensions);

    @Invoker
    void callTickActiveItemStack();

    @Invoker
    SoundEvent callGetHurtSound(DamageSource source);

    @Invoker
    SoundEvent callGetDeathSound();

    @Invoker
    SoundEvent callGetFallSound(int distance);
}
