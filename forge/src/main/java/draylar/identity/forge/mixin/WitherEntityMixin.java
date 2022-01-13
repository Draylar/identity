package draylar.identity.forge.mixin;

import draylar.identity.api.PlayerIdentity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.List;

@Mixin(WitherEntity.class)
public abstract class WitherEntityMixin extends HostileEntity {

    private WitherEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    // There's a de-compilation difference between Forge & Fabric which requires a tweaked mixin on both sides.
    @Inject(
            method = "mobTick",
            at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void removeInvalidPlayerTargets(CallbackInfo ci, int j, int b, List<LivingEntity> list) {
        List<LivingEntity> toRemove = new ArrayList<>();

        list.forEach(entity -> {
            if(entity instanceof PlayerEntity player) {
                LivingEntity identity = PlayerIdentity.getIdentity(player);

                // potentially ignore undead identity players
                if(identity != null && identity.isUndead()) {
                    if(this.getTarget() != null) {
                        // if this wither's target is not equal to the current entity
                        if(!this.getTarget().getUuid().equals(entity.getUuid())) {
                            toRemove.add(entity);
                        }
                    } else {
                        toRemove.add(entity);
                    }
                }
            }
        });

        list.removeAll(toRemove);
    }
}
