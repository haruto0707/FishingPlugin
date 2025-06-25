package org.hark7.fishingPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.hark7.fishingPlugin.command.AddExpCommand;
import org.hark7.fishingPlugin.command.FishStatsCommand;
import org.hark7.fishingPlugin.command.FishTopCommand;
import org.hark7.fishingPlugin.command.UpgradePoleCommand;
import org.hark7.fishingPlugin.listener.FishListener;

import java.util.*;

public class FishingPlugin extends JavaPlugin {
    public final Map<UUID, Integer> fishingLevels = new HashMap<>();  // Playerごとの釣りレベル
    public final Map<UUID, Integer> fishingExp = new HashMap<>();     // Playerごとの釣り経験値
    public final FishItems fishItems = new FishItems();

    public static FishingPlugin getInstance() {
        return JavaPlugin.getPlugin(FishingPlugin.class);
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new FishListener(this), this);
        loadConfig();
        fishItems.initializeFishList();
        Recipes.register();
        // コマンドの追加
        Optional.ofNullable(getCommand("fishstats"))
                .ifPresent(c -> c.setExecutor(new FishStatsCommand(this)));
        Optional.ofNullable(getCommand("fishtop"))
                .ifPresent(c -> c.setExecutor(new FishTopCommand(this)));
        Optional.ofNullable(getCommand("upgradepole"))
                .ifPresent(c -> c.setExecutor(new UpgradePoleCommand(this)));
        Optional.ofNullable(getCommand("addexp"))
                .ifPresent(c -> c.setExecutor(new AddExpCommand(this)));
        getLogger().info("FishingPlugin has been enabled!");
    }

    @Override
    public void onDisable() {
        saveCustomConfig();
        getLogger().info("FishingPlugin has been disabled!");
    }

    private void loadConfig() {
        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);
        saveConfig();

        if (config.contains("fishing_levels")) {
            Optional.ofNullable(config.getConfigurationSection("fishing_levels"))
                    .ifPresent(conf -> {
                        for (String uuidString : conf.getKeys(false)) {
                            UUID uuid = UUID.fromString(uuidString);
                            fishingLevels.put(uuid, conf.getInt(uuidString));
                            fishingExp.put(uuid, config.getInt("fishing_exp." + uuidString, 0));
                        }
                    });
        }
    }

    private void saveCustomConfig() {
        FileConfiguration config = getConfig();
        for (Map.Entry<UUID, Integer> entry : fishingLevels.entrySet()) {
            config.set("fishing_levels." + entry.getKey().toString(), entry.getValue());
            config.set("fishing_exp." + entry.getKey().toString(), fishingExp.get(entry.getKey()));
        }
        saveConfig();
    }



    /**
     * プレイヤーの経験値を追加します。
     * レベルアップ時にはメッセージを送信します。
     *
     * @param playerUUID プレイヤーのUUID
     * @param exp 追加する経験値
     */
    public void addExperience(UUID playerUUID, int exp) {
        int currentExp = fishingExp.getOrDefault(playerUUID, 0) + exp;
        int currentLevel = fishingLevels.getOrDefault(playerUUID, 1);

        while (currentExp >= getRequiredExp(currentLevel)) {
            currentExp -= getRequiredExp(currentLevel);
            currentLevel++;
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                player.sendMessage(ChatColor.GOLD + "釣りレベルが上がりました！ 現在のレベル: " + currentLevel);
            }
        }

        fishingExp.put(playerUUID, currentExp);
        fishingLevels.put(playerUUID, currentLevel);
    }

    public int getRequiredExp(int level) {
        return 100 * level * level;
    }

    public Map<UUID, Integer> getFishingLevels() {
        return fishingLevels;
    }

    public Map<UUID, Integer> getFishingExp() {
        return fishingExp;
    }
}