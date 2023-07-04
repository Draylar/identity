package draylar.identity.mixin;

import draylar.identity.api.PlayerIdentity;
import draylar.identity.impl.SonicBoomUser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("ConstantConditions")
@Mixin(PlayerEntity.class)
public abstract class PlayerSonicBoomMixin extends LivingEntity implements SonicBoomUser {

    @Unique
    private int identity$ability_wardenBoomDelay = -1;

    private PlayerSonicBoomMixin(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    @Override
    public void identity$ability_startSonicBoom() {
        @Nullable LivingEntity identity = PlayerIdentity.getIdentity((PlayerEntity) (Object) this);
        if(identity instanceof WardenEntity) {
            getWorld().sendEntityStatus(this, EntityStatuses.SONIC_BOOM);
            identity$ability_wardenBoomDelay = 40;

            // SFX
            getWorld().playSound(null, getX(), getY(), getZ(), SoundEvents.ENTITY_WARDEN_SONIC_CHARGE, SoundCategory.PLAYERS, 3.0f, 1.0f);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickSonicBoom(CallbackInfo ci) {
        if(!getWorld().isClient) {
            identity$ability_wardenBoomDelay = Math.max(-1, identity$ability_wardenBoomDelay - 1);
            if(identity$ability_wardenBoomDelay == 0) {

                // SFX
                getWorld().playSound(null, getX(), getY(), getZ(), SoundEvents.ENTITY_WARDEN_SONIC_BOOM, SoundCategory.PLAYERS, 3.0f, 1.0f);

                // Raycast out for sonic boom effect
                float heightOffset = 1.6f;
                int distance = 16;
                Vec3d target = getPos().add(getRotationVector().multiply(distance));
                Vec3d source = getPos().add(0.0, heightOffset, 0.0);
                Vec3d offsetToTarget = target.subtract(source);
                Vec3d normalized = offsetToTarget.normalize();

                // Spawn particles from the source to the target
                Set<Entity> hit = new HashSet<>();
                for (int particleIndex = 1; particleIndex < MathHelper.floor(offsetToTarget.length()) + 7; ++particleIndex) {
                    Vec3d particlePos = source.add(normalized.multiply(particleIndex));
                    ((ServerWorld) getWorld()).spawnParticles(ParticleTypes.SONIC_BOOM, particlePos.x, particlePos.y, particlePos.z, 1, 0.0, 0.0, 0.0, 0.0);

                    // Locate entities around the particle location for damage
                    hit.addAll(getWorld().getEntitiesByClass(LivingEntity.class, new Box(new BlockPos((int) particlePos.getX(), (int) particlePos.getY(), (int) particlePos.getZ())).expand(2), it -> !(it instanceof WolfEntity)));
                }

                // Don't hit ourselves
                hit.remove((PlayerEntity) (Object) this);

                // Find
                for (Entity hitTarget : hit) {
                    if(hitTarget instanceof LivingEntity living) {
                        living.damage(getDamageSources().sonicBoom((PlayerEntity) (Object) this), 10.0f);
                        double vertical = 0.5 * (1.0 - living.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
                        double horizontal = 2.5 * (1.0 - living.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
                        living.addVelocity(normalized.getX() * horizontal, normalized.getY() * vertical, normalized.getZ() * horizontal);
                    }
                }
            }
        }
    }
}
