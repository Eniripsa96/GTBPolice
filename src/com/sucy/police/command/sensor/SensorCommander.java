package com.sucy.police.command.sensor;

import com.sucy.police.Police;
import com.sucy.police.command.CommandHandler;

/**
 * Command handler for sensor commands
 */
public class SensorCommander extends CommandHandler {

    /**
     * Constructor
     *
     * @param plugin plugin reference
     */
    public SensorCommander(Police plugin) {
        super(plugin, "Sensor Commands", "sensor");
    }

    /**
     * Registers sensor commands
     */
    @Override
    protected void registerCommands() {
        registerCommand("delete", new CmdDelete());
        registerCommand("list", new CmdList());
    }
}
