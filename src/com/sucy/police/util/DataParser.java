package com.sucy.police.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

/**
 * Parses config data into various values
 */
public class DataParser {

    /**
     * Parses a location from a string
     *
     * @param data data to parse
     * @return     location or null if invalid input
     */
    public static Location parseLocation(String data) {
        String[] pieces = data.split(",");

        try {
            // Try to parse a location from the data
            if (pieces.length == 6) {
                return new Location(Bukkit.getWorld(pieces[0]),
                        Double.parseDouble(pieces[1]),
                        Double.parseDouble(pieces[2]),
                        Double.parseDouble(pieces[3]),
                        Float.parseFloat(pieces[4]),
                        Float.parseFloat(pieces[5]));
            }

            // Try to parse a simple location
            else if (pieces.length == 4) {
                return new Location(Bukkit.getWorld(pieces[0]),
                        Integer.parseInt(pieces[1]),
                        Integer.parseInt(pieces[2]),
                        Integer.parseInt(pieces[3]));
            }

            // Invalid format
            else {
                return null;
            }
        }

        // Return null if failed to parse
        catch (Exception e) {
            return null;
        }
    }

    /**
     * Serializes a location into a string
     *
     * @param loc location to serialize
     * @return    serialized string
     */
    public static String serializeLocation(Location loc) {

        // Null locations will clear the config value
        if (loc == null)
            return null;

        // Otherwise include all necessary data
        return loc.getWorld().getName() + ","
                + loc.getX() + ","
                + loc.getY() + ","
                + loc.getZ() + ","
                + loc.getYaw() + ","
                + loc.getPitch();
    }

    /**
     * Serializes a location in a simple format
     *
     * @param loc location to serialize
     * @return    data string
     */
    public static String serializeSimpleLocation(Location loc) {
        // Null locations will clear the config value
        if (loc == null)
            return null;

        // Otherwise include all necessary data
        return loc.getWorld().getName() + ","
                + loc.getBlockX() + ","
                + loc.getBlockY() + ","
                + loc.getBlockZ();
    }

    public static void serializeItem(ItemStack item, ConfigurationSection config) {
        config.set("mat", item.getType().name());
        config.set("amt", item.getAmount());

        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasDisplayName()) {
                config.set("name", meta.getDisplayName());
            }
            if (meta.hasEnchants()) {
                ConfigurationSection enchants = config.createSection("enchants");
                for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
                    enchants.set(entry.getKey().getName(), entry.getValue());
                }
            }
        }
    }

    public static ItemStack parseItem(ConfigurationSection config) {
        Material mat = Material.getMaterial(config.getString("mat"));
        int amount = config.getInt("amt");

        ItemStack item = new ItemStack(mat, amount);

        if (config.contains("name")) {
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(config.getString("name"));
            item.setItemMeta(meta);
        }

        if (config.contains("enchants")) {
            for (String key : config.getConfigurationSection("enchants").getKeys(false)) {
                Enchantment enchant = Enchantment.getByName(key);
                int level = config.getInt("enchants." + key);
                item.addEnchantment(enchant, level);
            }
        }

        return item;
    }
}
