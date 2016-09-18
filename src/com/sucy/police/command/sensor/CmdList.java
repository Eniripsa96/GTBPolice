package com.sucy.police.command.sensor;

import com.sucy.police.PermissionNodes;
import com.sucy.police.Police;
import com.sucy.police.command.CommandHandler;
import com.sucy.police.command.ICommand;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * Command to display a list of registered sensor blocks
 */
public class CmdList implements ICommand {

    private static final String BREAK = ChatColor.DARK_GRAY + "-----------------------------------------------------";

    /**
     * Displays the wanted list
     *
     * @param handler command handler
     * @param plugin  plugin reference
     * @param sender  sender of the command
     * @param args    command arguments
     */
    @Override
    public void execute(CommandHandler handler, Plugin plugin, CommandSender sender, String[] args) {

        // Needs to be a player
        if (sender instanceof Player) {

            List<Location> locations = ((Police)plugin).getSensorLocations((Player)sender);

            // No sensor blocks
            if (locations.size() == 0) {
                sender.sendMessage(ChatColor.DARK_RED + "You have no sensors placed");
            }

            // Display the list
            else {

                // Get the page
                int page = 0;
                try {
                    if (args.length > 0) {
                        page = Integer.parseInt(args[0]);
                    }
                }
                catch (Exception e) {
                    // Do nothing
                }

                // Get the max page
                int max = (locations.size() + 6) / 7;

                // Limit the page
                if (page > max) page = max;
                if (page < 1) page = 1;

                // Display the sensors
                sender.sendMessage(BREAK);
                sender.sendMessage(ChatColor.DARK_GREEN + "Placed Sensors " + (max > 1 ? ChatColor.GRAY + "(Page " + page + "/" + max + ")" : ""));
                for (int i = page * 7 - 7; i < page * 7 && i < locations.size(); i++) {
                    Location loc = locations.get(i);
                    sender.sendMessage("[" + i + "] "
                            + ChatColor.GRAY + "(" + ChatColor.GOLD + loc.getBlockX()
                            + ChatColor.GRAY + ", " + ChatColor.GOLD + loc.getBlockY()
                            + ChatColor.GRAY + ", " + ChatColor.GOLD + loc.getBlockZ()
                            + ChatColor.GRAY + ")");
                }
                sender.sendMessage(BREAK);
            }
        }

        // Not a player
        else {
            sender.sendMessage(ChatColor.DARK_RED + "This command cannot be used by the console");
        }
    }

    /**
     * @return permission node required for the command
     */
    @Override
    public String getPermissionNode() {
        return PermissionNodes.SENSOR;
    }

    /**
     * @return arguments used by the command
     */
    @Override
    public String getArgsString() {
        return "[page]";
    }

    /**
     * @return command description
     */
    @Override
    public String getDescription() {
        return "Displays placed sensors";
    }
}
