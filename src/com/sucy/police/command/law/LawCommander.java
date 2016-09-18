package com.sucy.police.command.law;

import com.sucy.police.Police;
import com.sucy.police.command.CommandHandler;
import com.sucy.police.command.prison.CmdRelease;
import com.sucy.police.command.prison.CmdSend;
import com.sucy.police.command.prison.CmdSpawn;
import com.sucy.police.command.prison.CmdStart;

/**
 * Command Handler for law commands
 */
public class LawCommander extends CommandHandler {

    /**
     * Constructor
     *
     * @param plugin plugin reference
     */
    public LawCommander(Police plugin) {
        super(plugin, "Law Commands", "law");
    }

    /**
     * Registers commands for prison management
     */
    @Override
    protected void registerCommands() {
        registerCommand("add", new CmdAdd());
        registerCommand("category", new CmdCategory());
        registerCommand("edit", new CmdEdit());
        registerCommand("latest", new CmdLatest());
        registerCommand("list", new CmdList());
        registerCommand("remove", new CmdRemove());
        registerCommand("view", new CmdView());
    }
}
