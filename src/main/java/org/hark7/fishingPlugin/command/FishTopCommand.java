package org.hark7.fishingPlugin.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.hark7.fishingPlugin.FishingPlugin;
import org.hark7.fishingPlugin.playerdata.PlayerData;
import org.hark7.fishingPlugin.type.Fishable.Rarity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FishTopCommand implements CommandExecutor, TabExecutor {
    private final FishingPlugin plugin;

    public FishTopCommand(FishingPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Map<UUID, PlayerData> playerDataList = plugin.playerDataMap();
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "引数が不正です。使用方法を確認してください。");
            sendUsage(sender);
            return true;
        }

        List<Map.Entry<UUID, Integer>> sortedList = new ArrayList<>();
        switch (args[0].toLowerCase()) {
            case "level":
                // レベル順の表示
                for (Map.Entry<UUID, PlayerData> entry : playerDataList.entrySet()) {
                    UUID playerId = entry.getKey();
                    PlayerData data = entry.getValue();
                    sortedList.add(Map.entry(playerId, data.level()));
                }
                sortedList.sort((a, b) -> Integer.compare(b.getValue(), a.getValue())); // 降順ソート
                sendLanking(sender, sortedList, "レベル");
                return true;
            case "total":
                // 釣った魚の個数順の表示
                for (Map.Entry<UUID, PlayerData> entry : playerDataList.entrySet()) {
                    UUID playerId = entry.getKey();
                    PlayerData data = entry.getValue();
                    int totalCount = data.countAll(); // 全ての魚の個数を合計
                    sortedList.add(Map.entry(playerId, totalCount));
                }
                sortedList.sort((a, b) -> Integer.compare(b.getValue(), a.getValue())); // 降順ソート
                sendLanking(sender, sortedList, "トータル");
                return true;
            case "scrap":
                for (Map.Entry<UUID, PlayerData> entry : playerDataList.entrySet()) {
                    UUID playerId = entry.getKey();
                    PlayerData data = entry.getValue();
                    sortedList.add(Map.entry(playerId, data.count(Rarity.SCRAP)));
                }
                sortedList.sort((a, b) -> Integer.compare(b.getValue(), a.getValue())); // 降順ソート
                sendLanking(sender, sortedList, "SCRAP");
                return true;
            case "common":
                for (Map.Entry<UUID, PlayerData> entry : playerDataList.entrySet()) {
                    UUID playerId = entry.getKey();
                    PlayerData data = entry.getValue();
                    sortedList.add(Map.entry(playerId, data.count(Rarity.COMMON)));
                }
                sortedList.sort((a, b) -> Integer.compare(b.getValue(), a.getValue())); // 降順ソート
                sendLanking(sender, sortedList, "COMMON");
                return true;
            case "uncommon":
                for (Map.Entry<UUID, PlayerData> entry : playerDataList.entrySet()) {
                    UUID playerId = entry.getKey();
                    PlayerData data = entry.getValue();
                    sortedList.add(Map.entry(playerId, data.count(Rarity.UNCOMMON)));
                }
                sortedList.sort((a, b) -> Integer.compare(b.getValue(), a.getValue())); // 降順ソート
                sendLanking(sender, sortedList, "UNCOMMON");
                return true;
            case "rare":
                for (Map.Entry<UUID, PlayerData> entry : playerDataList.entrySet()) {
                    UUID playerId = entry.getKey();
                    PlayerData data = entry.getValue();
                    sortedList.add(Map.entry(playerId, data.count(Rarity.RARE)));
                }
                sortedList.sort((a, b) -> Integer.compare(b.getValue(), a.getValue())); // 降順ソート
                sendLanking(sender, sortedList, "RARE");
                return true;
            case "epic":
                for (Map.Entry<UUID, PlayerData> entry : playerDataList.entrySet()) {
                    UUID playerId = entry.getKey();
                    PlayerData data = entry.getValue();
                    sortedList.add(Map.entry(playerId, data.count(Rarity.EPIC)));
                }
                sortedList.sort((a, b) -> Integer.compare(b.getValue(), a.getValue())); // 降順ソート
                sendLanking(sender, sortedList, "EPIC");
                return true;
            case "legendary":
                for (Map.Entry<UUID, PlayerData> entry : playerDataList.entrySet()) {
                    UUID playerId = entry.getKey();
                    PlayerData data = entry.getValue();
                    sortedList.add(Map.entry(playerId, data.count(Rarity.LEGENDARY)));
                }
                sortedList.sort((a, b) -> Integer.compare(b.getValue(), a.getValue())); // 降順ソート
                sendLanking(sender, sortedList, "LEGENDARY");
                return true;
            default:
                sender.sendMessage(ChatColor.RED + "無効な引数です。使用方法を確認してください。");
                sendUsage(sender);
                return true;
        }
    }

    private void sendLanking(CommandSender sender, List<Map.Entry<UUID, Integer>> sortedLevels, String title) {
        sender.sendMessage(ChatColor.YELLOW + "===== 釣りランキング - " + title + " =====");
        for (int i = 0; i < Math.min(10, sortedLevels.size()); i++) {
            Map.Entry<UUID, Integer> entry = sortedLevels.get(i);
            String playerName = Bukkit.getOfflinePlayer(entry.getKey()).getName();
            sender.sendMessage(ChatColor.GREEN +
                    String.valueOf(i + 1) + ". " + playerName + " - " + entry.getValue());
        }
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "使用方法: /fishtop <level | total | [Rarity]>");
        sender.sendMessage(ChatColor.YELLOW + "レベル順に表示: /fishtop level");
        sender.sendMessage(ChatColor.YELLOW + "釣った魚の個数順に表示: /fishtop total");
        sender.sendMessage(ChatColor.YELLOW + "レアリティー順に表示: /fishtop <Rarity>");
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("level", "total", "scrap", "common", "uncommon", "rare", "epic", "legendary");
        }
        return null; // 他の引数は補完しない
    }
}