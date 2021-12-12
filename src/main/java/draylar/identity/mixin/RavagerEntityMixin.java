package draylar.identity.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RavagerEntity.class)
public abstract class RavagerEntityMixin extends LivingEntity {

    @Shadow
    public abstract boolean canBeControlledByRider();

    private RavagerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    // todo: move to inject
    @Override
    public void travel(Vec3d movementInput) {
        if (this.isAlive()) {

            // Ensure Ravager has a passenger
            if (this.hasPassengers() && this.canBeControlledByRider()) {
                LivingEntity rider = (LivingEntity) this.getPrimaryPassenger();

                // Only players should be able to control Ravager
                if (rider instanceof PlayerEntity) {
                    // Assign rider properties to ravager
                    this.setYaw(rider.getYaw());
                    this.prevYaw = this.getYaw();
                    this.setPitch(rider.getPitch() * 0.5F);
                    this.setRotation(this.getYaw(), this.getPitch());
                    this.bodyYaw = this.getYaw();
                    this.headYaw = this.bodyYaw;
                    float sidewaysSpeed = rider.sidewaysSpeed * 0.5F;
                    float forwardSpeed = rider.forwardSpeed;

                    // Going backwards, slow down!
                    if (forwardSpeed <= 0.0F) {
                        forwardSpeed *= 0.25F;
                    }

                    // Update movement/velocity
                    this.airStrafingSpeed = this.getMovementSpeed() * 0.1F;
                    if (this.isLogicalSideForUpdatingMovement()) {
                        this.setMovementSpeed((float) this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
                        super.travel(new Vec3d(sidewaysSpeed, movementInput.y, forwardSpeed));
                    } else if (rider instanceof PlayerEntity) {
                        this.setVelocity(Vec3d.ZERO);
                    }

                    // Limb updates for movement
                    this.updateLimbs(this, false);
                    return;
                }
            }
            // Doesn't have a passenger, or passenger is not player,
            // but still alive, fall back to default travel logic
            super.travel(movementInput);
        }
    }
}
