package draylar.identity.mixin.accessor;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.OcelotEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(OcelotEntityModel.class)
public interface OcelotEntityModelAccessor {
    @Accessor
    ModelPart getRightFrontLeg();
}
