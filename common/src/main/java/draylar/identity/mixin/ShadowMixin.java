package draylar.identity.mixin;

import draylar.identity.api.PlayerIdentity;
import draylar.identity.mixin.accessor.EntityShadowAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public abstract class ShadowMixin {

    @Unique
    private static Entity identity_shadowEntity;

    @Inject(
            method = "renderShadow",
            at = @At("HEAD"))
    private static void storeContext(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Entity entity, float opacity, float tickDelta, WorldView world, float radius, CallbackInfo ci) {
        identity_shadowEntity = entity;
    }

    @ModifyVariable(
            method = "renderShadow",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(DDD)D", ordinal = 0), index = 7)
    private static float adjustShadowSize(float originalSize) {
        if(identity_shadowEntity instanceof PlayerEntity player) {
            LivingEntity identity = PlayerIdentity.getIdentity(player);

            if(identity != null) {
                EntityRenderer<?> r = MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(identity);
                float shadowRadius = ((EntityShadowAccessor) r).getShadowRadius();
                float mod = identity.isBaby() ? .5f : 1;
                return shadowRadius * mod;
            }
        }

        return originalSize;
    }
}
