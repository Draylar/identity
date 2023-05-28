package tocraft.walkers.mixin;

import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.model.ArmRenderingManipulator;
import tocraft.walkers.api.model.EntityArms;
import tocraft.walkers.api.model.EntityUpdater;
import tocraft.walkers.api.model.EntityUpdaters;
import tocraft.walkers.api.platform.WalkersConfig;
import tocraft.walkers.mixin.accessor.EntityAccessor;
import tocraft.walkers.mixin.accessor.LivingEntityAccessor;
import tocraft.walkers.mixin.accessor.LivingEntityRendererAccessor;
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
        LivingEntity walkers = PlayerShape.getCurrentShape((PlayerEntity) player);

        // sync player data to walkers walkers
        if(walkers != null) {
            walkers.lastLimbDistance = player.lastLimbDistance;
            walkers.limbDistance = player.limbDistance;
            walkers.limbAngle = player.limbAngle;
            walkers.handSwinging = player.handSwinging;
            walkers.handSwingTicks = player.handSwingTicks;
            walkers.lastHandSwingProgress = player.lastHandSwingProgress;
            walkers.handSwingProgress = player.handSwingProgress;
            walkers.bodyYaw = player.bodyYaw;
            walkers.prevBodyYaw = player.prevBodyYaw;
            walkers.headYaw = player.headYaw;
            walkers.prevHeadYaw = player.prevHeadYaw;
            walkers.age = player.age;
            walkers.preferredHand = player.preferredHand;
            walkers.setOnGround(player.isOnGround());
            walkers.setVelocity(player.getVelocity());

            ((EntityAccessor) walkers).setVehicle(player.getVehicle());
            ((EntityAccessor) walkers).setTouchingWater(player.isTouchingWater());

            // phantoms' pitch is inverse for whatever reason
            if(walkers instanceof PhantomEntity) {
                walkers.setPitch(-player.getPitch());
                walkers.prevPitch = -player.prevPitch;
            } else {
                walkers.setPitch(player.getPitch());
                walkers.prevPitch = player.prevPitch;
            }

            // equip held items on walkers
            if(WalkersConfig.getInstance().shapesEquipItems()) {
                walkers.equipStack(EquipmentSlot.MAINHAND, player.getEquippedStack(EquipmentSlot.MAINHAND));
                walkers.equipStack(EquipmentSlot.OFFHAND, player.getEquippedStack(EquipmentSlot.OFFHAND));
            }

            // equip armor items on walkers
            if(WalkersConfig.getInstance().shapesEquipArmor()) {
                walkers.equipStack(EquipmentSlot.HEAD, player.getEquippedStack(EquipmentSlot.HEAD));
                walkers.equipStack(EquipmentSlot.CHEST, player.getEquippedStack(EquipmentSlot.CHEST));
                walkers.equipStack(EquipmentSlot.LEGS, player.getEquippedStack(EquipmentSlot.LEGS));
                walkers.equipStack(EquipmentSlot.FEET, player.getEquippedStack(EquipmentSlot.FEET));
            }

            if (walkers instanceof MobEntity) {
                ((MobEntity) walkers).setAttacking(player.isUsingItem());
            }

            // Assign pose
            walkers.setPose(player.getPose());

            // set active hand after configuring held items
            walkers.setCurrentHand(player.getActiveHand() == null ? Hand.MAIN_HAND : player.getActiveHand());
            ((LivingEntityAccessor) walkers).callSetLivingFlag(1, player.isUsingItem());
            walkers.getItemUseTime();
            ((LivingEntityAccessor) walkers).callTickActiveItemStack();
            walkers.hurtTime = player.hurtTime; // FIX: https://github.com/Draylar/identity/issues/424

            // update walkers specific properties
            EntityUpdater entityUpdater = EntityUpdaters.getUpdater((EntityType<? extends LivingEntity>) walkers.getType());
            if(entityUpdater != null) {
                entityUpdater.update((PlayerEntity) player, walkers);
            }
        }

        if(walkers != null) {
            EntityRenderer walkersRenderer = MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(walkers);

            // Sync biped information for stuff like bow drawing animation
            if(walkersRenderer instanceof BipedEntityRenderer) {
                walkers_setBipedWalkersModelPose((AbstractClientPlayerEntity) player, walkers, (BipedEntityRenderer) walkersRenderer);
            }

            walkersRenderer.render(walkers, f, g, matrixStack, vertexConsumerProvider, i);

            // Only render nametags if the server option is true and the entity being rendered is NOT this player/client
            if(WalkersConfig.getInstance().showPlayerNametag() && player != MinecraftClient.getInstance().player) {
                renderLabelIfPresent((AbstractClientPlayerEntity) player, player.getDisplayName(), matrixStack, vertexConsumerProvider, i);
            }
        } else {
            super.render((AbstractClientPlayerEntity) player, f, g, matrixStack, vertexConsumerProvider, i);
        }
    }

    private void walkers_setBipedWalkersModelPose(AbstractClientPlayerEntity player, LivingEntity walkers, LivingEntityRenderer walkersRenderer) {
        BipedEntityModel<?> walkersBipedModel = (BipedEntityModel) walkersRenderer.getModel();

        if (walkers.isSpectator()) {
            walkersBipedModel.setVisible(false);
            walkersBipedModel.head.visible = true;
            walkersBipedModel.hat.visible = true;
        } else {
            walkersBipedModel.setVisible(true);
            walkersBipedModel.hat.visible = player.isPartVisible(PlayerModelPart.HAT);
            walkersBipedModel.sneaking = walkers.isInSneakingPose();

            BipedEntityModel.ArmPose mainHandPose = getArmPose(player, Hand.MAIN_HAND);
            BipedEntityModel.ArmPose offHandPose = getArmPose(player, Hand.OFF_HAND);

            if (mainHandPose.isTwoHanded()) {
                offHandPose = walkers.getOffHandStack().isEmpty() ? BipedEntityModel.ArmPose.EMPTY : BipedEntityModel.ArmPose.ITEM;
            }

            if (walkers.getMainArm() == Arm.RIGHT) {
                walkersBipedModel.rightArmPose = mainHandPose;
                walkersBipedModel.leftArmPose = offHandPose;
            } else {
                walkersBipedModel.rightArmPose = offHandPose;
                walkersBipedModel.leftArmPose = mainHandPose;
            }
        }
    }

    @Inject(
            method = "getPositionOffset",
            at = @At("HEAD"),
            cancellable = true
    )
    private void modifyPositionOffset(AbstractClientPlayerEntity player, float f, CallbackInfoReturnable<Vec3d> cir) {
        LivingEntity walkers = PlayerShape.getCurrentShape(player);

        if(walkers != null) {
            if(walkers instanceof TameableEntity) {
                cir.setReturnValue(super.getPositionOffset(player, f));
            }
        }
    }

    @Inject(
            method = "renderArm",
            at = @At("HEAD"), cancellable = true)
    private void onRenderArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve, CallbackInfo ci) {
        LivingEntity walkers = PlayerShape.getCurrentShape(player);

        // sync player data to walkers walkers
        if(walkers != null) {
            EntityRenderer<?> renderer = MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(walkers);

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
                    Pair<ModelPart, ArmRenderingManipulator<EntityModel>> pair = EntityArms.get(walkers, model);
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
                model.setAngles(walkers, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);

                // render
                if(arm != null) {
                    arm.pitch = 0.0F;
                    arm.render(matrices, vertexConsumers.getBuffer(((LivingEntityRendererAccessor) rendererCasted).callGetRenderLayer(walkers, true, false, true)), light, OverlayTexture.DEFAULT_UV);
                }

                if(sleeve != null) {
                    sleeve.pitch = 0.0F;
                    sleeve.render(matrices, vertexConsumers.getBuffer(((LivingEntityRendererAccessor) rendererCasted).callGetRenderLayer(walkers, true, false, true)), light, OverlayTexture.DEFAULT_UV);
                }

                ci.cancel();
            }
        }
    }
}
