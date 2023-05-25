package tocraft.walkers.forge.config;


import tocraft.walkers.forge.WalkersForge;
import tocraft.walkers.api.platform.WalkersConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WalkersForgeConfig extends WalkersConfig {

    public boolean overlayShapesUnlocks = true;
    public boolean overlay2ndShapesRevokes = true;
    public boolean revoke2ndShapesOnDeath = false;
    public boolean shapesEquipItems = true;
    public boolean shapesEquipArmor = true;
    public boolean hostilesIgnoreHostileShapedPlayer = true;
    public boolean hostilesForgetNewHostileShapedPlayer = false;
    public boolean wolvesAttack2ndShapedPrey = true;
    public boolean ownedwolvesAttack2ndShapedPrey = false;
    public boolean villagersRunFrom2ndShapes = true;
    public boolean foxesAttack2ndShapedPrey = true;
    public boolean useShapeSounds = true;
    public boolean playAmbientSounds = true;
    public boolean hearSelfAmbient = false;
    public boolean enableFlight = true;
    public int hostilityTime = 20 * 15;
    public List<String> advancementsRequiredForFlight = new ArrayList<>();
    public boolean scalingHealth = true;
    public boolean percentScalingHealth = true;
    public int maxHealth = 40;
    public int endermanAbilityTeleportDistance = 32;
    public boolean showPlayerNametag = false;
    public boolean logCommands = true;
    public float flySpeed = 0.05f;
    public boolean wardenIsBlinded = true;
    public boolean wardenBlindsNearby = true;

    public Map<String, Integer> abilityCooldownMap = new HashMap<>() {
        {
            put("minecraft:ghast", 60);
            put("minecraft:blaze", 20);
            put("minecraft:ender_dragon", 20);
            put("minecraft:enderman", 100);
            put("minecraft:creeper", 100);
            put("minecraft:wither", 200);
            put("minecraft:snow_golem", 10);
            put("minecraft:witch", 200);
            put("minecraft:evoker", 10);
        }
    };

    public static WalkersConfig getInstance() {
        return WalkersForge.CONFIG;
    }

    @Override
    public boolean enableFlight() {
        return enableFlight;
    }

    @Override
    public List<String> advancementsRequiredForFlight() {
        return advancementsRequiredForFlight;
    }

    @Override
    public Map<String, Integer> getAbilityCooldownMap() {
        return abilityCooldownMap;
    }

    @Override
    public boolean shouldOverlayShapesUnlocks() {
        return overlayShapesUnlocks;
    }

    @Override
    public boolean logCommands() {
        return logCommands;
    }

    @Override
    public boolean wolvesAttack2ndShapedPrey() {
        return wolvesAttack2ndShapedPrey;
    }

    @Override
    public boolean ownedwolvesAttack2ndShapedPrey() {
        return ownedwolvesAttack2ndShapedPrey;
    }

    @Override
    public boolean villagersRunFrom2ndShapes() {
        return villagersRunFrom2ndShapes;
    }

    @Override
    public boolean revoke2ndShapesOnDeath() {
        return revoke2ndShapesOnDeath;
    }

    @Override
    public boolean overlay2ndShapesRevokes() {
        return overlay2ndShapesRevokes;
    }

    @Override
    public float flySpeed() {
        return flySpeed;
    }

    @Override
    public boolean scalingHealth() {
        return scalingHealth;
    }

    @Override
    public boolean percentScalingHealth() {
        return percentScalingHealth;
    }

    @Override
    public int maxHealth() {
        return maxHealth;
    }

    @Override
    public boolean shapesEquipItems() {
        return shapesEquipItems;
    }

    @Override
    public boolean shapesEquipArmor() {
        return shapesEquipArmor;
    }

    @Override
    public boolean showPlayerNametag() {
        return showPlayerNametag;
    }

    @Override
    public boolean foxesAttack2ndShapedPrey() {
        return foxesAttack2ndShapedPrey;
    }

    @Override
    public boolean hostilesForgetNewHostileShapedPlayer() {
        return hostilesForgetNewHostileShapedPlayer;
    }

    @Override
    public boolean hostilesIgnoreHostileShapedPlayer() {
        return hostilesIgnoreHostileShapedPlayer;
    }

    @Override
    public boolean playAmbientSounds() {
        return playAmbientSounds;
    }

    @Override
    public boolean useShapeSounds() {
        return useShapeSounds;
    }

    @Override
    public boolean hearSelfAmbient() {
        return hearSelfAmbient;
    }

    @Override
    public double endermanAbilityTeleportDistance() {
        return endermanAbilityTeleportDistance;
    }

    @Override
    public int hostilityTime() {
        return hostilityTime;
    }

    @Override
    public boolean wardenIsBlinded() {
        return wardenIsBlinded;
    }

    @Override
    public boolean wardenBlindsNearby() {
        return wardenBlindsNearby;
    }
}
