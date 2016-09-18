package com.sucy.police;

import com.sucy.police.config.Config;
import com.sucy.police.report.Report;
import com.sucy.police.report.ReportList;
import com.sucy.police.report.ReportStatus;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Data for a player
 */
public class PlayerData {

    public static final String REPORT_FOLDER = "records/";

    private final Config config;

    private Police plugin;
    private String name;
    private String jailer;
    private long damageTimer;
    private int beatDownHealth;
    private int deletedRecords;

    /**
     * Constructor
     *
     * @param plugin  plugin reference
     * @param name    player name
     * @param health  beat down health
     */
    public PlayerData(Police plugin, String name, int health) {
        this.plugin = plugin;
        this.config = new Config(plugin, REPORT_FOLDER + name);
        if (config.getConfig().contains("d")) {
            deletedRecords = config.getConfig().getInt("d");
        }
        else {
            config.getConfig().set("d", 0);
            deletedRecords = 0;
        }
        this.name = name;
        this.beatDownHealth = Math.min(health, 14);
    }

    /**
     * @return plugin reference
     */
    public Police getPlugin() {
        return plugin;
    }

    /**
     * @return name of the player
     */
    public String getPlayerName() {
        return name;
    }

    /**
     * @return player
     */
    public Player getPlayer() {
        return plugin.getServer().getPlayer(name);
    }

    /**
     * @return current beat down health for the player
     */
    public int getBeatDownHealth() {
        return beatDownHealth;
    }

    /**
     * @return name of the last person to hit this player with a baton
     */
    public String getJailerName() {
        return jailer;
    }

    /**
     * @return lat player to hit this player with a baton
     */
    public Player getJailer() {
        return plugin.getServer().getPlayer(jailer);
    }

    /**
     * @return next report ID for the player
     */
    public int getNextId() {
        return config.getConfig().getKeys(false).size() + deletedRecords;
    }

    /**
     * @return list of related reports
     */
    public ReportList getReports() {
        ReportList list = new ReportList(deletedRecords);
        for (String key : config.getConfig().getKeys(false)) {
            if (key.equals("d")) continue;
            list.add(new Report(this, Integer.parseInt(key)));
        }
        return list;
    }

    /**
     * Deletes a record
     *
     * @param id record id
     * @return   true if deleted, false if didn't exist
     */
    public boolean deleteReport(int id) {
        if (config.getConfig().contains(id + "")) {
            config.getConfig().set(id + "", null);
            deletedRecords++;
            config.getConfig().set("d", deletedRecords);
            return true;
        }
        else return false;
    }

    /**
     * Applies beat down damage to the player
     *
     * @param amount amount fo damage to deal
     * @param jailer player that is trying to jail this player
     * @return       amount of health remaining
     */
    public int damage(int amount, Player jailer) {

        // Cannot damage too frequently
        if (damageTimer < System.currentTimeMillis() - 500) {
            damageTimer = System.currentTimeMillis();
            this.jailer = jailer.getName();
            beatDownHealth -= amount;

            // Health cannot go below 0
            if (beatDownHealth < 0)
                beatDownHealth = 0;
        }

        return beatDownHealth;
    }

    /**
     * Sets the beat down health of the player
     *
     * @param amount beat down health
     */
    public void setBeatDownHealth(int amount) {
        beatDownHealth = amount;

        // Health cannot go below 0
        if (amount < 0)
            beatDownHealth = 0;
    }

    /**
     * @return configuration data for the player
     */
    public ConfigurationSection getConfig() {
        return config.getConfig();
    }

    /**
     * Saves the player config
     */
    public void saveConfig() {
        config.saveConfig();
    }
}
