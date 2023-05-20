package draylar.identity.mixin;

import com.mojang.authlib.GameProfile;
import draylar.identity.Identity;
import draylar.identity.api.PlayerIdentity;
import draylar.identity.api.PlayerUnlocks;
import draylar.identity.api.FlightHelper;
import draylar.identity.api.platform.IdentityConfig;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

    @Shadow public abstract boolean isCreative();
    @Shadow public abstract boolean isSpectator();
    @Shadow public abstract void sendMessage(Text message, boolean actionBar);

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(
            method = "onDeath",
            at = @At("HEAD")
    )
    private void revokeIdentityOnDeath(DamageSource source, CallbackInfo ci) {
        if(IdentityConfig.getInstance().revokeIdentityOnDeath() && !this.isCreative() && !this.isSpectator()) {
            LivingEntity entity = PlayerIdentity.getIdentity(this);

            // revoke the identity current equipped by the player
            if(entity != null) {
                EntityType<?> type = entity.getType();
                PlayerUnlocks.revoke((ServerPlayerEntity) (Object) this, PlayerIdentity.getIdentityType(this));
                PlayerIdentity.updateIdentity((ServerPlayerEntity) (Object) this, null,null);

                // todo: this option might be server-only given that this method isn't[?] called on the client
                // send revoke message to player if they aren't in creative and the config option is on
                if(IdentityConfig.getInstance().overlayIdentityRevokes()) {
                    sendMessage(
                            Text.translatable(
                                    "identity.revoke_entity",
                                    type.getTranslationKey()
                            ), true
                    );
                }
            }
        }
    }

    @Inject(
            method = "onSpawn()V",
            at = @At("HEAD")
    )
    private void onSpawn(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        if(Identity.hasFlyingPermissions(player)) {
            if(!FlightHelper.hasFlight(player)) {
                FlightHelper.grantFlightTo(player);
                getAbilities().setFlySpeed(IdentityConfig.getInstance().flySpeed());
                sendAbilitiesUpdate();
            }

            FlightHelper.grantFlightTo(player);
        }
    }
}
