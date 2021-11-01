package draylar.identity.mixin.accessor;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.RavagerEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RavagerEntityModel.class)
public interface RavagerEntityModelAccessor {
    @Accessor
    ModelPart getRightFrontLeg();
}
