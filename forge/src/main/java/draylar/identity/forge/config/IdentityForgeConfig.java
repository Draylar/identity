package draylar.identity.forge.config;


import draylar.identity.forge.IdentityForge;
import draylar.identity.api.platform.IdentityConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IdentityForgeConfig extends IdentityConfig {

    public boolean overlayIdentityUnlocks = true;
    public boolean overlayIdentityRevokes = true;
    public boolean revokeIdentityOnDeath = false;
    public boolean identitiesEquipItems = true;
    public boolean identitiesEquipArmor = true;
    public boolean renderOwnNameTag = false;
    public boolean hostilesIgnoreHostileIdentityPlayer = true;
    public boolean hostilesForgetNewHostileIdentityPlayer = false;
    public boolean wolvesAttackIdentityPrey = true;
    public boolean ownedWolvesAttackIdentityPrey = false;
    public boolean villagersRunFromIdentities = true;
    public boolean foxesAttackIdentityPrey = true;
    public boolean useIdentitySounds = true;
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
    public boolean killForIdentity = false;
    public int requiredKillsForIdentity = 50;
    public boolean wardenIsBlinded = true;
    public boolean wardenBlindsNearby = true;

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

    public static IdentityConfig getInstance() {
        return IdentityForge.CONFIG;
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
    public boolean requiresKillsForIdentity() {
        return killForIdentity;
    }

    @Override
    public int getRequiredKillsForIdentity() {
        return requiredKillsForIdentity;
    }

    @Override
    public Map<String, Integer> getRequiredKillsByType() {
        return requiredKillsByType;
    }

    @Override
    public boolean shouldOverlayIdentityUnlocks() {
        return overlayIdentityUnlocks;
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
    public boolean wolvesAttackIdentityPrey() {
        return wolvesAttackIdentityPrey;
    }

    @Override
    public boolean ownedWolvesAttackIdentityPrey() {
        return ownedWolvesAttackIdentityPrey;
    }

    @Override
    public boolean villagersRunFromIdentities() {
        return villagersRunFromIdentities;
    }

    @Override
    public boolean revokeIdentityOnDeath() {
        return revokeIdentityOnDeath;
    }

    @Override
    public boolean overlayIdentityRevokes() {
        return overlayIdentityRevokes;
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
    public boolean shouldRenderOwnNameTag() {
        return renderOwnNameTag;
    }

    @Override
    public boolean showPlayerNametag() {
        return showPlayerNametag;
    }

    @Override
    public boolean foxesAttackIdentityPrey() {
        return foxesAttackIdentityPrey;
    }

    @Override
    public boolean hostilesForgetNewHostileIdentityPlayer() {
        return hostilesForgetNewHostileIdentityPlayer;
    }

    @Override
    public boolean hostilesIgnoreHostileIdentityPlayer() {
        return hostilesIgnoreHostileIdentityPlayer;
    }

    @Override
    public boolean playAmbientSounds() {
        return playAmbientSounds;
    }

    @Override
    public boolean useIdentitySounds() {
        return useIdentitySounds;
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
}
