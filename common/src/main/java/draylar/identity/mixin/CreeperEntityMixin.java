package draylar.identity.mixin;

import draylar.identity.api.PlayerIdentity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreeperEntity.class)
public abstract class CreeperEntityMixin extends HostileEntity {

    private CreeperEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(
            method = "initGoals",
            at = @At("RETURN")
    )
    private void addCustomGoals(CallbackInfo ci) {
        this.goalSelector.add(3, new FleeEntityGoal<>(
                this,
                PlayerEntity.class,
                entity -> {
                    if (entity instanceof PlayerEntity player) {
                        LivingEntity identity = PlayerIdentity.getIdentity(player);
                        return identity != null && identity.getType().equals(EntityType.OCELOT);
                    }

                    return true;
                },
                6.0F,
                1.0D,
                1.2D,
                player -> true
        ));
    }
}
