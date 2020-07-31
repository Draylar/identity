package draylar.identity.mixin;

import draylar.identity.Identity;
import draylar.identity.registry.Components;
import io.github.ladysnake.pal.VanillaAbilities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    @Shadow public abstract boolean isCreative();

    @Shadow public abstract boolean isSpectator();

    @Shadow public abstract void sendMessage(Text message, boolean actionBar);

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
