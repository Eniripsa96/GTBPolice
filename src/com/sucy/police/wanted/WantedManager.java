package com.sucy.police.wanted;

import com.sucy.police.Police;
import com.sucy.police.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Manages the wanted list, keeping track of the attached crimes and priorities
 */
public class WantedManager {

    private final HashMap<String, WantedPlayer> players = new HashMap<String, WantedPlayer>();
    private final HashSet<String> viewing = new HashSet<String>();
    private final Police plugin;
    private final Config config;

    private Scoreboard wantedList;
    private Objective obj;

    /**
     * Constructor
     *
     * @param plugin plugin reference
     */
    public WantedManager(Police plugin) {
        this.plugin = plugin;
        config = new Config(plugin, "wanted");

        // Load player data
        for (String player : config.getConfig().getKeys(false)) {
            players.put(player, new WantedPlayer(player, config.getConfig()));
        }

        updateScoreboard();
    }

    /**
     * Checks if the wanted list has entries
     *
     * @return true if there are entries in the list, false otherwise
     */
    public boolean hasEntries() {
        return players.size() > 0;
    }

    /**
     * Adds a player to the wanted list
     *
     * @param player   player name
     * @param crime    crime committed
     * @param priority crime priority
     * @return         true if could add them, false otherwise
     */
    public boolean addPlayer(String player, String reporter, String crime, int priority) {

        // Use lowercase to prevent the same player from being added multiple times
        player = player.toLowerCase();

        // Cannot add a player multiple times
        if (players.containsKey(player)) {
            return false;
        }

        // Add the player
        players.put(player, new WantedPlayer(player, reporter, crime, priority));

        // Update the scoreboard
        updateScoreboard();

        return true;
    }

    /**
     * Removes a player from the wanted list
     *
     * @param player player to remove
     * @return       true if removed successfully, false otherwise
     */
    public boolean removePlayer(String player) {

        // Use lowercase to make sure its the correct format
        player = player.toLowerCase();

        // Cannot remove a player that isn't on the list
        if (!players.containsKey(player)) {
            return false;
        }

        // Remove the player
        players.remove(player);
        config.getConfig().set(player, null);

        // Update the scoreboard
        updateScoreboard();

        return true;
    }

    /**
     * Retrieves a player from the wanted list
     *
     * @param player player name
     * @return       wanted player data
     */
    public WantedPlayer getPlayer(String player) {
        return players.get(player.toLowerCase());
    }

    /**
     * Updates the wanted list scoreboard
     */
    public void updateScoreboard() {

        // Get the wanted players in descending priority order
        ArrayList<WantedPlayer> wanted = new ArrayList<WantedPlayer>(players.values());
        Collections.sort(wanted);

        // Initialize the scoreboard if needed
        if (wantedList == null) {
            wantedList = Bukkit.getScoreboardManager().getNewScoreboard();
        }

        // Reset the objective if necessary
        if (obj != null) {
            obj.unregister();
        }

        // Create a new objective
        obj = wantedList.registerNewObjective(ChatColor.DARK_GREEN + "" + ChatColor.UNDERLINE + "Wanted  List", "dummy");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        // Add the top 15 targets
        for (int i = 0; i < 15 && i < wanted.size(); i++) {
            WantedPlayer player = wanted.get(i);
            obj.getScore(Bukkit.getOfflinePlayer(player.getPlayerName())).setScore(player.getPriority());
        }
    }

    /**
     * Displays the wanted list scoreboard to the given player
     *
     * @param player player to display the scoreboard to
     * @return       true if could show them, false otherwise
     */
    public boolean viewScoreboard(final Player player) {

        // Cannot view it when it is already being viewed
        if (player == null || viewing.contains(player.getName())) {
            return false;
        }

        // Prevent player from viewing it multiple times simultaneously
        viewing.add(player.getName());

        // Set the scoreboard
        player.setScoreboard(wantedList);

        // Set a task to expire after 10 seconds
        plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                viewing.remove(player.getName());
                if (player.isOnline()) {
                    player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                }
            }
        }, 200);

        return true;
    }

    /**
     * Saves the wanted list data before closing
     */
    public void save() {
        for (WantedPlayer player : players.values()) {
            player.save(config.getConfig());
        }
        config.saveConfig();
    }
}
