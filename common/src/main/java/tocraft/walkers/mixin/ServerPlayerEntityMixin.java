package tocraft.walkers.mixin;

import com.mojang.authlib.GameProfile;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.PlayerWalkers;
import tocraft.walkers.api.PlayerUnlocks;
import tocraft.walkers.api.FlightHelper;
import tocraft.walkers.api.platform.WalkersConfig;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
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

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile, @Nullable PlayerPublicKey publicKey) {
        super(world, pos, yaw, gameProfile, publicKey);
    }

    @Inject(
            method = "onDeath",
            at = @At("HEAD")
    )
    private void revoke2ndShapesOnDeath(DamageSource source, CallbackInfo ci) {
        if(WalkersConfig.getInstance().revoke2ndShapesOnDeath() && !this.isCreative() && !this.isSpectator()) {
            LivingEntity entity = PlayerWalkers.getWalkers(this);

            // revoke the walkers current equipped by the player
            if(entity != null) {
                EntityType<?> type = entity.getType();
                PlayerUnlocks.revoke((ServerPlayerEntity) (Object) this, PlayerWalkers.getWalkersType(this));
                PlayerWalkers.updateWalkers((ServerPlayerEntity) (Object) this, null,null);

                // todo: this option might be server-only given that this method isn't[?] called on the client
                // send revoke message to player if they aren't in creative and the config option is on
                if(WalkersConfig.getInstance().overlay2ndShapesRevokes()) {
                    sendMessage(
                            Text.translatable(
                                    "walkers.revoke_entity",
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
        if(Walkers.hasFlyingPermissions(player)) {
            if(!FlightHelper.hasFlight(player)) {
                FlightHelper.grantFlightTo(player);
                getAbilities().setFlySpeed(WalkersConfig.getInstance().flySpeed());
                sendAbilitiesUpdate();
            }

            FlightHelper.grantFlightTo(player);
        }
    }
}
