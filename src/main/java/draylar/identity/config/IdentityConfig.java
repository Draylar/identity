package draylar.identity.config;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.Comment;

@Config(name = "identity")
public class IdentityConfig implements ConfigData {

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
}
