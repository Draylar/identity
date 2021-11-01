package draylar.identity.mixin.accessor;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityAccessor {
    @Accessor
    void setTouchingWater(boolean touchingWater);

    @Accessor
    void setVehicle(Entity vehicle);

    @Invoker("setFlag")
    void identity_callSetFlag(int index, boolean value);
}
