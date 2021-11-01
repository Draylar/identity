package draylar.identity.network;

import draylar.identity.Identity;
import net.minecraft.util.Identifier;

public interface NetworkHandler {
    Identifier CAN_OPEN_MENU = Identity.id("can_open_menu");
    Identifier IDENTITY_REQUEST = Identity.id("request");
    Identifier FAVORITE_UPDATE = Identity.id("favorite");
    Identifier USE_ABILITY = Identity.id("use_ability");
    Identifier IDENTITY_SYNC = Identity.id("identity_sync");
    Identifier FAVORITE_SYNC = Identity.id("favorite_sync");
    Identifier ABILITY_SYNC = Identity.id("ability_sync");
    Identifier CONFIG_SYNC = Identity.id("config_sync");
    Identifier UNLOCK_SYNC = Identity.id("unlock_sync");
}
