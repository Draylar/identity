package draylar.identity.mixin;

import draylar.identity.api.model.ArmRenderingManipulator;
import draylar.identity.api.model.EntityArms;
import draylar.identity.api.model.EntityUpdater;
import draylar.identity.api.model.EntityUpdaters;
import draylar.identity.api.platform.IdentityConfig;
import draylar.identity.api.platform.PlayerIdentity;
import draylar.identity.mixin.accessor.EntityAccessor;
import draylar.identity.mixin.accessor.LivingEntityAccessor;
import draylar.identity.mixin.accessor.LivingEntityRendererAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    @Shadow
    protected static BipedEntityModel.ArmPose getArmPose(AbstractClientPlayerEntity player, Hand hand) {
        return null;
    }

    private PlayerEntityRendererMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Redirect(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V")
    )
    private void redirectRender(LivingEntityRenderer renderer, LivingEntity player, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        LivingEntity identity = PlayerIdentity.getIdentity((PlayerEntity) player);

        // sync player data to identity identity
        if(identity != null) {
            identity.lastLimbDistance = player.lastLimbDistance;
            identity.limbDistance = player.limbDistance;
            identity.limbAngle = player.limbAngle;
            identity.handSwinging = player.handSwinging;
            identity.handSwingTicks = player.handSwingTicks;
            identity.lastHandSwingProgress = player.lastHandSwingProgress;
            identity.handSwingProgress = player.handSwingProgress;
            identity.bodyYaw = player.bodyYaw;
            identity.prevBodyYaw = player.prevBodyYaw;
            identity.headYaw = player.headYaw;
            identity.prevHeadYaw = player.prevHeadYaw;
            identity.age = player.age;
            identity.preferredHand = player.preferredHand;
            identity.setOnGround(player.isOnGround());
            identity.setVelocity(player.getVelocity());

            ((EntityAccessor) identity).setVehicle(player.getVehicle());
            ((EntityAccessor) identity).setTouchingWater(player.isTouchingWater());

            // phantoms' pitch is inverse for whatever reason
            if(identity instanceof PhantomEntity) {
                identity.setPitch(-player.getPitch());
                identity.prevPitch = -player.prevPitch;
            } else {
                identity.setPitch(player.getPitch());
                identity.prevPitch = player.prevPitch;
            }

            // equip held items on identity
            if(IdentityConfig.getInstance().identitiesEquipItems()) {
                identity.equipStack(EquipmentSlot.MAINHAND, player.getEquippedStack(EquipmentSlot.MAINHAND));
                identity.equipStack(EquipmentSlot.OFFHAND, player.getEquippedStack(EquipmentSlot.OFFHAND));
            }

            // equip armor items on identity
            if(IdentityConfig.getInstance().identitiesEquipArmor()) {
                identity.equipStack(EquipmentSlot.HEAD, player.getEquippedStack(EquipmentSlot.HEAD));
                identity.equipStack(EquipmentSlot.CHEST, player.getEquippedStack(EquipmentSlot.CHEST));
                identity.equipStack(EquipmentSlot.LEGS, player.getEquippedStack(EquipmentSlot.LEGS));
                identity.equipStack(EquipmentSlot.FEET, player.getEquippedStack(EquipmentSlot.FEET));
            }

            if (identity instanceof MobEntity) {
                ((MobEntity) identity).setAttacking(player.isUsingItem());
            }

            // Assign pose
            identity.setPose(player.getPose());

            // set active hand after configuring held items
            identity.setCurrentHand(player.getActiveHand() == null ? Hand.MAIN_HAND : player.getActiveHand());
            ((LivingEntityAccessor) identity).callSetLivingFlag(1, player.isUsingItem());
            identity.getItemUseTime();
            ((LivingEntityAccessor) identity).callTickActiveItemStack();

            // update identity specific properties
            EntityUpdater entityUpdater = EntityUpdaters.getUpdater((EntityType<? extends LivingEntity>) identity.getType());
            if(entityUpdater != null) {
                entityUpdater.update((PlayerEntity) player, identity);
            }
        }

        if(identity != null) {
            EntityRenderer identityRenderer = MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(identity);

            // Sync biped information for stuff like bow drawing animation
            if(identityRenderer instanceof BipedEntityRenderer) {
                identity_setBipedIdentityModelPose((AbstractClientPlayerEntity) player, identity, (BipedEntityRenderer) identityRenderer);
            }

            identityRenderer.render(identity, f, g, matrixStack, vertexConsumerProvider, i);

            // Only render nametags if the server option is true and the entity being rendered is NOT this player/client
            if(IdentityConfig.getInstance().showPlayerNametag() && player != MinecraftClient.getInstance().player) {
                renderLabelIfPresent((AbstractClientPlayerEntity) player, player.getDisplayName(), matrixStack, vertexConsumerProvider, i);
            }
        } else {
            super.render((AbstractClientPlayerEntity) player, f, g, matrixStack, vertexConsumerProvider, i);
        }
    }

    private void identity_setBipedIdentityModelPose(AbstractClientPlayerEntity player, LivingEntity identity, LivingEntityRenderer identityRenderer) {
        BipedEntityModel<?> identityBipedModel = (BipedEntityModel) identityRenderer.getModel();

        if (identity.isSpectator()) {
            identityBipedModel.setVisible(false);
            identityBipedModel.head.visible = true;
            identityBipedModel.hat.visible = true;
        } else {
            identityBipedModel.setVisible(true);
            identityBipedModel.hat.visible = player.isPartVisible(PlayerModelPart.HAT);
            identityBipedModel.sneaking = identity.isInSneakingPose();

            BipedEntityModel.ArmPose mainHandPose = getArmPose(player, Hand.MAIN_HAND);
            BipedEntityModel.ArmPose offHandPose = getArmPose(player, Hand.OFF_HAND);

            if (mainHandPose.isTwoHanded()) {
                offHandPose = identity.getOffHandStack().isEmpty() ? BipedEntityModel.ArmPose.EMPTY : BipedEntityModel.ArmPose.ITEM;
            }

            if (identity.getMainArm() == Arm.RIGHT) {
                identityBipedModel.rightArmPose = mainHandPose;
                identityBipedModel.leftArmPose = offHandPose;
            } else {
                identityBipedModel.rightArmPose = offHandPose;
                identityBipedModel.leftArmPose = mainHandPose;
            }
        }
    }

    @Inject(
            method = "getPositionOffset",
            at = @At("HEAD"),
            cancellable = true
    )
    private void modifyPositionOffset(AbstractClientPlayerEntity player, float f, CallbackInfoReturnable<Vec3d> cir) {
        LivingEntity identity = PlayerIdentity.getIdentity(player);

        if(identity != null) {
            if(identity instanceof TameableEntity) {
                cir.setReturnValue(super.getPositionOffset(player, f));
            }
        }
    }

    @Inject(
            method = "renderArm",
            at = @At("HEAD"), cancellable = true)
    private void onRenderArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve, CallbackInfo ci) {
        LivingEntity identity = PlayerIdentity.getIdentity(player);

        // sync player data to identity identity
        if(identity != null) {
            EntityRenderer<?> renderer = MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(identity);

            if(renderer instanceof LivingEntityRenderer) {
                LivingEntityRenderer<LivingEntity, ?> rendererCasted = (LivingEntityRenderer<LivingEntity, ?>) renderer;
                EntityModel model = ((LivingEntityRenderer) renderer).getModel();

                // re-assign arm & sleeve models
                arm = null;
                sleeve = null;

                if(model instanceof PlayerEntityModel) {
                    arm = ((PlayerEntityModel) model).rightArm;
                    sleeve = ((PlayerEntityModel) model).rightSleeve;
                } else if(model instanceof BipedEntityModel) {
                    arm = ((BipedEntityModel) model).rightArm;
                    sleeve = null;
                } else {
                    Pair<ModelPart, ArmRenderingManipulator<EntityModel>> pair = EntityArms.get(identity, model);
                    if(pair != null) {
                        arm = pair.getLeft();
                        pair.getRight().run(matrices, model);
                        matrices.translate(0, -.35, .5);
                    }
                }

                // assign model properties
                model.handSwingProgress = 0.0F;
//                model.sneaking = false;
//                model.leaningPitch = 0.0F;
                model.setAngles(identity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);

                // render
                if(arm != null) {
                    arm.pitch = 0.0F;
                    arm.render(matrices, vertexConsumers.getBuffer(((LivingEntityRendererAccessor) rendererCasted).callGetRenderLayer(identity, true, false, true)), light, OverlayTexture.DEFAULT_UV);
                }

                if(sleeve != null) {
                    sleeve.pitch = 0.0F;
                    sleeve.render(matrices, vertexConsumers.getBuffer(((LivingEntityRendererAccessor) rendererCasted).callGetRenderLayer(identity, true, false, true)), light, OverlayTexture.DEFAULT_UV);
                }

                ci.cancel();
            }
        }
    }
}
