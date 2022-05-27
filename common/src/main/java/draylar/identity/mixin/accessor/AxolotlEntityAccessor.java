package draylar.identity.mixin.accessor;

import net.minecraft.entity.passive.AxolotlEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AxolotlEntity.class)
public interface AxolotlEntityAccessor {

    @Invoker
    void callSetVariant(AxolotlEntity.Variant variant);
}
