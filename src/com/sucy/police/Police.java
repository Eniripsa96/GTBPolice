package com.sucy.police;

import com.sucy.police.command.law.LawCommander;
import com.sucy.police.command.police.PoliceCommander;
import com.sucy.police.command.prison.PrisonCommander;
import com.sucy.police.command.record.RecordCommander;
import com.sucy.police.command.sensor.SensorCommander;
import com.sucy.police.command.wanted.WantedCommander;
import com.sucy.police.config.Config;
import com.sucy.police.config.LawNodes;
import com.sucy.police.config.PrisonNodes;
import com.sucy.police.config.SettingNodes;
import com.sucy.police.report.ReportProgress;
import com.sucy.police.util.DataParser;
import com.sucy.police.util.LinkGenerator;
import com.sucy.police.util.TextFormatter;
import com.sucy.police.wanted.WantedManager;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.util.*;

/**
 * Police plugin developed for Gus Callaway
 * Developed by Steven Sucy (Eniripsa96)
 */
public class Police extends JavaPlugin {

    private static final String[] cats = { "a", "b", "c", "d" };
    private static final String[] titles = { "Admin Intervention", "Player Relations", "Protected Regions", "Category Not Yet Decided" };
    private static final String[] urls = { "2766449", "2766450", "2766451", "2766905" };

    private final HashMap<String, PlayerData> players = new HashMap<String, PlayerData>();
    private final HashMap<String, String> watchers = new HashMap<String, String>();
    private final HashMap<String, HashMap<String, String>> laws = new HashMap<String, HashMap<String, String>>();
    private final HashMap<String, String> catTitles = new HashMap<String, String>();
    private final HashMap<String, String> catURLs = new HashMap<String, String>();
    private final HashMap<String, String> lawNames = new HashMap<String, String>();
    private final HashMap<String, Location> offlineTeleport = new HashMap<String, Location>();
    private final HashMap<String, List<Integer>> removedLaws = new HashMap<String, List<Integer>>();
    private final List<String> pvpBanned = new ArrayList<String>();
    private final List<String> latestLaws = new ArrayList<String>();
    private final HashMap<String, Long> prisoners = new HashMap<String, Long>();
    private PoliceListener listener;
    private WantedManager wantedManager;
    private Config prisonConfig;
    private Config lawConfig;
    private BukkitTask regenTask;
    private PrisonTask prisonTask;
    private Location prisonSpawn;
    private Location prisonLoc;
    private Location releaseLoc;

    /**
     * Loads data and sets up required functions
     */
    @Override
    public void onEnable() {

        // Load report data
        loadReportData();

        // Save settings config
        saveDefaultConfig();

        // Set up the recipes
        registerRecipe(Material.STICK);
        registerRecipe(Material.SNOW_BALL);
        registerRecipe(Material.STRING);

        // Set up the regen task
        setupRegen();

        // Set up prison data
        prisonConfig = new Config(this, "prison");
        prisonConfig.saveDefaultConfig();

        // Load prisoners
        if (prisonConfig.getConfig().contains(PrisonNodes.PRISONERS)) {
            if (prisonConfig.getConfig().isList(PrisonNodes.PRISONERS)) {
                for (String prisoner : prisonConfig.getConfig().getStringList(PrisonNodes.PRISONERS)) {
                    prisoners.put(prisoner, Long.MAX_VALUE);
                }
            }
            else {
                ConfigurationSection prison = prisonConfig.getConfig().getConfigurationSection(PrisonNodes.PRISONERS);
                for (String prisoner : prison.getKeys(false)) {
                    prisoners.put(prisoner, prison.getLong(prisoner));
                }
            }
        }

        // Get prison spawn
        if (prisonConfig.getConfig().contains(PrisonNodes.SPAWN_LOC)) {
            prisonSpawn = DataParser.parseLocation(prisonConfig.getConfig().getString(PrisonNodes.SPAWN_LOC));
        }

        // Get prison loc
        if (prisonConfig.getConfig().contains(PrisonNodes.START_LOC)) {
            prisonLoc = DataParser.parseLocation(prisonConfig.getConfig().getString(PrisonNodes.START_LOC));
        }

        // Get Release loc
        if (prisonConfig.getConfig().contains(PrisonNodes.RELEASE_LOC)) {
            releaseLoc = DataParser.parseLocation(prisonConfig.getConfig().getString(PrisonNodes.RELEASE_LOC));
        }

        // Get the banned players
        if (prisonConfig.getConfig().contains(PrisonNodes.PVP_BANNED)) {
            pvpBanned.addAll(prisonConfig.getConfig().getStringList(PrisonNodes.PVP_BANNED));
        }

        // Set up the law data
        lawConfig = new Config(this, "laws");

        // Load latest
        if (lawConfig.getConfig().contains(LawNodes.LATEST)) {
            latestLaws.addAll(lawConfig.getConfig().getStringList(LawNodes.LATEST));
        }

        // Load laws
        if (lawConfig.getConfig().contains(LawNodes.LAWS)) {
            ConfigurationSection lawSection = lawConfig.getConfig().getConfigurationSection(LawNodes.LAWS);
            for (String cat : lawSection.getKeys(false)) {
                HashMap<String, String> lawSet = new HashMap<String, String>();
                ConfigurationSection catSection = lawSection.getConfigurationSection(cat);
                for (String law : catSection.getKeys(false)) {
                    lawSet.put(law, catSection.getString(law));
                }
                laws.put(cat, lawSet);
            }
        }
        if (lawConfig.getConfig().contains(LawNodes.NAMES)) {
            ConfigurationSection lawSection = lawConfig.getConfig().getConfigurationSection(LawNodes.NAMES);
            for (String law : lawSection.getKeys(false)) {
                lawNames.put(law, lawSection.getString(law));
            }
        }

        // Load law categories
        if (lawConfig.getConfig().contains(LawNodes.TITLES)) {
            for (String cat : lawConfig.getConfig().getConfigurationSection(LawNodes.TITLES).getKeys(false)) {
                catTitles.put(cat, lawConfig.getConfig().getString(LawNodes.TITLES + "." + cat));
            }
        }
        if (lawConfig.getConfig().contains(LawNodes.URLS)) {
            for (String cat : lawConfig.getConfig().getConfigurationSection(LawNodes.URLS).getKeys(false)) {
                catURLs.put(cat, lawConfig.getConfig().getString(LawNodes.URLS + "." + cat));
            }
        }

        // Load law removal count
        if (lawConfig.getConfig().contains(LawNodes.REMOVED)) {
            for (String cat : lawConfig.getConfig().getConfigurationSection(LawNodes.REMOVED).getKeys(false)) {
                removedLaws.put(cat, lawConfig.getConfig().getIntegerList(LawNodes.REMOVED + "." + cat));
            }
        }

        // Default law data if none was loaded
        if (laws.size() == 0) {
            for (int i = 0; i < cats.length; i++) {
                laws.put(cats[i], new HashMap<String, String>());
                catTitles.put(cats[i], titles[i]);
                catURLs.put(cats[i], getStringSetting(SettingNodes.CAT_URL) + urls[i]);
            }
        }

        // Set up the wanted manager
        wantedManager = new WantedManager(this);

        // Setup listeners
        listener = new PoliceListener(this);

        // Setup commands
        new RecordCommander(this);
        new WantedCommander(this);
        new PrisonCommander(this);
        prisonTask = new PrisonTask(this);
        new LawCommander(this);
        new PoliceCommander(this);
        new SensorCommander(this);
    }

    /**
     * Cleans up the plugin before closing
     */
    @Override
    public void onDisable() {

        // Cancel regen task
        regenTask.cancel();

        // Remove listeners
        listener.save();
        listener.clearBlocks();
        HandlerList.unregisterAll(this);

        // Save prison data
        prisonConfig.reloadConfig();
        prisonConfig.getConfig().set(PrisonNodes.PRISONERS, prisoners);
        prisonConfig.getConfig().set(PrisonNodes.SPAWN_LOC, DataParser.serializeLocation(prisonSpawn));
        prisonConfig.getConfig().set(PrisonNodes.START_LOC, DataParser.serializeLocation(prisonLoc));
        prisonConfig.getConfig().set(PrisonNodes.RELEASE_LOC, DataParser.serializeLocation(releaseLoc));
        prisonConfig.getConfig().set(PrisonNodes.PVP_BANNED, pvpBanned);
        prisonConfig.saveConfig();

        // Save law data
        lawConfig.getConfig().set(LawNodes.LATEST, latestLaws);
        lawConfig.getConfig().set(LawNodes.LAWS, null);
        lawConfig.getConfig().set(LawNodes.REMOVED, removedLaws);
        lawConfig.getConfig().set(LawNodes.URLS, catURLs);
        lawConfig.getConfig().set(LawNodes.TITLES, catTitles);
        lawConfig.getConfig().set(LawNodes.NAMES, lawNames);
        lawConfig.getConfig().createSection(LawNodes.LAWS);
        ConfigurationSection base = lawConfig.getConfig().getConfigurationSection(LawNodes.LAWS);
        for (String cat : laws.keySet()) {
            base.set(cat, laws.get(cat));
        }
        lawConfig.saveConfig();

        // Save Wanted Data
        wantedManager.save();

        // Clear data
        players.clear();
    }

    /**
     * @return spawn point for prison
     */
    public Location getPrisonSpawn() {
        return prisonSpawn;
    }

    /**
     * @return location to send prisoners to initially
     */
    public Location getPrisonLoc() {
        return prisonLoc;
    }

    /**
     * @return location to send prisoners upon release
     */
    public Location getReleaseLoc() {
        return releaseLoc;
    }

    /**
     * Sets the spawn point for prisoners
     *
     * @param loc prisoner spawn
     */
    public void setPrisonSpawn(Location loc) {
        prisonSpawn = loc;
    }

    /**
     * Sets the initial spawn for prisoners
     *
     * @param loc initial prison location
     */
    public void setPrisonLoc(Location loc) {
        prisonLoc = loc;
    }

    /**
     * Sets the release location of prison
     *
     * @param loc release location
     */
    public void setReleaseLoc(Location loc) {
        releaseLoc = loc;
    }

    /**
     * Checks if a player is in prison
     *
     * @param playerName name of player to check
     * @return           true if in prison, false otherwise
     */
    public PrisonState isInPrison(String playerName) {
        if (prisoners.containsKey(playerName.toLowerCase())) {
            if (System.currentTimeMillis() < prisoners.get(playerName.toLowerCase())) {
                return PrisonState.IN_PRISON;
            }
            else return PrisonState.EXPIRED;
        }
        return PrisonState.NOT_IN_PRISON;
    }

    /**
     * Retrieves the offline teleport location for a player
     *
     * @param player player to check
     * @return       teleport location or null if not found
     */
    public Location getOfflineTeleport(Player player) {
        return offlineTeleport.remove(player.getName().toLowerCase());
    }

    /**
     * Sets an offline teleport location for a player
     *
     * @param player player to teleport
     * @param loc    destination
     */
    public void setOfflineTeleport(String player, Location loc) {
        offlineTeleport.put(player.toLowerCase(), loc);
    }

    /**
     * Sends a player to prison
     *
     * @param player player to send to prison
     */
    public void addPrisoner(OfflinePlayer player, long duration) {
        String playerName = player.getName();
        if (isInPrison(playerName) == PrisonState.NOT_IN_PRISON) {
            if (duration != Long.MAX_VALUE) duration = System.currentTimeMillis() + duration;
            prisoners.put(playerName.toLowerCase(), duration);
            if (player.isOnline()) {
                getServer().getPlayer(player.getName()).teleport(prisonLoc);
            }
            else setOfflineTeleport(player.getName(), prisonLoc);
        }
    }

    /**
     * Frees a player from prison
     *
     * @param playerName name of player to free
     */
    public void freePrisoner(String playerName) {
        if (isInPrison(playerName) != PrisonState.NOT_IN_PRISON) {
            prisoners.remove(playerName.toLowerCase());
            Player player = getServer().getPlayer(playerName);
            if (player != null) {
                player.teleport(getReleaseLoc());
                player.sendMessage(ChatColor.DARK_GREEN + "You have been released from prison");

                Config inv = new Config(this, "inventory\\" + player.getName().toLowerCase());
                int index = 0;
                for (ItemStack item : player.getInventory().getContents()) {
                    if (item == null) continue;
                    DataParser.serializeItem(item, inv.getConfig().createSection("Item" + index++));
                }
                player.getInventory().clear();
            }
            else setOfflineTeleport(playerName, getReleaseLoc());
        }
    }

    /**
     * Checks if a command is white listed for prisoners
     *
     * @param command command to check
     * @return        true if white listed, false otherwise
     */
    public boolean isWhiteListed(String command) {
        return prisonConfig.getConfig().getStringList(SettingNodes.PRISON_WHITELIST).contains(command);
    }

    /**
     * Loads report data
     */
    private void loadReportData() {

        // Load the data
        try {
            File dir = new File(getDataFolder() + "/" + PlayerData.REPORT_FOLDER);
            File[] files = dir.listFiles();

            // Get all open reports
            for (File file : files) {

                // Cannot be folder
                if (file.isDirectory())
                    continue;

                // Add the player data
                String name = file.getName();
                name = name.substring(0, name.length() - 4);
                players.put(name.toLowerCase(), new PlayerData(this, name, getIntSetting(SettingNodes.HEALTH)));
            }
        }

        // Failed to load data
        catch (Exception ex) {
            getLogger().severe("Failed to load report data");
        }
    }

    /**
     * Adds the recipe for the baton to the server
     */
    private void registerRecipe(Material type) {

        // Create the baton
        ItemStack baton = new ItemStack(type);
        baton.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, 1);

        // Create the recipe
        ShapelessRecipe recipe = new ShapelessRecipe(baton);
        recipe.addIngredient(type);
        recipe.addIngredient(Material.GOLD_NUGGET);

        // Register the recipe
        getServer().addRecipe(recipe);
    }

    /**
     * Sets up the regen for player's beat down health
     */
    private void setupRegen() {
        long delay = getIntSetting(SettingNodes.REGEN_DELAY) * 20;
        final int maxHealth = getIntSetting(SettingNodes.HEALTH);
        regenTask = getServer().getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {

                // Regen health
                for (PlayerData player : players.values()) {
                    int health = Math.min(player.getBeatDownHealth() + 1, maxHealth);
                    player.setBeatDownHealth(health);
                }

                // Update watching scoreboards
                for (Map.Entry<String, String> entry : watchers.entrySet()) {
                    PlayerData data = getPlayerData(entry.getValue());
                    Player player = getServer().getPlayer(entry.getKey());

                    // When no longer available, clear from the list
                    if (player == null || data == null) {
                        watchers.remove(entry.getKey());
                    }

                    // If the health is full again, simply don't show it anymore
                    else if (data.getBeatDownHealth() == getIntSetting(SettingNodes.HEALTH)) {
                        clearPlayer(entry.getKey());
                    }

                    // Otherwise, update the current display
                    else {
                        player.getScoreboard().getObjective("BeatDown").getScore(data.getPlayer()).setScore(data.getBeatDownHealth());
                    }
                }
            }
        }, delay, delay);
    }

    /**
     * Retrieves the value for a setting from the config
     *
     * @param node config node
     * @return     setting value
     */
    public int getIntSetting(String node) {
        return getConfig().getInt(node);
    }

    /**
     * Retrieves the value for a setting from the config
     *
     * @param node config node
     * @return     setting value
     */
    public String getStringSetting(String node) {
        return getConfig().getString(node);
    }

    /**
     * Retrieves the value for a setting from the config
     *
     * @param node config node
     * @return     setting value
     */
    public List<String> getStringListSetting(String node) {
        return getConfig().getStringList(node);
    }

    /**
     * Retrieves the data for a player
     *
     * @param playerName player to look for
     * @return           data for the player or null if not found
     */
    public PlayerData getPlayerData(String playerName) {

        // Try to retrieve an online player if no data is found
        if (players.containsKey(playerName.toLowerCase()) || getServer().getPlayer(playerName) == null) {
            return players.get(playerName.toLowerCase());
        }

        // Otherwise just return the loaded data
        else return initializePlayer(getServer().getPlayer(playerName));
    }

    /**
     * @return data for all players
     */
    public ArrayList<PlayerData> getPlayerData() {
        return new ArrayList<PlayerData>(players.values());
    }

    /**
     * @return wanted manager
     */
    public WantedManager getWantedManager() {
        return wantedManager;
    }

    /**
     * Initializes the data for a player
     *
     * @param player player to initialize
     * @return       the data for the loaded player
     */
    public PlayerData initializePlayer(Player player) {
        PlayerData data = new PlayerData(this, player.getName(), getIntSetting(SettingNodes.HEALTH));
        players.put(player.getName().toLowerCase(), data);
        return data;
    }

    /**
     * Displays the target's beat down health bar to the watcher
     *
     * @param target  player being watched
     * @param watcher player watching the other
     */
    public void addWatcher(Player target, Player watcher) {

        // Clear the player's scoreboard if already watching another player
        if (watchers.containsKey(watcher.getName().toLowerCase())) {
            watcher.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }

        // Set up the scoreboard
        watchers.put(watcher.getName().toLowerCase(), target.getName());
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective("BeatDown", "dummy");
        int health = getPlayerData(target.getName()).getBeatDownHealth();
        obj.setDisplayName(getHealthString(health));
        obj.setDisplaySlot(DisplaySlot.BELOW_NAME);
        watcher.setScoreboard(board);
        obj.getScore(target).setScore(health);

        for (Player player : getServer().getOnlinePlayers()) {
            if (!player.getName().equals(target.getName())) {
                obj.getScore(player).setScore(getPlayerData(player.getName()).getBeatDownHealth());
            }
        }
    }

    /**
     * Constructs a health bar string
     *
     * @param health current beat down health
     * @return       health bar string
     */
    private String getHealthString(int health) {
        String result = ChatColor.WHITE + "[" + ChatColor.GREEN;
        for (int i = 0; i < health; i++) {
            result += "|";
        }
        result += ChatColor.RED;
        for (int i = health; i < getIntSetting(SettingNodes.HEALTH); i++) {
            result += "|";
        }
        result += ChatColor.WHITE + "]";
        return result;
    }

    /**
     * Clears a player's watching data before exiting the game
     *
     * @param player player to clear
     */
    public void clearPlayer(String player) {
        if (watchers.containsKey(player.toLowerCase())) {
            Player p = getServer().getPlayer(player);
            p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            watchers.remove(player.toLowerCase());
        }
    }

    /**
     * Retrieves the list of laws in a category
     *
     * @param category category name
     * @return         laws in the category
     */
    public Set<String> getLaws(String category) {
        return laws.containsKey(category.toLowerCase()) ? laws.get(category.toLowerCase()).keySet() : null;
    }

    /**
     * Retrieves the URL for the thread for a law
     *
     * @param law      law name
     * @return         law thread URL
     */
    public String getURL(String law) {
        HashMap<String, String> map = laws.get(law.toLowerCase().substring(0, 1));
        return map == null ? null : map.get(law.toUpperCase());
    }

    /**
     * Adds a new law to the database
     *
     * @param category law category
     * @param url      law URL
     */
    public void addLaw(String category, String url) {
        category = category.toLowerCase();
        if (!laws.containsKey(category)) {
            laws.put(category, new HashMap<String, String>());
        }
        String law = category.toUpperCase();
        if (removedLaws.get(category) != null && removedLaws.get(category).size() > 0) {
            Collections.sort(removedLaws.get(category));
            law += removedLaws.get(category).get(0);
            removedLaws.get(category).remove(0);
        }
        else law += (laws.get(category).size() + 1);
        laws.get(category).put(law, url);
        addName(law, url);
        setShortUrl(laws.get(category), law, url);
        if (latestLaws.size() == 5) {
            latestLaws.remove(0);
        }
        latestLaws.add(law);
    }

    /**
     * @return latest laws
     */
    public List<String> getLatest() {
        return latestLaws;
    }

    /**
     * @return list of categories registered
     */
    public Set<String> getCategories() {
        return laws.keySet();
    }

    /**
     * Adds a new category to the database
     *
     * @param category title of the category
     */
    public void addCategory(String category, String url) {
        String c = (char)(65 + laws.size()) + "";
        laws.put(c.toLowerCase(), new HashMap<String, String>());
        catTitles.put(c.toLowerCase(), category);
        catURLs.put(c.toLowerCase(), url);
        setShortUrl(catURLs, c.toLowerCase(), url);
    }

    /**
     * Removes a law from the database
     *
     * @param law law to remove
     */
    public void removeLaw(String law) {
        String cat = law.toLowerCase().substring(0, 1);
        laws.get(cat).remove(law.toUpperCase());
        latestLaws.remove(law.toUpperCase());
        lawNames.remove(law.toUpperCase());
        if (!removedLaws.containsKey(cat)) {
            removedLaws.put(cat, new ArrayList<Integer>());
        }
        removedLaws.get(cat).add(Integer.parseInt(law.substring(1)));
    }

    /**
     * Retrieves title for a category
     *
     * @param cat category
     * @return    title
     */
    public String getCatTitle(String cat) {
        return catTitles.get(cat.toLowerCase());
    }

    /**
     * Retrieves URL for a category
     *
     * @param cat category
     * @return    URL
     */
    public String getCatUrl(String cat) {
        return catURLs.get(cat.toLowerCase());
    }

    /**
     * Sets the category title
     *
     * @param cat   category
     * @param title new title
     */
    public void setCatTitle(String cat, String title) {
        if (catTitles.containsKey(cat.toLowerCase())) {
            catTitles.put(cat.toLowerCase(), title);
        }
    }

    /**
     * Sets category URL
     *
     * @param cat category
     * @param url new URL
     * @return    true if valid url, false otherwise
     */
    public boolean setCatUrl(String cat, String url) {
        return catURLs.containsKey(cat.toLowerCase()) && setShortUrl(catURLs, cat.toLowerCase(), url);
    }

    /**
     * Sets the URL for a law
     *
     * @param law law to set for
     * @param url new URL
     * @return    true if valid url, false otherwise
     */
    public boolean setLawUrl(String law, String url) {
        HashMap<String, String> map = laws.get(law.toLowerCase().substring(0, 1));
        if (map != null) addName(law, url);
        return map != null && setShortUrl(map, law.toUpperCase(), url);
    }

    /**
     * Sets a short url to the map
     *
     * @param target target map
     * @param key    key of the map
     * @param url    long url
     * @return       true if successful, false otherwise
     */
    private boolean setShortUrl(HashMap<String, String> target, String key, String url) {
        try {
            getServer().getScheduler().runTaskAsynchronously(this, new LinkGenerator(target, key, url));
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }

    /**
     * Adds a law name to the map
     *
     * @param law law with the name
     * @param url url containing the name
     */
    private void addName(String law, String url) {
        url = url.substring(url.lastIndexOf("/"));
        url = url.substring(url.indexOf("-") + 1);
        url = TextFormatter.format(url);
        lawNames.put(law.toUpperCase(), url);
    }

    /**
     * Retrieves the name of a law
     *
     * @param law law code
     * @return    law name
     */
    public String getLawName(String law) {
        return lawNames.get(law.toUpperCase());
    }

    /**
     * Gets the sensor locations for a player
     *
     * @param player player to get it for
     * @return       sensor location list
     */
    public List<Location> getSensorLocations(Player player) {
        return listener.getSensorLocations(player);
    }

    /**
     * Deletes a sensor block for the player
     *
     * @param player player to delete for
     * @param id     sensor ID
     */
    public void deleteSensor(Player player, int id) {
        listener.deleteSensor(player, id);
    }

    /**
     * Checks if a player is PvP Banned
     *
     * @param player player to check
     * @return       true if banned, false otherwise
     */
    public boolean isPvPBanned(String player) {
        return pvpBanned.contains(player.toLowerCase());
    }

    /**
     * Bans a player from PvP
     *
     * @param player player to ban
     */
    public void pvpBan(String player) {
        pvpBanned.add(player.toLowerCase());
    }

    /**
     * Unbans a player from PvP
     *
     * @param player player to unban
     */
    public void pvpUnban(String player) {
        pvpBanned.remove(player.toLowerCase());
    }
}
