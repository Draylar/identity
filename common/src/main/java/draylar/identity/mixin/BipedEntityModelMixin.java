package draylar.identity.mixin;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntityRenderer.class)
public abstract class BipedEntityModelMixin extends LivingEntityRenderer {

    private BipedEntityModelMixin(EntityRendererFactory.Context ctx, EntityModel model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(
            method = "render",
            at = @At("HEAD"))
    private void onRender(MobEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        // Only apply to Biped Entities
        if (!((Object) this instanceof BipedEntityRenderer)) {
            return;
        }

        BipedEntityModel<?> model = (BipedEntityModel) getModel();

        if (model != null) {
            model.sneaking = mobEntity.isInSneakingPose();
        }
    }
}
