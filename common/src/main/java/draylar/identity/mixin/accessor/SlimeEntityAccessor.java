package draylar.identity.mixin.accessor;

import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.particle.ParticleEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SlimeEntity.class)
public interface SlimeEntityAccessor {

    @Invoker
    void callSetSize(int size, boolean heal);
}
