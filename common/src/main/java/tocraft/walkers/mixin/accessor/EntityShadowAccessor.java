package tocraft.walkers.mixin.accessor;

import net.minecraft.client.render.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityRenderer.class)
public interface EntityShadowAccessor {
    @Accessor
    float getShadowRadius();
}
