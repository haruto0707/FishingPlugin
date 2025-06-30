package org.hark7.fishingPlugin.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.hark7.fishingPlugin.FishingPlugin;
import org.hark7.fishingPlugin.type.Fishable.*;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class FishStatsCommand implements CommandExecutor {
    private final FishingPlugin plugin;

    public FishStatsCommand(FishingPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        UUID playerUUID = player.getUniqueId();
        var playerData = plugin.playerDataMap().get(playerUUID);
        int level = playerData.level();
        int exp = playerData.exp();
        int requiredExp = plugin.getRequiredExp(level);

        player.sendMessage(ChatColor.YELLOW + "===== 釣り統計 =====");
        player.sendMessage(ChatColor.GREEN + "レベル: " + level);
        player.sendMessage(ChatColor.GREEN + "経験値: " + exp + "/" + requiredExp);
        player.sendMessage(ChatColor.GREEN + "SCRAP: " + playerData.count(Rarity.SCRAP));
        player.sendMessage(ChatColor.GREEN + "COMMON: " + playerData.count(Rarity.COMMON));
        player.sendMessage(ChatColor.GREEN + "UNCOMMON: " + playerData.count(Rarity.UNCOMMON));
        player.sendMessage(ChatColor.GREEN + "RARE: " + playerData.count(Rarity.RARE));
        player.sendMessage(ChatColor.GREEN + "EPIC: " + playerData.count(Rarity.EPIC));
        player.sendMessage(ChatColor.GREEN + "LEGENDARY: " + playerData.count(Rarity.LEGENDARY));
        // Add more statistics here if needed

        return true;
    }
}