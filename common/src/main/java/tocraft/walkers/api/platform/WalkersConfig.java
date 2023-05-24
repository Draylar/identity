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

    public abstract boolean shouldOverlayShapesUnlocks();

    public abstract boolean logCommands();

    public abstract boolean foxesAttack2ndShapedPrey();

    public abstract boolean ownedwolvesAttack2ndShapedPrey();

    public abstract boolean villagersRunFrom2ndShapes();

    public abstract boolean revoke2ndShapesOnDeath();

    public abstract boolean overlay2ndShapesRevokes();

    public abstract float flySpeed();

    public abstract boolean scalingHealth();

    public abstract int maxHealth();

    public abstract boolean shapesEquipItems();

    public abstract boolean shapesEquipArmor();

    public abstract boolean showPlayerNametag();

    public abstract boolean wolvesAttack2ndShapedPrey();

    public abstract boolean hostilesForgetNewHostileShapedPlayer();

    public abstract boolean hostilesIgnoreHostileShapedPlayer();

    public abstract boolean playAmbientSounds();

    public abstract boolean useShapeSounds();

    public abstract boolean hearSelfAmbient();

    public abstract double endermanAbilityTeleportDistance();

    public abstract int hostilityTime();

    public abstract boolean wardenIsBlinded();

    public abstract boolean wardenBlindsNearby();
}
