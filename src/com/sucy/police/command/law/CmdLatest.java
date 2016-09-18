package com.sucy.police.command.law;

import com.sucy.police.PermissionNodes;
import com.sucy.police.Police;
import com.sucy.police.command.CommandHandler;
import com.sucy.police.command.ICommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * Command to display the latest laws added
 */
public class CmdLatest implements ICommand {

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
        if (args.length == 0) {
            List<String> latest = police.getLatest();
            sender.sendMessage(BREAK);
            sender.sendMessage(PREFIX + ChatColor.DARK_GREEN+ "Latest Laws Added");
            sender.sendMessage(PREFIX);
            for (String law : latest) {
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
        return "";
    }

    /**
     * @return command description
     */
    @Override
    public String getDescription() {
        return "Displays latest laws";
    }
}
