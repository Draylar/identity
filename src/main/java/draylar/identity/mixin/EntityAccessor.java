package draylar.identity.mixin;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface EntityAccessor {
    @Accessor
    void setTouchingWater(boolean touchingWater);

    @Accessor
    void setVehicle(Entity vehicle);
}
