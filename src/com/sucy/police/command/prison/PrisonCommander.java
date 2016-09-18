package com.sucy.police.command.prison;

import com.sucy.police.Police;
import com.sucy.police.command.CommandHandler;

/**
 * Command handler for prison commands
 */
public class PrisonCommander extends CommandHandler {

    /**
     * Constructor
     *
     * @param plugin plugin reference
     */
    public PrisonCommander(Police plugin) {
        super(plugin, "Prison Commands", "prison");
    }

    /**
     * Registers commands for prison management
     */
    @Override
    protected void registerCommands() {
        registerCommand("send", new CmdSend());
        registerCommand("release", new CmdRelease());
        registerCommand("spawn", new CmdSpawn());
        registerCommand("start", new CmdStart());
        registerCommand("pvpban", new CmdPvPBan());
        registerCommand("pvpunban", new CmdPvPUnban());
    }
}
