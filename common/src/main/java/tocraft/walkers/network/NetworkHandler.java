package tocraft.walkers.network;

import tocraft.walkers.Walkers;
import net.minecraft.util.Identifier;

public interface NetworkHandler {
    Identifier CAN_OPEN_MENU = Walkers.id("can_open_menu");
    Identifier IDENTITY_REQUEST = Walkers.id("request");
    Identifier FAVORITE_UPDATE = Walkers.id("favorite");
    Identifier USE_ABILITY = Walkers.id("use_ability");
    Identifier IDENTITY_SYNC = Walkers.id("walkers_sync");
    Identifier FAVORITE_SYNC = Walkers.id("favorite_sync");
    Identifier ABILITY_SYNC = Walkers.id("ability_sync");
    Identifier CONFIG_SYNC = Walkers.id("config_sync");
    Identifier UNLOCK_SYNC = Walkers.id("unlock_sync");
}
