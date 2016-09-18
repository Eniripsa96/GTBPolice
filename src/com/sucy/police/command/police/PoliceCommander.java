package com.sucy.police.command.police;

import com.sucy.police.Police;
import com.sucy.police.command.CommandHandler;
import com.sucy.police.util.TextSizer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class PoliceCommander extends CommandHandler {

    private static final String[] COMMANDS = { "law", "prison", "record", "sensor", "wanted" };
    private static final String[] DESC = {
            "Displays laws for the server",
            "Manages the server prisons",
            "Views the record database",
            "Manages sensor blocks",
            "Manages the wanted list" };

    /**
     * Constructor
     *
     * @param plugin plugin reference
     */
    public PoliceCommander(Police plugin) {
        super(plugin, "Police - By Steven Sucy", "police");
    }

    /**
     * No commands to register
     */
    @Override
    protected void registerCommands() { }

    @Override
    public void displayUsage (CommandSender sender, int page) {

        // Get the maximum length
        int maxSize = 0;
        for (String key : COMMANDS) {
            int size = TextSizer.measureString(key);
            if (size > maxSize) maxSize = size;
        }
        maxSize += 4;

        sender.sendMessage(BREAK);
        sender.sendMessage(ChatColor.DARK_GREEN + title);
        for (int i = 0; i < COMMANDS.length; i++) {
            sender.sendMessage(ChatColor.GOLD + "/" + TextSizer.expand(COMMANDS[i], maxSize, false)
                + ChatColor.GRAY + "- " + DESC[i]);
        }
        sender.sendMessage(BREAK);
    }
}
