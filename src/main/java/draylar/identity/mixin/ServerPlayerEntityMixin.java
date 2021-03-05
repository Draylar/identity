package draylar.identity.mixin;

import com.mojang.authlib.GameProfile;
import draylar.identity.Identity;
import draylar.identity.cca.IdentityComponent;
import draylar.identity.registry.Components;
import io.github.ladysnake.pal.VanillaAbilities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Shadow public abstract boolean isCreative();

    @Shadow public abstract boolean isSpectator();

    @Shadow public abstract void sendMessage(Text message, boolean actionBar);

    @Shadow public abstract boolean isInvulnerableTo(DamageSource damageSource);

    @Inject(
            method = "onDeath",
            at = @At("HEAD")
    )
    private void revokeIdentityOnDeath(DamageSource source, CallbackInfo ci) {
        if(Identity.CONFIG.revokeIdentityOnDeath && !this.isCreative() && !this.isSpectator()) {
            LivingEntity entity = Components.CURRENT_IDENTITY.get(this).getIdentity();

            // revoke the identity current equipped by the player
            if(entity != null) {
                EntityType<?> IdentityType = entity.getType();
                Components.CURRENT_IDENTITY.get(this).setIdentity(null);
                Components.UNLOCKED_IDENTITIES.get(this).revoke(IdentityType);

                // todo: this option might be server-only given that this method isn't[?] called on the client
                // send revoke message to player if they aren't in creative and the config option is on
                if(Identity.CONFIG.overlayIdentityRevokes) {
                    sendMessage(
                            new TranslatableText(
                                    "identity.revoke_entity",
                                    new TranslatableText(IdentityType.getTranslationKey()).asString()
                            ), true
                    );
                }
            }
        }
    }

    @Inject(
            method = "damage",
            at = @At("RETURN"))
    private void switchIdentityOnDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!this.isInvulnerableTo(source) && isAlive() && !source.isFire()) {
            if(Identity.CONFIG.switchOnDamage) {
                IdentityComponent identityComponent = Components.CURRENT_IDENTITY.get(this);

                // find a living, non-player identity to swap to
                Entity entity;
                do {
                    entity = Registry.ENTITY_TYPE.get(world.getRandom().nextInt(Registry.ENTITY_TYPE.getEntries().size())).create(world);
                } while(!(entity instanceof LivingEntity) || entity instanceof PlayerEntity);
                identityComponent.setIdentity((LivingEntity) entity);
            }
        }
    }

    @Inject(
            method = "onSpawn",
            at = @At("HEAD")
    )
    private void onSpawn(CallbackInfo ci) {
        if(Identity.hasFlyingPermissions((ServerPlayerEntity) (Object) this)) {
            if(!Identity.ABILITY_SOURCE.grants((ServerPlayerEntity) (Object) this, VanillaAbilities.ALLOW_FLYING)) {
                Identity.ABILITY_SOURCE.grantTo((ServerPlayerEntity) (Object) this, VanillaAbilities.ALLOW_FLYING);
            }

            Identity.ABILITY_SOURCE.grantTo((ServerPlayerEntity) (Object) this, VanillaAbilities.FLYING);
        }
    }
}
