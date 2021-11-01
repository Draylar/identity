package draylar.identity.mixin.accessor;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(IllagerEntityModel.class)
public interface IllagerEntityModelAccessor {
    @Accessor
    ModelPart getRightArm();
}
