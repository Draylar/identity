package tocraft.walkers.registry;

import tocraft.walkers.command.WalkersCommand;

public class WalkersCommands {

    public static void init() {
        WalkersCommand.register();
    }

    private WalkersCommands() {

    }
}
