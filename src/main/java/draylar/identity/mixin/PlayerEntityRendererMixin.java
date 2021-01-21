package draylar.identity.mixin;

import draylar.identity.Identity;
import draylar.identity.IdentityClient;
import draylar.identity.api.model.EntityUpdater;
import draylar.identity.api.model.EntityUpdaters;
import draylar.identity.registry.Components;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    @Shadow
    protected static BipedEntityModel.ArmPose getArmPose(AbstractClientPlayerEntity abstractClientPlayerEntity, Hand hand) {
        return null;
    }

    private PlayerEntityRendererMixin(EntityRenderDispatcher dispatcher, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowSize) {
        super(dispatcher, model, shadowSize);
    }

    @Redirect(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V")
    )
    private void redirectRender(LivingEntityRenderer renderer, LivingEntity player, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        LivingEntity identity = Components.CURRENT_IDENTITY.get(player).getIdentity();

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

            ((EntityAccessor) identity).setVehicle(player.getVehicle());
            ((EntityAccessor) identity).setTouchingWater(player.isTouchingWater());

            if(identity instanceof EnderDragonEntity) {
                // setting yaw without +180 making tail faces front, for some reason
                if (((EnderDragonEntity) identity).latestSegment < 0) {
                    for(int l = 0; l < ((EnderDragonEntity) identity).segmentCircularBuffer.length; ++l) {
                       ((EnderDragonEntity) identity).segmentCircularBuffer[l][0] = (double)player.yaw + 180;
                       ((EnderDragonEntity) identity).segmentCircularBuffer[l][1] = player.getY();
                    }
                 }

                 if (++((EnderDragonEntity) identity).latestSegment == ((EnderDragonEntity) identity).segmentCircularBuffer.length) {
                    ((EnderDragonEntity) identity).latestSegment = 0;
                 }

                 ((EnderDragonEntity) identity).segmentCircularBuffer[((EnderDragonEntity) identity).latestSegment][0] = (double)player.yaw + 180;
                 ((EnderDragonEntity) identity).segmentCircularBuffer[((EnderDragonEntity) identity).latestSegment][1] = player.getY();
            }

            // phantoms' pitch is inverse for whatever reason
            if(identity instanceof PhantomEntity) {
                identity.pitch = -player.pitch;
                identity.prevPitch = -player.prevPitch;
            } else {
                identity.pitch = player.pitch;
                identity.prevPitch = player.prevPitch;
            }

            // equip held items on identity
            if(Identity.CONFIG.identitiesEquipItems) {
                identity.equipStack(EquipmentSlot.MAINHAND, player.getEquippedStack(EquipmentSlot.MAINHAND));
                identity.equipStack(EquipmentSlot.OFFHAND, player.getEquippedStack(EquipmentSlot.OFFHAND));
            }

            // equip armor items on identity
            if(Identity.CONFIG.identitiesEquipArmor) {
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
            if(IdentityClient.showNametags && player != MinecraftClient.getInstance().player) {
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
            identityBipedModel.helmet.visible = true;
        } else {
            identityBipedModel.setVisible(true);
            identityBipedModel.helmet.visible = player.isPartVisible(PlayerModelPart.HAT);
            identityBipedModel.sneaking = identity.isInSneakingPose();

            BipedEntityModel.ArmPose mainHandPose = getArmPose(player, Hand.MAIN_HAND);
            BipedEntityModel.ArmPose offHandPose = getArmPose(player, Hand.OFF_HAND);

            if (mainHandPose.method_30156()) {
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
        LivingEntity identity = Components.CURRENT_IDENTITY.get(player).getIdentity();

        if(identity != null) {
            if(identity instanceof TameableEntity) {
                cir.setReturnValue(super.getPositionOffset(player, f));
            }
        }
    }
}
