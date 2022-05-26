package draylar.identity.mixin;

import draylar.identity.Identity;
import draylar.identity.api.FlightHelper;
import draylar.identity.api.platform.IdentityConfig;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerAdvancementTracker.class)
public class PlayerAdvancementTrackerMixin {

    @Shadow private ServerPlayerEntity owner;

    @Inject(
            method = "grantCriterion",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/advancement/AdvancementRewards;apply(Lnet/minecraft/server/network/ServerPlayerEntity;)V")
    )
    private void refreshFlight(Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        if(Identity.hasFlyingPermissions(owner)) {
            FlightHelper.grantFlightTo(owner);
            owner.getAbilities().setFlySpeed(IdentityConfig.getInstance().flySpeed());
            owner.sendAbilitiesUpdate();
        }
    }
}
