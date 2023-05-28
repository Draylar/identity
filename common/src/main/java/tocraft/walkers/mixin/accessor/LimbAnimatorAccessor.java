package tocraft.walkers.mixin.accessor;

import net.minecraft.entity.LimbAnimator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LimbAnimator.class)
public interface LimbAnimatorAccessor
{
	@Accessor("prevSpeed")
	float getPrevSpeed();

	@Accessor("pos")
	void setPos(float pos);

	@Accessor("prevSpeed")
	void setPrevSpeed(float prevSpeed);
}