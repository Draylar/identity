package draylar.identity.mixin.accessor;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.SquidEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SquidEntityModel.class)
public interface SquidEntityModelAccessor {
    @Accessor
    ModelPart[] getTentacles();
}
