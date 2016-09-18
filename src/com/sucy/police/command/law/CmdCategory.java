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
 * Command for creating a new category
 */
public class CmdCategory implements ICommand {

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

        // Requires at least 2 arguments
        if (args.length >= 2) {

            String cat = args[0];
            for (int i = 1; i < args.length - 1; i++) {
                cat += " " + args[i];
            }

            Police police = (Police)plugin;
            police.addCategory(cat, ((Police) plugin).getStringSetting(SettingNodes.CAT_URL) + args[args.length - 1]);
            sender.sendMessage(ChatColor.DARK_GREEN + "The category has been registered successfully");
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
        return PermissionNodes.CATEGORY;
    }

    /**
     * @return arguments used by the command
     */
    @Override
    public String getArgsString() {
        return "<title> <URL>";
    }

    /**
     * @return command description
     */
    @Override
    public String getDescription() {
        return "Registers a new category";
    }
}
