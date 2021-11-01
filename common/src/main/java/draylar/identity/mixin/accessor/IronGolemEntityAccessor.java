package draylar.identity.mixin.accessor;

import net.minecraft.entity.passive.IronGolemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(IronGolemEntity.class)
public interface IronGolemEntityAccessor {
    @Accessor
    int getAttackTicksLeft();

    @Accessor
    void setAttackTicksLeft(int attackTicksLeft);
}
