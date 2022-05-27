package draylar.identity.mixin.accessor;

import net.minecraft.entity.passive.FoxEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FoxEntity.class)
public interface FoxEntityAccessor {

    @Invoker
    void callSetType(FoxEntity.Type type);
}
