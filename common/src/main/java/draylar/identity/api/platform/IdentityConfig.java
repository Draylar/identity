package draylar.identity.api.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;

import java.util.List;
import java.util.Map;

public abstract class IdentityConfig {

    @ExpectPlatform
    public static IdentityConfig getInstance() {
        throw new AssertionError();
    }

    public abstract boolean enableFlight();

    public abstract List<String> advancementsRequiredForFlight();

    public abstract Map<String, Integer> getAbilityCooldownMap();

    public abstract boolean requiresKillsForIdentity();

    public abstract int getRequiredKillsForIdentity();

    public abstract Map<String, Integer> getRequiredKillsByType();

    public abstract boolean shouldOverlayIdentityUnlocks();

    public abstract boolean forceChangeNew();

    public abstract boolean forceChangeAlways();

    public abstract boolean logCommands();

    public abstract boolean enableClientSwapMenu();

    public abstract boolean wolvesAttackIdentityPrey();

    public abstract boolean ownedWolvesAttackIdentityPrey();

    public abstract boolean villagersRunFromIdentities();

    public abstract boolean revokeIdentityOnDeath();

    public abstract boolean overlayIdentityRevokes();

    public abstract float flySpeed();

    public abstract boolean scalingHealth();

    public abstract int maxHealth();

    public abstract boolean identitiesEquipItems();

    public abstract boolean identitiesEquipArmor();

    public abstract boolean showPlayerNametag();

    public abstract boolean shouldRenderOwnNameTag();

    public abstract boolean foxesAttackIdentityPrey();

    public abstract boolean hostilesForgetNewHostileIdentityPlayer();

    public abstract boolean hostilesIgnoreHostileIdentityPlayer();

    public abstract boolean playAmbientSounds();

    public abstract boolean useIdentitySounds();

    public abstract boolean hearSelfAmbient();

    public abstract double endermanAbilityTeleportDistance();

    public abstract boolean enableSwaps();

    public abstract int hostilityTime();

    public abstract boolean wardenIsBlinded();

    public abstract boolean wardenBlindsNearby();

    public abstract String getForcedIdentity();
}
