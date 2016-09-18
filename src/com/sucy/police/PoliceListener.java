package com.sucy.police;

import com.sucy.police.config.Config;
import com.sucy.police.config.LogNodes;
import com.sucy.police.config.SettingNodes;
import com.sucy.police.report.ProgressState;
import com.sucy.police.report.Report;
import com.sucy.police.report.ReportProgress;
import com.sucy.police.util.DataParser;
import com.sucy.police.util.TextSplitter;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Listens to various events for the plugin
 */
public class PoliceListener implements Listener {

    private static final DateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private static final String BREAK = ChatColor.DARK_GRAY + "-----------------------------------------------------";
    private static final String PREFIX = ChatColor.DARK_GRAY + "|  ";
    private static final String STUN_META = "stun";

    private final HashMap<String, ReportProgress> reporters = new HashMap<String, ReportProgress>();
    private final HashMap<Location, String> stunBlocks = new HashMap<Location, String>();
    private final HashMap<String, Long> stunned = new HashMap<String, Long>();
    private final HashMap<Location, String> logBlocks = new HashMap<Location, String>();
    private final HashMap<Location, String> logOwners = new HashMap<Location, String>();
    private final HashMap<String, Long> logCd = new HashMap<String, Long>();
    private final Config logConfig;
    private final Police plugin;
    private final Permission permission;

    private boolean addMeta;

    /**
     * Constructor
     *
     * @param plugin plugin reference
     */
    public PoliceListener(Police plugin) {
        this.plugin = plugin;
        addMeta = false;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        logConfig = new Config(plugin, "log-blocks");

        // Load log block data
        if (logConfig.getConfig().contains(LogNodes.BLOCKS)) {
            ConfigurationSection config = logConfig.getConfig().getConfigurationSection(LogNodes.BLOCKS);
            for (String key : config.getKeys(false)) {
                logBlocks.put(DataParser.parseLocation(key), config.getString(key));
            }
        }
        if (logConfig.getConfig().contains(LogNodes.OWNERS)) {
            ConfigurationSection config = logConfig.getConfig().getConfigurationSection(LogNodes.OWNERS);
            for (String key : config.getKeys(false)) {
                logOwners.put(DataParser.parseLocation(key), config.getString(key));
            }
        }

        // Hook into vault permissions
        RegisteredServiceProvider<Permission> permissionProvider = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        else throw new IllegalStateException("Vault with a registered permission plugin is required for this plugin to work");
    }

    /**
     * Saves data before termination
     */
    public void save() {
        ConfigurationSection blocks = logConfig.getConfig().createSection(LogNodes.BLOCKS);
        for (Map.Entry<Location, String> entry : logBlocks.entrySet()) {
            blocks.set(DataParser.serializeSimpleLocation(entry.getKey()), entry.getValue());
        }
        ConfigurationSection owners = logConfig.getConfig().createSection(LogNodes.OWNERS);
        for (Map.Entry<Location, String> entry : logOwners.entrySet()) {
            owners.set(DataParser.serializeSimpleLocation(entry.getKey()), entry.getValue());
        }
        logConfig.saveConfig();
    }

    /**
     * Gets the sensor locations for a player
     *
     * @param player player to get it for
     * @return       sensor location list
     */
    public List<Location> getSensorLocations(Player player) {
        List<Location> list = new ArrayList<Location>();
        for (Map.Entry<Location, String> entry : logOwners.entrySet()) {
            if (entry.getValue().equals(player.getName())) {
                list.add(entry.getKey());
            }
        }
        return list;
    }

    /**
     * Deletes a sensor for the player
     *
     * @param player player to delete for
     * @param id     sensor ID
     */
    public void deleteSensor(Player player, int id) {
        int index = -1;
        for (Map.Entry<Location, String> entry : logOwners.entrySet()) {
            if (entry.getValue().equals(player.getName())) {
                index++;
                if (index == id) {
                    logOwners.remove(entry.getKey());
                    logBlocks.remove(entry.getKey());
                    entry.getKey().getBlock().setType(Material.AIR);
                    return;
                }
            }
        }
    }

    /**
     * Checks for damage via a baton
     *
     * @param event event details
     */
    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {

        // Get the attacking player if it is one
        Player attacker = null;
        if (event.getDamager() instanceof Player) attacker = (Player)event.getDamager();
        if (event.getDamager() instanceof Projectile && ((Projectile)event.getDamager()).getShooter() instanceof Player) {
            attacker = (Player)((Projectile)event.getDamager()).getShooter();
        }

        // Cancel event if PvP Banned
        if (attacker != null && plugin.isPvPBanned(attacker.getName())) {
            event.setCancelled(true);
            return;
        }

        // Hit by a snowball
        if (event.getDamager() instanceof Snowball && event.getEntity() instanceof Player) {

            Snowball snowball = (Snowball)event.getDamager();
            Player player = (Player)event.getEntity();

            // Must be a stun grenade
            if (snowball.hasMetadata(STUN_META)) {
                stunned.put(player.getName(), System.currentTimeMillis() + plugin.getIntSetting(SettingNodes.STUN_TIME) * 1000);
                player.sendMessage(ChatColor.DARK_GREEN + "You have been stunned");
                if (player.isFlying()) {
                    player.setFlying(false);
                }
                player.setAllowFlight(false);
            }
        }

        // Both the target and the attacker must be players
        if (attacker == null || !(event.getEntity() instanceof Player)) {
            return;
        }

        // Must be a regular attack
        else if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            return;
        }

        Player target = (Player)event.getEntity();

        // Attacker needs the permission
        if (!attacker.hasPermission(PermissionNodes.BATON)) {
            return;
        }

        // Attacker needs a baton
        ItemStack item = attacker.getItemInHand();
        if (item == null || item.getType() != Material.STICK || !item.containsEnchantment(Enchantment.ARROW_KNOCKBACK)) {
            return;
        }

        // Deal no actual damage with a baton
        event.setDamage(0);

        // Deal beat down damage
        int prev = plugin.getPlayerData(target.getName()).getBeatDownHealth();
        int remaining = plugin.getPlayerData(target.getName()).damage(plugin.getIntSetting(SettingNodes.DAMAGE), attacker);

        // If there's no remaining health, the player is jailed
        if (remaining == 0) {
            arrest(target, attacker);
        }

        // Otherwise inform of the changes
        else if (prev != remaining) {
            plugin.addWatcher(target, attacker);
        }
    }

    /**
     * Arrests a player
     *
     * @param target target to arrest
     * @param police player doing the arresting
     */
    private void arrest(OfflinePlayer target, Player police) {

        // Eject from vehicle to prevent exploits
        if (target.isOnline()) {
            Player player = plugin.getServer().getPlayer(target.getName());
            if (player.isInsideVehicle()) {
                player.getVehicle().eject();
            }
        }

        // Remove from wanted list
        plugin.getWantedManager().removePlayer(target.getName());

        // Run commands
        ReportProgress report = new ReportProgress(plugin, police.getName(), target.getName());
        for (String command : plugin.getStringListSetting(SettingNodes.PRE_COMMANDS)) {
            plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), filterCommand(command, report));
        }

        // Start the question process
        reporters.put(police.getName().toLowerCase(), report);
        plugin.getServer().dispatchCommand(police, "global off");
        sendMessage(police, report.getState().getQuestion(), report.getState().getPart());

        // Stop displaying the beat down health bar
        plugin.getPlayerData(target.getName()).setBeatDownHealth(plugin.getIntSetting(SettingNodes.HEALTH));
        plugin.clearPlayer(police.getName());
    }

    /**
     * Cancels async events when filling out a report
     *
     * @param event event details
     */
    @EventHandler (priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        if (reporters.containsKey(event.getPlayer().getName().toLowerCase())) {
            event.setCancelled(true);
        }
    }

    /**
     * Listens for responses to a question
     *
     * @param event event details
     */
    @EventHandler (priority = EventPriority.LOWEST)
    public void onChat(PlayerChatEvent event) {

        // Must be filing out a report
        if (!reporters.containsKey(event.getPlayer().getName().toLowerCase())) {
            return;
        }

        ReportProgress report = reporters.get(event.getPlayer().getName().toLowerCase());

        // Cancel the report with the keywords
        if (event.getMessage().equalsIgnoreCase("cancel")
                || event.getMessage().equalsIgnoreCase("stop")
                || event.getMessage().equalsIgnoreCase("quit")
                || (event.getMessage().equalsIgnoreCase("none") && report.getState() == ProgressState.RULE)) {
            cancelJailing(event.getPlayer());
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "You have cancelled the jailing process");
            return;
        }

        // Reports are filed via a book instead of the chat
        if (report.getState() == ProgressState.REPORT) {
            return;
        }

        // Apply the report entry
        applyReportEntry(event.getPlayer(), event.getMessage());
        event.setCancelled(true);
    }

    /**
     * Listens for a detailed report being finished
     *
     * @param event event details
     */
    @EventHandler
    public void onBookFilled(PlayerEditBookEvent event) {

        BookMeta meta = event.getNewBookMeta();

        // Must be a reporter
        if (!reporters.containsKey(event.getPlayer().getName().toLowerCase())) {
            return;
        }

        // Apply the report
        applyReportEntry(event.getPlayer(), meta.getPage(1));
    }

    /**
     * Applies a report entry for a player
     *
     * @param player  player to apply for
     * @param message message to apply
     */
    private void applyReportEntry(final Player player, String message) {

        // Apply the entry
        ReportProgress report = reporters.get(player.getName().toLowerCase());
        boolean valid = report.applyResponse(message);

        // Valid response, display the next question or end the report
        if (valid) {

            // Provide a book when needed
            if (report.getState() == ProgressState.REPORT) {
                ItemStack book = new ItemStack(Material.BOOK_AND_QUILL);
                player.getInventory().addItem(book);
            }

            // End the report
            else if (report.getState() == ProgressState.FINISHED) {

                // Stop filtering their chat
                plugin.getServer().dispatchCommand(player, "global on");
                reporters.remove(player.getName().toLowerCase());

                plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
                    @Override
                    public void run() {
                        player.setItemInHand(new ItemStack(Material.AIR));
                        player.updateInventory();
                    }
                }, 1);

                // Execute commands accordingly

                // Jail Commands
                for (String command : plugin.getStringListSetting(SettingNodes.JAIL_COMMANDS)) {
                    command = filterCommand(command, report);
                    plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
                }

                // Report recording
                Report result = new Report(plugin.getPlayerData(report.getTargetName()),
                        report.getTargetName(),
                        report.getPlayerName(),
                        report.getReport(),
                        report.getRule(),
                        report.getTime(),
                        report.isOpen());
            }

            sendMessage(player, report.getState().getQuestion().replace("%r", report.getReport()), report.getState().getPart());
        }

        // Invalid responses
        else {
            sendMessage(player, report.getState().getErrorQuestion(), report.getState().getPart());
        }
    }

    /**
     * Sends a formatted question message to a player
     *
     * @param player  player to send to
     * @param message report question message
     */
    private void sendMessage(Player player, String message, int part) {

        // Spacing beforehand
        for (int i = 0; i < 10; i++) {
            player.sendMessage(ChatColor.BLACK + "|");
        }

        // Header
        player.sendMessage(BREAK);
        player.sendMessage(PREFIX);
        player.sendMessage(PREFIX + ChatColor.GOLD + "Part " + part + ":");
        player.sendMessage(PREFIX);

        // Formatted message
        List<String> messageLines = TextSplitter.getLines(message, 38);
        for (String line : messageLines) {
            player.sendMessage(PREFIX + ChatColor.DARK_GREEN + line);
        }

        // Spacing afterwards
        for (int i = 0;  i < 5 - messageLines.size(); i++) {
            player.sendMessage(PREFIX);
        }

        player.sendMessage(BREAK);
    }

    /**
     * Filters a command using a report progress
     *
     * @param command command to filter
     * @param report  report progress
     * @return        filtered command
     */
    public String filterCommand(String command, ReportProgress report) {

        Pattern randomRegex = Pattern.compile("%r[0-9]+");
        Matcher match = randomRegex.matcher(command);
        int size = command.length();
        while (match.find()) {
            int value = Integer.parseInt(match.group().substring(2));
            command = command.substring(0, match.start() + command.length() - size)
                    + ((int)(Math.random() * value) + 1)
                    + command.substring(match.end() + command.length() - size);
        }

        command = command.replace("%p", report.getTargetName());
        command = command.replace("%j", report.getPlayerName());
        command = command.replace("%t", report.getTime() + "m");
        command = command.replace("%l", report.getRule());
        command = command.replace('&', ChatColor.COLOR_CHAR);
        return command;
    }

    /**
     * Clear watching data before leaving the game
     * Also applies stun effects
     *
     * @param event event details
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        // Cancel reports on quitting
        plugin.clearPlayer(event.getPlayer().getName());
        if (reporters.containsKey(event.getPlayer().getName().toLowerCase())) {
            cancelJailing(event.getPlayer());
        }

        // Stun effect upon logging out
        if (isStunned(event.getPlayer())) {
            Block block = event.getPlayer().getLocation().getBlock();
            block.setType(Material.WOOL);
            block.setData((byte) 14);
            stunBlocks.put(block.getLocation(), event.getPlayer().getName());
        }
    }

    /**
     * Clears marker block on rejoining
     *
     * @param event event details
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        // Remove stun blocks on rejoining
        Block block = event.getPlayer().getLocation().getBlock();
        if (stunBlocks.containsKey(block.getLocation())) {
            block.setType(Material.AIR);
            block.setData((byte) 0);
            stunBlocks.remove(block.getLocation());
        }

        // Offline teleport
        plugin.isInPrison(event.getPlayer().getName());
        Location loc = plugin.getOfflineTeleport(event.getPlayer());
        if (loc != null) {
            event.getPlayer().teleport(loc);

            if (plugin.isInPrison(event.getPlayer().getName()) == PrisonState.NOT_IN_PRISON) {
                Config inv = new Config(plugin, "inventory\\" + event.getPlayer().getName().toLowerCase());
                int index = 0;
                for (ItemStack item : event.getPlayer().getInventory().getContents()) {
                    if (item == null) continue;
                    DataParser.serializeItem(item, inv.getConfig().createSection("Item" + index++));
                }
                event.getPlayer().getInventory().clear();
            }
        }
    }

    /**
     * Removes the stun block from the map and cancels dropping items
     *
     * @param event event details
     */
    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {

        // Clear stun blocks when broken
        Location loc = event.getBlock().getLocation();
        if (stunBlocks.containsKey(loc)) {
            stunBlocks.remove(loc);
            event.setCancelled(true);
            event.getBlock().setType(Material.AIR);
        }

        // Clear sensor blocks when broken
        if (logBlocks.containsKey(event.getBlock().getLocation())) {
            logBlocks.remove(event.getBlock().getLocation());
            logOwners.remove(event.getBlock().getLocation());
            event.getPlayer().sendMessage(ChatColor.DARK_RED + "Sensor Block has been unregistered");
        }
    }

    /**
     * Cancels commands besides white-listed commands for prisoners and stunned players
     *
     * @param event event details
     */
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {

        String command = event.getMessage();

        // Single slashes don't apply
        if (command.length() < 2 || command.charAt(1) == ' ')
            return;

        // Trim the command
        int endIndex = command.length() - 1;
        if (command.contains(" ")) {
            endIndex = command.indexOf(" ") - 1;
        }
        command = command.substring(1, endIndex);

        // Must be in prison
        if (plugin.isInPrison(event.getPlayer().getName()) != PrisonState.NOT_IN_PRISON) {

            // Cancel the command if not white listed
            if (!plugin.isWhiteListed(command)) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.DARK_RED + "You do not have permission to type this command");
            }
        }

        // Must be stunned
        else if (isStunned(event.getPlayer())) {

            // Cancel the command if not white listed
            if (!plugin.isWhiteListed(command)) {
                event.setCancelled(true);
                int time = plugin.getIntSetting(SettingNodes.STUN_TIME) + (int)(stunned.get(event.getPlayer().getName()) - System.currentTimeMillis()) / 1000;
                event.getPlayer().sendMessage(ChatColor.DARK_RED + "You are stunned! No commands for " + ChatColor.GOLD + time + ChatColor.DARK_GREEN + " seconds");
            }
        }
    }

    /**
     * Modifies spawn location of prisoners
     *
     * @param event event details
     */
    @EventHandler (priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent event) {
        if (plugin.isInPrison(event.getPlayer().getName()) != PrisonState.NOT_IN_PRISON) {
            event.setRespawnLocation(plugin.getPrisonSpawn());

            File file = new File(plugin.getDataFolder().getAbsolutePath() + "\\inventory\\" + event.getPlayer().getName().toLowerCase() + ".yml");
            if (file.exists()) {
                Config inv = new Config(plugin, "inventory\\" + event.getPlayer().getName().toLowerCase());
                for (String key : inv.getConfig().getKeys(false)) {
                    event.getPlayer().getInventory().addItem(DataParser.parseItem(inv.getConfig().getConfigurationSection(key)));
                }
                file.delete();
            }
        }
    }

    /**
     * Clears stunned blocks
     */
    public void clearBlocks() {
        for (Location loc : stunBlocks.keySet()) {
            loc.getBlock().setType(Material.AIR);
            loc.getBlock().setData((byte)0);
        }
    }

    /**
     * Checks if a player is stunned, updating the map if a stun expires
     *
     * @param player player to check
     * @return       true if stunned, false otherwise
     */
    private boolean isStunned(Player player) {

        // Was stunned
        if (stunned.containsKey(player.getName())) {

            // time expired
            if (stunned.get(player.getName()) < System.currentTimeMillis()) {
                stunned.remove(player.getName());
                return false;
            }

            // Still stunned
            else return true;
        }

        // Not stunned
        else return false;
    }

    /**
     * Checks snowballs before they are thrown to determine what are stun grenades
     *
     * @param event event details
     */
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        addMeta = false;

        // Stun grenades
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            if (event.getPlayer().getItemInHand().getType() == Material.SNOW_BALL && event.getPlayer().hasPermission(PermissionNodes.STUN)
                    && event.getPlayer().getItemInHand().containsEnchantment(Enchantment.ARROW_KNOCKBACK)) {
                addMeta = true;
            }
        }

        // Stun blocks
        else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Location loc = event.getClickedBlock().getLocation();
            if (stunBlocks.containsKey(loc)
                    && event.getPlayer().getItemInHand().getType() == Material.STICK
                    && event.getPlayer().getItemInHand().containsEnchantment(Enchantment.ARROW_KNOCKBACK)
                    && event.getPlayer().hasPermission(PermissionNodes.BATON)) {
                arrest(plugin.getServer().getOfflinePlayer(stunBlocks.get(loc)), event.getPlayer());
                stunBlocks.remove(loc);
                loc.getBlock().setType(Material.AIR);
            }
        }

        // String
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getPlayer().hasPermission(PermissionNodes.SENSOR)) {
            if (event.getPlayer().getItemInHand().getType() == Material.STRING
                    && event.getPlayer().getItemInHand().containsEnchantment(Enchantment.ARROW_KNOCKBACK)) {

                int limit = plugin.getIntSetting(SettingNodes.SENSOR_LIMIT);
                String base = PermissionNodes.SENSOR + ".create.";
                int  max = 0;
                for (int i = limit; i > 0; i--) {
                    if (event.getPlayer().hasPermission(base + i)) {
                        max = i;
                        break;
                    }
                }
                if (max > 0) {
                    if (getSensorCount(event.getPlayer()) >= max) {
                        event.getPlayer().sendMessage(ChatColor.DARK_RED + "You cannot place any more sensor blocks");
                    }
                    else addMeta = true;
                }
            }
        }

        // Sensor Blocks

        // Apply cooldown restrictions
        int cd = plugin.getIntSetting(SettingNodes.LOG_CD);
        if (logCd.containsKey(event.getPlayer().getName())
                && logCd.get(event.getPlayer().getName()) > System.currentTimeMillis() - cd * 1000) {
            return;
        }

        // Make sure it is actually a sensor block
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getPlayer().hasPermission(PermissionNodes.SENSOR)) {
            if (logBlocks.containsKey(event.getClickedBlock().getLocation())) {

                // Get the config settings
                List<String> permissions = plugin.getStringListSetting(SettingNodes.SENSOR_PERMISSIONS);
                List<String> commands = plugin.getStringListSetting(
                        event.getPlayer().isSneaking() ? SettingNodes.SENSOR_COMMANDS : SettingNodes.SHIFT_COMMANDS);
                HashMap<String, Boolean> perms = new HashMap<String, Boolean>();

                // Give required permissions
                for (String p : permissions) {
                    boolean has = permission.has(event.getPlayer(), p);
                    perms.put(p, has);
                    if (!has) permission.playerAdd(event.getPlayer(), p);
                }

                // Run the commands
                for (String command : commands) {
                    command = command.replace('&', ChatColor.COLOR_CHAR);
                    command = command.replace("%d", logBlocks.get(event.getClickedBlock().getLocation()));
                    command = command.replace("%p", event.getPlayer().getName());
                    event.getPlayer().performCommand(command);
                }

                // Remove any permissions they didn't have
                for (String p : permissions) {
                    if (!perms.get(p)) {
                        permission.playerRemove(event.getPlayer(), p);
                    }
                }

                // Start the cooldown
                logCd.put(event.getPlayer().getName(), System.currentTimeMillis());
            }
        }
    }

    /**
     * Gets the number of sensor blocks the player has down
     *
     * @param player player to check
     * @return       number of sensor blocks
     */
    private int getSensorCount(Player player) {
        int count = 0;
        for (String name : logOwners.values()) {
            if (name.equals(player.getName())) {
                count++;
            }
        }
        return count;
    }

    /**
     * Cancels a jailing process for the player
     *
     * @param player player doing the jailing
     */
    private void cancelJailing(Player player) {

        // Stop the report
        ReportProgress report = reporters.get(player.getName().toLowerCase());
        reporters.remove(player.getName().toLowerCase());

        // Apply cancel commands
        for (String command : plugin.getStringListSetting(SettingNodes.CANCEL_COMMANDS)) {
            command = filterCommand(command, report);
            plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }

    /**
     * Adds meta to stun grenades so that they can be recognized later
     *
     * @param event event details
     */
    @EventHandler
    public void onThrow(ProjectileLaunchEvent event) {
        if (addMeta) {
            event.getEntity().setMetadata(STUN_META, new FixedMetadataValue(plugin, true));
            addMeta = false;
        }
    }

    /**
     * Records a log block when placed
     *
     * @param event event details
     */
    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (addMeta) {
            addMeta = false;
            Date time = Calendar.getInstance().getTime();
            time.setTime(Calendar.getInstance().getTimeInMillis() - 60000);
            logBlocks.put(event.getBlock().getLocation(), FORMAT.format(time));
            logOwners.put(event.getBlock().getLocation(), event.getPlayer().getName());
            event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "You have created a sensor block");
        }
    }
}
