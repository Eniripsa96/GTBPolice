package com.sucy.police.command.law;

import com.sucy.police.PermissionNodes;
import com.sucy.police.Police;
import com.sucy.police.command.CommandHandler;
import com.sucy.police.command.ICommand;
import com.sucy.police.util.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.*;

/**
 * Command for displaying the list of laws
 */
public class CmdList implements ICommand {

    private static final String BREAK = ChatColor.DARK_GRAY + "-----------------------------------------------------";
    private static final String PREFIX = ChatColor.DARK_GRAY + "| ";

    /**
     * Executes the command
     *
     * @param handler command handler
     * @param plugin  plugin reference
     * @param sender  sender of the command
     * @param args    command arguments
     */
    @Override
    public void execute(CommandHandler handler, Plugin plugin, CommandSender sender, String[] args) {

        Police police = (Police)plugin;

        // No arguments is a list of categories
        if (args.length == 0 || (args.length == 1 && TypeChecker.isInteger(args[0]))) {
            int page = 1;
            if (args.length == 1) {
                page = Integer.parseInt(args[0]);
            }
            List<String> cats = new ArrayList<String>(((Police)plugin).getCategories());
            Collections.sort(cats);
            sender.sendMessage(BREAK);
            int max = (cats.size() + 5) / 6;
            if (page < 1) {
                page = 1;
            }
            if (page > max) {
                page = max;
            }
            sender.sendMessage(PREFIX + ChatColor.DARK_GREEN + "Law Categories" + ChatColor.GRAY + " (Page " + page + "/" + max + ")");
            sender.sendMessage(PREFIX);
            int index = -1;
            for (String cat : cats) {
                index++;
                if (index < page * 6 - 6 || index >= page * 6) continue;
                sender.sendMessage(PREFIX + ChatColor.GOLD + cat.toUpperCase() + ChatColor.GRAY + " - " + police.getCatTitle(cat));
            }
            sender.sendMessage(BREAK);
        }

        // One argument is a list of laws in that category
        else if (args.length == 1 || args.length == 2) {

            int page = 1;
            if (args.length == 2) {
                try {
                    page = Integer.parseInt(args[1]);
                }
                catch (Exception ex) {
                    // Do nothing
                }
            }

            // Get the laws
            Set<String> laws = police.getLaws(args[0]);

            // Make sure there are laws
            if (laws == null || laws.size() == 0) {
                sender.sendMessage(ChatColor.DARK_RED + "No laws were found for the category: " + ChatColor.GOLD + args[0]);
                return;
            }

            // Limit the page
            int max = (laws.size() + 5) / 6;
            if (page > max) {
                page = max;
            }
            if (page < 1) {
                page = 1;
            }

            // Get the laws in numerical order
            List<String> list = new ArrayList<String>(laws.size());
            int i = -1;
            while (list.size() < laws.size()) {
                i++;
                for (String law : laws) {
                    int id = Integer.parseInt(law.substring(1));
                    if (id == i) {
                        list.add(law);
                        break;
                    }
                }
            }

            // Output
            sender.sendMessage(BREAK);
            sender.sendMessage(PREFIX + ChatColor.DARK_GREEN + "Laws in the category - " + ChatColor.GOLD + args[0].toUpperCase()
                    + ChatColor.GRAY + " (Page " + page + "/" + max + ")");
            sender.sendMessage(PREFIX);
            int index = -1;
            for (String law : list) {
                index++;
                if (index < page * 6 - 6 || index >= page * 6) continue;
                sender.sendMessage(PREFIX + ChatColor.GOLD + law + ChatColor.GRAY + " - " + police.getLawName(law));
            }
            sender.sendMessage(BREAK);
        }

        // Otherwise show command usage
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
        return "[category]";
    }

    /**
     * @return command description
     */
    @Override
    public String getDescription() {
        return "Displays a list of laws";
    }
}
