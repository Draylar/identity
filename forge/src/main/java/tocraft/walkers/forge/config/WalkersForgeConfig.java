package tocraft.walkers.forge.config;


import tocraft.walkers.forge.WalkersForge;
import tocraft.walkers.api.platform.WalkersConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WalkersForgeConfig extends WalkersConfig {

    public boolean overlayWalkersUnlocks = true;
    public boolean overlayWalkersRevokes = true;
    public boolean revokeWalkersOnDeath = false;
    public boolean identitiesEquipItems = true;
    public boolean identitiesEquipArmor = true;
    public boolean hostilesIgnoreHostileWalkersPlayer = true;
    public boolean hostilesForgetNewHostileWalkersPlayer = false;
    public boolean wolvesAttackWalkersPrey = true;
    public boolean ownedWolvesAttackWalkersPrey = false;
    public boolean villagersRunFromIdentities = true;
    public boolean foxesAttackWalkersPrey = true;
    public boolean useWalkersSounds = true;
    public boolean playAmbientSounds = true;
    public boolean hearSelfAmbient = false;
    public boolean enableFlight = true;
    public int hostilityTime = 20 * 15;
    public List<String> advancementsRequiredForFlight = new ArrayList<>();
    public boolean scalingHealth = true;
    public int maxHealth = 40;
    public boolean enableClientSwapMenu = true;
    public boolean enableSwaps = true;
    public int endermanAbilityTeleportDistance = 32;
    public boolean showPlayerNametag = false;
    public boolean forceChangeNew = false;
    public boolean forceChangeAlways = false;
    public boolean logCommands = true;
    public float flySpeed = 0.05f;
    public boolean killForWalkers = false;
    public int requiredKillsForWalkers = 50;
    public boolean wardenIsBlinded = true;
    public boolean wardenBlindsNearby = true;
    public boolean autoUnlockShapes = true;

    public Map<String, Integer> requiredKillsByType = new HashMap<>() {
        {
            put("minecraft:ender_dragon", 1);
            put("minecraft:elder_guardian", 1);
            put("minecraft:wither", 1);
        }
    };

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
    public boolean requiresKillsForWalkers() {
        return killForWalkers;
    }

    @Override
    public int getRequiredKillsForWalkers() {
        return requiredKillsForWalkers;
    }

    @Override
    public Map<String, Integer> getRequiredKillsByType() {
        return requiredKillsByType;
    }

    @Override
    public boolean shouldOverlayWalkersUnlocks() {
        return overlayWalkersUnlocks;
    }

    @Override
    public boolean forceChangeNew() {
        return forceChangeNew;
    }

    @Override
    public boolean forceChangeAlways() {
        return forceChangeAlways;
    }

    @Override
    public boolean logCommands() {
        return logCommands;
    }

    @Override
    public boolean enableClientSwapMenu() {
        return enableClientSwapMenu;
    }

    @Override
    public boolean wolvesAttackWalkersPrey() {
        return wolvesAttackWalkersPrey;
    }

    @Override
    public boolean ownedWolvesAttackWalkersPrey() {
        return ownedWolvesAttackWalkersPrey;
    }

    @Override
    public boolean villagersRunFromIdentities() {
        return villagersRunFromIdentities;
    }

    @Override
    public boolean revokeWalkersOnDeath() {
        return revokeWalkersOnDeath;
    }

    @Override
    public boolean overlayWalkersRevokes() {
        return overlayWalkersRevokes;
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
    public int maxHealth() {
        return maxHealth;
    }

    @Override
    public boolean identitiesEquipItems() {
        return identitiesEquipItems;
    }

    @Override
    public boolean identitiesEquipArmor() {
        return identitiesEquipArmor;
    }

    @Override
    public boolean showPlayerNametag() {
        return showPlayerNametag;
    }

    @Override
    public boolean foxesAttackWalkersPrey() {
        return foxesAttackWalkersPrey;
    }

    @Override
    public boolean hostilesForgetNewHostileWalkersPlayer() {
        return hostilesForgetNewHostileWalkersPlayer;
    }

    @Override
    public boolean hostilesIgnoreHostileWalkersPlayer() {
        return hostilesIgnoreHostileWalkersPlayer;
    }

    @Override
    public boolean playAmbientSounds() {
        return playAmbientSounds;
    }

    @Override
    public boolean useWalkersSounds() {
        return useWalkersSounds;
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
    public boolean enableSwaps() {
        return enableSwaps;
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

    @Override
    public boolean autoUnlockShapes() {
        return autoUnlockShapes;
    }
}
