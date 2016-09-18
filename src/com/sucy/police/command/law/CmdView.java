package com.sucy.police.command.law;

import com.sucy.police.PermissionNodes;
import com.sucy.police.Police;
import com.sucy.police.command.CommandHandler;
import com.sucy.police.command.ICommand;
import com.sucy.police.util.LinkGenerator;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.awt.*;
import java.net.URI;

/**
 * Command for viewing the law thread pages
 */
public class CmdView implements ICommand {

    /**
     *
     * @param handler command handler
     * @param plugin  plugin reference
     * @param sender  sender of the command
     * @param args    command arguments
     */
    @Override
    public void execute(CommandHandler handler, Plugin plugin, CommandSender sender, String[] args) {

        // Requires one argument
        if (args.length == 1) {
            Police police = (Police)plugin;
            String url = args[0].length() == 1 ? police.getCatUrl(args[0]) : police.getURL(args[0]);

            // No law found
            if (url == null) {
                sender.sendMessage(ChatColor.DARK_RED + "No law exists with the name: " + ChatColor.GOLD + args[0]);
            }

            // View law thread page
            else {
                sender.sendMessage(ChatColor.GRAY + "Details for the " + (args[0].length() == 1 ? "category" : "law") + " can be found here:");
                sender.sendMessage(ChatColor.GOLD + url);
            }
        }

        // Command Usage otherwise
        else handler.displayUsage(sender);
    }

    /**
     * @return permission node required for the command
     */
    @Override
    public String getPermissionNode() {
        return PermissionNodes.LIST_LAW;
    }

    /**
     * @return arguments used by the command
     */
    @Override
    public String getArgsString() {
        return "<law|category>";
    }

    /**
     * @return command description
     */
    @Override
    public String getDescription() {
        return "Displays the law/category link";
    }
}
