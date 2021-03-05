package draylar.identity.mixin;

import draylar.identity.Identity;
import draylar.identity.cca.IdentityComponent;
import draylar.identity.registry.Components;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {

    @Inject(
            method = "setTimeOfDay",
            at = @At("HEAD"))
    private void onTimeChange(long timeOfDay, CallbackInfo ci) {
        ServerWorld world = (ServerWorld) (Object) this;
        long cappedDayTime = timeOfDay % 24000;

        if (cappedDayTime != 0 && cappedDayTime % Identity.CONFIG.daySwapTime == 0) {
            if (Identity.CONFIG.switchEveryDay) {
                world.getPlayers().forEach(player -> {
                    if (player.isAlive()) {
                        IdentityComponent identityComponent = Components.CURRENT_IDENTITY.get(player);

                        // find a living, non-player identity to swap to
                        Entity entity;
                        do {
                            entity = Registry.ENTITY_TYPE.get(world.getRandom().nextInt(Registry.ENTITY_TYPE.getEntries().size())).create(world);
                        } while (!(entity instanceof LivingEntity) || entity instanceof PlayerEntity);
                        identityComponent.setIdentity((LivingEntity) entity);
                    }
                });
            }
        }
    }
}
