package org.hark7.fishingPlugin.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.hark7.fishingPlugin.FishingPlugin;

import java.util.UUID;

public class FishStatsCommand implements CommandExecutor {
    private final FishingPlugin plugin;

    public FishStatsCommand(FishingPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        UUID playerUUID = player.getUniqueId();
        int level = plugin.getFishingLevels().getOrDefault(playerUUID, 1);
        int exp = plugin.getFishingExp().getOrDefault(playerUUID, 0);
        int requiredExp = plugin.getRequiredExp(level);

        player.sendMessage(ChatColor.YELLOW + "===== 釣り統計 =====");
        player.sendMessage(ChatColor.GREEN + "レベル: " + level);
        player.sendMessage(ChatColor.GREEN + "経験値: " + exp + "/" + requiredExp);
        // Add more statistics here if needed

        return true;
    }
}