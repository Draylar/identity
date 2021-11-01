package draylar.identity.mixin.accessor;

import net.minecraft.entity.passive.ParrotEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ParrotEntity.class)
public interface ParrotEntityAccessor {
    @Invoker
    void callFlapWings();
}
