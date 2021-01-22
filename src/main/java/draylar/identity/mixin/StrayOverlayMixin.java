package draylar.identity.mixin;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.StrayOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.StrayEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StrayOverlayFeatureRenderer.class)
public abstract class StrayOverlayMixin extends FeatureRenderer<StrayEntity, SkeletonEntityModel<StrayEntity>> {

    @Shadow @Final private SkeletonEntityModel<StrayEntity> model;

    public StrayOverlayMixin(FeatureRendererContext<StrayEntity, SkeletonEntityModel<StrayEntity>> context) {
        super(context);
    }

    @Inject(
            method = "render",
            at = @At("HEAD"))
    private void onRender(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, MobEntity mobEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        SkeletonEntityModel<StrayEntity> model = getContextModel();

        if (model != null) {
            model.setAttributes(this.model);
            model.sneaking = mobEntity.isInSneakingPose();
        }
    }
}
