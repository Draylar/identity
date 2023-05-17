package tocraft.walkers.mixin.accessor;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BlazeEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlazeEntityModel.class)
public interface BlazeEntityModelAccessor {
    @Accessor
    ModelPart[] getRods();
}
