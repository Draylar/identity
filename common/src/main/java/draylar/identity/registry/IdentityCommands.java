package draylar.identity.registry;

import draylar.identity.command.IdentityCommand;

public class IdentityCommands {

    public static void init() {
        IdentityCommand.register();
    }

    private IdentityCommands() {

    }
}
