package com.sucy.police.command.record;

import com.sucy.police.Police;
import com.sucy.police.command.CommandHandler;

/**
 * Command handler for record commands
 */
public class RecordCommander extends CommandHandler {

    /**
     * Constructor
     *
     * @param plugin plugin reference
     */
    public RecordCommander(Police plugin) {
        super(plugin, "Police Records", "record");
    }

    /**
     * Registers the sub commands for records
     */
    @Override
    protected void registerCommands() {
        registerCommand("close", new CmdClose());
        registerCommand("list", new CmdList());
        registerCommand("lookup", new CmdLookup());
        registerCommand("open", new CmdOpen());
        registerCommand("remove", new CmdRemove());
        registerCommand("report", new CmdReport());
        registerCommand("delete", new CmdDelete());
    }
}
