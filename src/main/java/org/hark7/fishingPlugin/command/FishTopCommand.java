package org.hark7.fishingPlugin.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.hark7.fishingPlugin.FishingPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FishTopCommand implements CommandExecutor {
    private final FishingPlugin plugin;

    public FishTopCommand(FishingPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Map<UUID, Integer> levels = plugin.getFishingLevels();
        List<Map.Entry<UUID, Integer>> sortedLevels = new ArrayList<>(levels.entrySet());
        sortedLevels.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        sender.sendMessage(ChatColor.YELLOW + "===== 釣りランキング =====");
        for (int i = 0; i < Math.min(10, sortedLevels.size()); i++) {
            Map.Entry<UUID, Integer> entry = sortedLevels.get(i);
            String playerName = Bukkit.getOfflinePlayer(entry.getKey()).getName();
            sender.sendMessage(ChatColor.GREEN + String.valueOf(i + 1) + ". " + playerName + " - レベル " + entry.getValue());
        }

        return true;
    }
}