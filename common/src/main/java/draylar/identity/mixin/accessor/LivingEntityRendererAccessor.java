package draylar.identity.mixin.accessor;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntityRenderer.class)
public interface LivingEntityRendererAccessor {
    @Invoker
    RenderLayer callGetRenderLayer(LivingEntity entity, boolean showBody, boolean translucent, boolean showOutline);
}
