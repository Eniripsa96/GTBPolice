package com.sucy.police;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Task for checking prisoner release times
 */
public class PrisonTask extends BukkitRunnable {

    private Police plugin;

    /**
     * Constructor
     *
     * @param plugin plugin reference
     */
    public PrisonTask(Police plugin) {
        this.plugin = plugin;
        runTaskTimer(plugin, 100, 100);
    }

    /**
     * Checks for prisoner releasing every so often
     */
    @Override
    public void run() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (plugin.isInPrison(player.getName()) == PrisonState.EXPIRED) {
                plugin.freePrisoner(player.getName());
            }
        }
    }
}
