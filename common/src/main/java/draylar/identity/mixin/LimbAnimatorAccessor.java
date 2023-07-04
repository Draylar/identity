package draylar.identity.mixin;

import net.minecraft.entity.LimbAnimator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LimbAnimator.class)
public interface LimbAnimatorAccessor {

    @Accessor
    float getPrevSpeed();

    @Accessor
    void setPrevSpeed(float prevSpeed);

    @Accessor
    float getSpeed();

    @Accessor
    void setSpeed(float speed);

    @Accessor
    float getPos();

    @Accessor
    void setPos(float pos);
}
