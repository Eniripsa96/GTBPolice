package com.sucy.police.command.law;

import com.sucy.police.PermissionNodes;
import com.sucy.police.Police;
import com.sucy.police.command.CommandHandler;
import com.sucy.police.command.ICommand;
import com.sucy.police.config.SettingNodes;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

/**
 * Adds a new law to the database
 */
public class CmdEdit implements ICommand {

    /**
     *
     * @param handler command handler
     * @param plugin  plugin reference
     * @param sender  sender of the command
     * @param args    command arguments
     */
    @Override
    public void execute(CommandHandler handler, Plugin plugin, CommandSender sender, String[] args) {

        // Requires at least one argument
        if (args.length > 0) {

            Police police = (Police)plugin;

            // URL
            if (args[0].equalsIgnoreCase("url")) {

                // Requires 3 arguments
                if (args.length == 3) {

                    // Category
                    if (args[1].length() == 1) {
                        String url = police.getCatUrl(args[1]);

                        // Category not found
                        if (url == null) {
                            sender.sendMessage(ChatColor.DARK_RED + "The law cannot be found");
                        }

                        // Update the URL
                        else {
                            police.setCatUrl(args[1], police.getStringSetting(SettingNodes.CAT_URL) + args[2]);
                            sender.sendMessage(ChatColor.DARK_GREEN + "The category URL has been updated successfully");
                        }
                    }

                    // Law
                    else {
                        String url = police.getURL(args[1]);

                        // Law not found
                        if (url == null) {
                            sender.sendMessage(ChatColor.DARK_RED + "The law cannot be found");
                        }

                        // Set the URL
                        else {
                            police.setLawUrl(args[1], police.getStringSetting(SettingNodes.LAW_URL) + args[2]);
                            sender.sendMessage(ChatColor.DARK_GREEN + "The law URL has been updated successfully");
                        }
                    }
                }

                // Usage otherwise
                else {
                    sender.sendMessage(ChatColor.DARK_GREEN + "Edit URL Command Usage");
                    sender.sendMessage(ChatColor.GOLD + "/law edit url " + ChatColor.LIGHT_PURPLE + "<law> <URL>");
                    sender.sendMessage(ChatColor.GOLD + "/law edit url " + ChatColor.LIGHT_PURPLE + "<category> <URL>");
                }
            }

            // Titles
            else if (args[0].equalsIgnoreCase("title")) {

                // Requires at least 3 arguments
                if (args.length >= 3 && args[1].length() == 1) {

                    String title = args[2];
                    for (int i = 3; i < args.length; i++) {
                        title += " " + args[i];
                    }

                    String temp = police.getCatTitle(args[1]);

                    // Category not found
                    if (temp == null) {
                        sender.sendMessage(ChatColor.DARK_RED + "The category cannot be found");
                    }

                    // Set the title
                    else {
                        police.setCatTitle(args[1], title);
                        sender.sendMessage(ChatColor.DARK_GREEN + "The category title has been updated successfully");
                    }
                }

                // Usage otherwise
                else {
                    sender.sendMessage(ChatColor.DARK_GREEN + "Edit Title Command Usage");
                    sender.sendMessage(ChatColor.GOLD + "/law edit title " + ChatColor.LIGHT_PURPLE + "<category> <title>");
                }
            }

            // Unknown
            else {
                handler.displayUsage(sender);
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
        return PermissionNodes.EDIT;
    }

    /**
     * @return arguments used by the command
     */
    @Override
    public String getArgsString() {
        return "<" + ChatColor.GOLD + "url" + ChatColor.LIGHT_PURPLE
                + "|" + ChatColor.GOLD + "title" + ChatColor.LIGHT_PURPLE + ">";
    }

    /**
     * @return command description
     */
    @Override
    public String getDescription() {
        return "Edits laws and categories";
    }
}
