package draylar.identity.mixin.accessor;

import net.minecraft.entity.mob.RavagerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RavagerEntity.class)
public interface RavagerEntityAccessor {
    @Accessor
    int getAttackTick();

    @Accessor
    void setAttackTick(int attackTick);
}
