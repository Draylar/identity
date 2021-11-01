package draylar.identity.registry;

import draylar.identity.command.IdentityCommand;

public class Commands {

    public static void init() {
        IdentityCommand.register();
    }

    private Commands() {

    }
}
