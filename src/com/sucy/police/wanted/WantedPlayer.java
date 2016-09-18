package com.sucy.police.wanted;

import com.sucy.police.config.WantedNodes;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Data for a player wanted for a crime
 */
public class WantedPlayer implements Comparable<WantedPlayer> {

    private String playerName;
    private String reporterName;
    private String crime;
    private int priority;

    /**
     * Constructor
     *
     * @param name     wanted player name
     * @param reporter player who added the entry
     * @param crime    crime the player committed
     * @param priority wanted priority
     */
    public WantedPlayer(String name, String reporter, String crime, int priority) {
        this.playerName = name;
        this.reporterName = reporter;
        this.crime = crime;
        this.priority = priority;
    }

    /**
     * Loads wanted player data from a config
     *
     * @param name   player name
     * @param config config to load from
     */
    public WantedPlayer(String name, ConfigurationSection config) {
        this.playerName = name;
        this.reporterName = config.getString(name + WantedNodes.REPORTER);
        this.crime = config.getString(name + WantedNodes.CRIME);
        this.priority = config.getInt(name + WantedNodes.PRIORITY);
    }

    /**
     * @return name of the wanted player
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * @return name of the player who reported the entry
     */
    public String getReporterName() {
        return reporterName;
    }

    /**
     * @return crime committed by the player
     */
    public String getCrime() {
        return crime;
    }

    /**
     * @return wanted priority of the player
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Saves the wanted data to the config
     *
     * @param config config to save to
     */
    public void save(ConfigurationSection config) {
        config.set(playerName + WantedNodes.REPORTER, reporterName);
        config.set(playerName + WantedNodes.CRIME, crime);
        config.set(playerName + WantedNodes.PRIORITY, priority);
    }

    /**
     * Compares two wanted players by their priority for sorting
     *
     * @param player player to compare to
     * @return       highest to lowest comparison of priorities
     */
    @Override
    public int compareTo(WantedPlayer player) {
        return priority > player.priority ? -1 : priority == player.priority ? 0 : 1;
    }
}
