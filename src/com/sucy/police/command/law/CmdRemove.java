package com.sucy.police.command.law;

import com.sucy.police.PermissionNodes;
import com.sucy.police.Police;
import com.sucy.police.command.CommandHandler;
import com.sucy.police.command.ICommand;
import com.sucy.police.config.SettingNodes;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * Adds a new law to the database
 */
public class CmdRemove implements ICommand {

    /**
     *
     * @param handler command handler
     * @param plugin  plugin reference
     * @param sender  sender of the command
     * @param args    command arguments
     */
    @Override
    public void execute(CommandHandler handler, Plugin plugin, CommandSender sender, String[] args) {

        // Requires 1 argument
        if (args.length == 1) {
            Police police = (Police)plugin;
            String url = police.getURL(args[0]);

            if (url == null) {
                sender.sendMessage(ChatColor.DARK_RED + "That law cannot be found");
            }
            else {
                police.removeLaw(args[0]);
                sender.sendMessage(ChatColor.DARK_GREEN + "The law has been removed");
            }
        }

        // command usage otherwise
        else {
            handler.displayUsage(sender);
        }
    }

    /**
     * @return permission node required for the command
     */
    @Override
    public String getPermissionNode() {
        return PermissionNodes.REMOVE_LAW;
    }

    /**
     * @return arguments used by the command
     */
    @Override
    public String getArgsString() {
        return "<law>";
    }

    /**
     * @return command description
     */
    @Override
    public String getDescription() {
        return "Removes a law";
    }
}
