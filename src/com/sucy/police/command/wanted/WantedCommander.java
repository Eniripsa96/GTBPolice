package com.sucy.police.command.wanted;

import com.sucy.police.Police;
import com.sucy.police.command.CommandHandler;

/**
 * Manager of commands for the wanted list
 */
public class WantedCommander extends CommandHandler {

    /**
     * Constructor
     *
     * @param plugin plugin reference
     */
    public WantedCommander(Police plugin) {
        super(plugin, "Police Wanted List", "wanted");
    }

    /**
     * Registers all of the wanted commands
     */
    @Override
    public void registerCommands() {
        registerCommand("add", new CmdAdd());
        registerCommand("info", new CmdInfo());
        registerCommand("list", new CmdList());
        registerCommand("remove", new CmdRemove());
    }
}
