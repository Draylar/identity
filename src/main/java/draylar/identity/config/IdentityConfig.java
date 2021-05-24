package draylar.identity.config;


import draylar.omegaconfig.api.Comment;
import draylar.omegaconfig.api.Config;
import draylar.omegaconfig.api.Syncing;

import java.util.ArrayList;
import java.util.List;

public class IdentityConfig implements Config {

    @Comment(value = "Whether an overlay message appears above the hotbar when a new identity is unlocked.")
    public boolean overlayIdentityUnlocks = true;

    @Comment(value = "Whether an overlay message appears above the hotbar when a new identity is revoked.")
    public boolean overlayIdentityRevokes = true;

    @Comment(value = "Whether a player's equipped identity is revoked on death.")
    public boolean revokeIdentityOnDeath = false;

    @Comment(value = "Whether identities equip the items (swords, items, tools) held by the underlying player.")
    public boolean identitiesEquipItems = true;

    @Comment(value = "Whether identities equip the armor (chestplate, leggings, elytra) worn by the underlying player.")
    public boolean identitiesEquipArmor = true;

    @Comment(value = "Whether hostile mobs ignore players with hostile mob identities.")
    public boolean hostilesIgnoreHostileIdentityPlayer = true;

    @Comment(value = "Whether a hostile mob will stop targeting you after switching to a hostile mob identity.")
    public boolean hostilesForgetNewHostileIdentityPlayer = false;

    @Comment(value = "Whether Wolves will attack Players with an identity that the Wolf would normally hunt (Sheep, Fox, Skeleton).")
    public boolean wolvesAttackIdentityPrey = true;

    @Comment(value = "Whether owned Wolves will attack Players with an identity that the Wolf would normally hunt (Sheep, Fox, Skeleton).")
    public boolean ownedWolvesAttackIdentityPrey = false;

    @Comment(value = "Whether Villagers will run from Players morphed as identities villagers normally run from (Zombies).")
    public boolean villagersRunFromIdentities = true;

    @Comment(value = "Whether Foxes will attack Players with an identity that the Fox would normally hunt (Fish, Chicken).")
    public boolean foxesAttackIdentityPrey = true;

    @Comment(value = "Whether Identity sounds take priority over Player Sounds (eg. Blaze hurt sound when hit).")
    public boolean useIdentitySounds = true;

    @Comment(value = "Whether disguised players should randomly emit the ambient sound of their Identity.")
    public boolean playAmbientSounds = true;

    @Comment(value = "Whether disguised players should hear their own ambient sounds (only if playAmbientSounds is true).")
    public boolean hearSelfAmbient = false;

    @Comment(value = "Whether mobs in the flying entity tag can fly.")
    public boolean enableFlight = true;

    @Comment(value = "How long hostility lasts for players morphed as hostile mobs (think: Pigman aggression")
    public int hostilityTime = 20 * 15;

    @Comment(value = "A list of Advancements required before the player can fly using an Identity.")
    public List<String> advancementsRequiredForFlight = new ArrayList<>();

    @Comment(value = "Whether Identities modify your max health value based on their max health value.")
    public boolean scalingHealth = true;

    @Comment(value = "The maximum value of scaling health. Useful for not giving players 300 HP when they turn into a wither.")
    public int maxHealth = 40;

    @Syncing
    @Comment(value = "If set to false, only operators can switch identities through the ` menu. Note that this config option is synced from S2C when a client joins the game, but a client can still open the menu if they have a modified version of Identity.")
    public boolean enableClientSwapMenu = true;

    @Comment(value = "If set to false, only operators can switch identities. Used on the server; guaranteed to be authoritative.")
    public boolean enableSwaps = true;

    @Comment(value = "In ticks, how long until the Ghast can shoot a fireball again?")
    public int ghastAbilityCooldown = 60;

    @Comment(value = "In ticks, how long until the Blaze can shoot a mini fireball again?")
    public int blazeAbilityCooldown = 20;

    @Comment(value = "In ticks, how long until the Ender Dragon can shoot a dragon fireball again?")
    public int dragonAbilityCooldown = 20;

    @Comment(value = "In ticks, how long until the Enderman can teleport again?")
    public int endermanAbilityCooldown = 100;

    @Comment(value = "In blocks, how far can the Enderman ability teleport?")
    public int endermanAbilityTeleportDistance = 32;

    @Comment(value = "In ticks, how long until the Creeper can ka-boom again?")
    public int creeperAbilityCooldown = 100;

    @Comment(value = "In ticks, how long until the Wither can shoot a wither skull again?")
    public int witherAbilityCooldown = 200;

    @Comment(value = "Tick cooldown time of the Snow Golem ability")
    public int snowGolemAbilityCooldown = 10;

    @Syncing
    @Comment(value = "Should player nametags render above players disguised with an identity? Note that the server is the authority for this config option.")
    public boolean showPlayerNametag = false;

    @Comment(value = "If true, players that gain a NEW Identity will be forcibly changed into it on kill.")
    public boolean forceChangeNew = false;

    @Comment(value = "If true, players will be forcibly changed into any entity they kill. The above option, forceChangeNew, only applies to new unlocks.")
    public boolean forceChangeAlways = false;

    @Comment(value = "If true, /identity commands will send feedback in the action bar.")
    public boolean logCommands = true;

    public float flySpeed = 0.05f;

    @Override
    public String getName() {
        return "identity";
    }

    @Override
    public String getExtension() {
        return "json5";
    }
}
