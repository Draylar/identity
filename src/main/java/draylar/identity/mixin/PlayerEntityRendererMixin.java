package draylar.identity.mixin;

import draylar.identity.Identity;
import draylar.identity.api.model.EntityUpdater;
import draylar.identity.api.model.EntityUpdaters;
import draylar.identity.api.sneak.SneakHandler;
import draylar.identity.api.sneak.SneakHandlers;
import draylar.identity.registry.Components;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.player.PlayerEntity;
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

    @Shadow protected abstract void setModelPose(AbstractClientPlayerEntity abstractClientPlayerEntity);

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

            // Assign pose
            EntityPose pose = player.getPose();
            EntityType<? extends LivingEntity> livingType = (EntityType<? extends LivingEntity>) identity.getType();
            SneakHandler sneakHandler = SneakHandlers.get(livingType);
            if(pose == EntityPose.CROUCHING && sneakHandler != null) {
                sneakHandler.onSneak((PlayerEntity) player, identity);
            } else {
                identity.setPose(pose);
            }

            // set active hand after configuring held items
            identity.setCurrentHand(player.getActiveHand() == null ? Hand.MAIN_HAND : player.getActiveHand());

            // update identity specific properties
            EntityUpdater entityUpdater = EntityUpdaters.getUpdater((EntityType<? extends LivingEntity>) identity.getType());
            if(entityUpdater != null) {
                entityUpdater.update((PlayerEntity) player, identity);
            }
        }

        if(identity != null) {
            EntityRenderer IdentityRenderer = MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(identity);
            IdentityRenderer.render(identity, f, g, matrixStack, vertexConsumerProvider, i);
        } else {
            super.render((AbstractClientPlayerEntity) player, f, g, matrixStack, vertexConsumerProvider, i);
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
            EntityType<? extends LivingEntity> livingType = (EntityType<? extends LivingEntity>) identity.getType();
            SneakHandler sneakHandler = SneakHandlers.get(livingType);

            if(player.isInSneakingPose() && sneakHandler != null) {
                cir.setReturnValue(super.getPositionOffset(player, f));
            }
        }
    }
}
