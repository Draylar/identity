package draylar.identity.mixin;

import draylar.identity.Identity;
import draylar.identity.api.model.EntityUpdater;
import draylar.identity.api.model.EntityUpdaters;
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
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

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
    private void redirectRender(LivingEntityRenderer renderer, LivingEntity livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        LivingEntity identity = Components.CURRENT_IDENTITY.get(livingEntity).getIdentity();

        // sync player data to identity identity
        if(identity != null) {
            identity.lastLimbDistance = livingEntity.lastLimbDistance;
            identity.limbDistance = livingEntity.limbDistance;
            identity.limbAngle = livingEntity.limbAngle;
            identity.isHandSwinging = livingEntity.isHandSwinging;
            identity.handSwingTicks = livingEntity.handSwingTicks;
            identity.lastHandSwingProgress = livingEntity.lastHandSwingProgress;
            identity.handSwingProgress = livingEntity.handSwingProgress;
            identity.bodyYaw = livingEntity.bodyYaw;
            identity.prevBodyYaw = livingEntity.prevBodyYaw;
            identity.headYaw = livingEntity.headYaw;
            identity.prevHeadYaw = livingEntity.prevHeadYaw;
            identity.pitch = livingEntity.pitch;
            identity.prevPitch = livingEntity.prevPitch;
            identity.age = livingEntity.age;
            identity.preferredHand = livingEntity.preferredHand;

            // equip held items on identity
            if(Identity.CONFIG.identitiesEquipItems) {
                identity.equipStack(EquipmentSlot.MAINHAND, livingEntity.getEquippedStack(EquipmentSlot.MAINHAND));
                identity.equipStack(EquipmentSlot.OFFHAND, livingEntity.getEquippedStack(EquipmentSlot.OFFHAND));
            }

            // equip armor items on identity
            if(Identity.CONFIG.identitiesEquipArmor) {
                identity.equipStack(EquipmentSlot.HEAD, livingEntity.getEquippedStack(EquipmentSlot.HEAD));
                identity.equipStack(EquipmentSlot.CHEST, livingEntity.getEquippedStack(EquipmentSlot.CHEST));
                identity.equipStack(EquipmentSlot.LEGS, livingEntity.getEquippedStack(EquipmentSlot.LEGS));
                identity.equipStack(EquipmentSlot.FEET, livingEntity.getEquippedStack(EquipmentSlot.FEET));
            }

            // update identity specific properties
            EntityUpdater entityUpdater = EntityUpdaters.getUpdater((EntityType<? extends LivingEntity>) identity.getType());
            if(entityUpdater != null) {
                entityUpdater.update((PlayerEntity) livingEntity, identity);
            }
        }

        if(identity != null) {
            EntityRenderer IdentityRenderer = MinecraftClient.getInstance().getEntityRenderManager().getRenderer(identity);
            IdentityRenderer.render(identity, f, g, matrixStack, vertexConsumerProvider, i);
        } else {
            super.render((AbstractClientPlayerEntity) livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
        }
    }
}
