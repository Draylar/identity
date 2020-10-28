package draylar.identity.network;

import draylar.identity.Identity;
import net.minecraft.util.Identifier;

public interface NetworkHandler {
    Identifier CAN_OPEN_MENU = Identity.id("can_open_menu");
    Identifier IDENTITY_REQUEST = Identity.id("request");
}
