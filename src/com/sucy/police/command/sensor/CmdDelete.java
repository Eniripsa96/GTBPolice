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
 * Command to delete a sensor block
 */
public class CmdDelete implements ICommand {

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
        if (sender instanceof Player && args.length == 1) {

            List<Location> locations = ((Police)plugin).getSensorLocations((Player)sender);

            // Get the ID
            int index = -1;
            try {
                index = Integer.parseInt(args[0]);
            }
            catch (Exception ex) {
                // Do nothing
            }

            // Invalid ID
            if (index < 0 || index >= locations.size()) {
                sender.sendMessage(ChatColor.DARK_RED + "Invalid sensor ID");
            }

            // Delete the block
            else {
                ((Police)plugin).deleteSensor((Player)sender, index);
                sender.sendMessage(ChatColor.DARK_GREEN + "The sensor block has been deleted");
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
        return "<id>";
    }

    /**
     * @return command description
     */
    @Override
    public String getDescription() {
        return "Deletes a sensor";
    }
}
