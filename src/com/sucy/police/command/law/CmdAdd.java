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
import java.util.Set;

/**
 * Adds a new law to the database
 */
public class CmdAdd implements ICommand {

    /**
     *
     * @param handler command handler
     * @param plugin  plugin reference
     * @param sender  sender of the command
     * @param args    command arguments
     */
    @Override
    public void execute(CommandHandler handler, Plugin plugin, CommandSender sender, String[] args) {

        // Requires 2 arguments
        if (args.length == 2) {

            Police police = (Police)plugin;
            Set<String> laws = police.getLaws(args[0]);

            if (laws == null) {
                sender.sendMessage(ChatColor.DARK_RED + "That is not a valid category!");
                return;
            }

            String url = ((Police)plugin).getStringSetting(SettingNodes.LAW_URL) + args[1];
            ((Police)plugin).addLaw(args[0], url);
            sender.sendMessage(ChatColor.DARK_GREEN + "The law has been registered successfully");
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
        return PermissionNodes.ADD_LAW;
    }

    /**
     * @return arguments used by the command
     */
    @Override
    public String getArgsString() {
        return "<category> <URL>";
    }

    /**
     * @return command description
     */
    @Override
    public String getDescription() {
        return "Registers a new law";
    }
}
