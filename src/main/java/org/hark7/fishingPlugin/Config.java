package org.hark7.fishingPlugin;

import org.bukkit.configuration.file.FileConfiguration;

public class Config {
    private final FishingPlugin plugin;
    private final FileConfiguration config;
    private static final int PLAYERS_TABLE_VERSION = 1;
    private static final int COUNTS_TABLE_VERSION = 1;
    private static final String PLAYERS_TABLE_VERSION_KEY = "playersTableVersion";
    private static final String COUNTS_TABLE_VERSION_KEY = "countsTableVersion";

    public Config(FishingPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        config.options().copyDefaults(true);
        load();
    }

    private void load() {
        try {
            if (config.isInt(PLAYERS_TABLE_VERSION_KEY)) {
                int version = config.getInt(PLAYERS_TABLE_VERSION_KEY);
                if (version < PLAYERS_TABLE_VERSION) {
                    plugin.getLogger().info("Updating players table to version " + PLAYERS_TABLE_VERSION);
                    plugin.updatePlayerTableVersion();
                    config.set(PLAYERS_TABLE_VERSION_KEY, PLAYERS_TABLE_VERSION);
                }
            } else {
                config.set(PLAYERS_TABLE_VERSION_KEY, PLAYERS_TABLE_VERSION);
                plugin.saveConfig();
            }

            if (config.isInt(COUNTS_TABLE_VERSION_KEY)) {
                int version = config.getInt(COUNTS_TABLE_VERSION_KEY);
                if (version < COUNTS_TABLE_VERSION) {
                    plugin.getLogger().info("Updating counts table to version " + COUNTS_TABLE_VERSION);
                    plugin.updateCountTableVersion();
                    config.set(COUNTS_TABLE_VERSION_KEY, COUNTS_TABLE_VERSION);
                }
            } else {
                config.set(COUNTS_TABLE_VERSION_KEY, COUNTS_TABLE_VERSION);
                plugin.saveConfig();
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to load configuration: " + e.getMessage());
        }
    }

    public void save() {
        config.set(PLAYERS_TABLE_VERSION_KEY, PLAYERS_TABLE_VERSION);
        config.set(COUNTS_TABLE_VERSION_KEY, COUNTS_TABLE_VERSION);
        try {
            plugin.saveConfig();
            plugin.getLogger().info("Configuration saved successfully.");
        }
        catch (Exception e) {
            plugin.getLogger().severe("Failed to save configuration: " + e.getMessage());
        }
    }
}
