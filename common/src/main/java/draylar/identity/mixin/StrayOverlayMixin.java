package draylar.identity.mixin;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.StrayOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.StrayEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StrayOverlayFeatureRenderer.class)
public abstract class StrayOverlayMixin<T extends MobEntity & RangedAttackMob, M extends EntityModel<T>> extends FeatureRenderer<T, M> {

    @Shadow @Final private SkeletonEntityModel<StrayEntity> model;

    public StrayOverlayMixin(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @Inject(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/mob/MobEntity;FFFFFF)V",
            at = @At("HEAD"))
    private void onRender(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T mobEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        M model = getContextModel();

        if (model instanceof BipedEntityModel) {
            ((BipedEntityModel) model).copyBipedStateTo(this.model);
            ((BipedEntityModel) model).sneaking = mobEntity.isInSneakingPose();
        }
    }
}
