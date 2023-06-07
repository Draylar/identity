package draylar.identity.mixin;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.DrownedOverlayFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.DrownedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.DrownedEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrownedOverlayFeatureRenderer.class)
public abstract class DrownedOverlayMixin extends FeatureRenderer<DrownedEntity, DrownedEntityModel<DrownedEntity>> {

    @Shadow @Final private DrownedEntityModel<DrownedEntity> model;

    public DrownedOverlayMixin(FeatureRendererContext<DrownedEntity, DrownedEntityModel<DrownedEntity>> context) {
        super(context);
    }

    @Inject(
            method = "render",
            at = @At("HEAD"))
    private void onRender(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, DrownedEntity drownedEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        DrownedEntityModel<DrownedEntity> model = getContextModel();

        if (model != null) {
            model.copyBipedStateTo(this.model);
            model.sneaking = drownedEntity.isSneaking();
        }
    }
}
