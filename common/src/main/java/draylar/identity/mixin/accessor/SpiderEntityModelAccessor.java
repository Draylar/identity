package draylar.identity.mixin.accessor;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.SpiderEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SpiderEntityModel.class)
public interface SpiderEntityModelAccessor {
    @Accessor
    ModelPart getRightFrontLeg();
}
