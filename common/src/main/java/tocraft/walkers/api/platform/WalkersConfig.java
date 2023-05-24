package tocraft.walkers.api.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;

import java.util.List;
import java.util.Map;

public abstract class WalkersConfig {

    @ExpectPlatform
    public static WalkersConfig getInstance() {
        throw new AssertionError();
    }

    public abstract boolean enableFlight();

    public abstract List<String> advancementsRequiredForFlight();

    public abstract Map<String, Integer> getAbilityCooldownMap();

    public abstract boolean shouldOverlayWalkersUnlocks();

    public abstract boolean forceChangeNew();

    public abstract boolean forceChangeAlways();

    public abstract boolean logCommands();

    public abstract boolean enableClientSwapMenu();

    public abstract boolean wolvesAttackWalkersPrey();

    public abstract boolean ownedWolvesAttackWalkersPrey();

    public abstract boolean villagersRunFromShapes();

    public abstract boolean revokeWalkersOnDeath();

    public abstract boolean overlayWalkersRevokes();

    public abstract float flySpeed();

    public abstract boolean scalingHealth();

    public abstract int maxHealth();

    public abstract boolean shapesEquipItems();

    public abstract boolean shapesEquipArmor();

    public abstract boolean showPlayerNametag();

    public abstract boolean foxesAttackWalkersPrey();

    public abstract boolean hostilesForgetNewHostileWalkersPlayer();

    public abstract boolean hostilesIgnoreHostileWalkersPlayer();

    public abstract boolean playAmbientSounds();

    public abstract boolean useWalkersSounds();

    public abstract boolean hearSelfAmbient();

    public abstract double endermanAbilityTeleportDistance();

    public abstract boolean enableSwaps();

    public abstract int hostilityTime();

    public abstract boolean wardenIsBlinded();

    public abstract boolean wardenBlindsNearby();
}
