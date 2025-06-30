package org.hark7.fishingPlugin.command;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.hark7.fishingPlugin.FishingPlugin;
import org.jetbrains.annotations.NotNull;

public class UpgradePoleCommand implements CommandExecutor {
    private final FishingPlugin plugin;

    public UpgradePoleCommand(FishingPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("このコマンドはプレイヤーのみ使用できます。");
            return true;
        }

        ItemStack fishingRod = player.getInventory().getItemInMainHand();

        if (fishingRod.getType() != Material.FISHING_ROD) {
            player.sendMessage(ChatColor.RED + "釣り竿を手に持って使用してください。");
            return true;
        }
        var playerUUID = player.getUniqueId();
        int playerLevel = plugin.getPlayerData(playerUUID).level();
        int upgradeCost = calculateUpgradeCost(fishingRod);

        if (playerLevel < upgradeCost) {
            player.sendMessage(ChatColor.RED + "釣りレベルが足りません。必要レベル: " + upgradeCost);
            return true;
        }

        upgradeFishingRod(fishingRod);
        player.sendMessage(ChatColor.GREEN + "釣り竿をアップグレードしました！");

        return true;
    }

    private int calculateUpgradeCost(ItemStack fishingRod) {
        int luckOfTheSeaLevel = fishingRod.getEnchantmentLevel(Enchantment.LUCK_OF_THE_SEA);
        int lureLevel = fishingRod.getEnchantmentLevel(Enchantment.LURE);
        return (luckOfTheSeaLevel + lureLevel + 1) * 5;
    }

    private void upgradeFishingRod(ItemStack fishingRod) {
        ItemMeta meta = fishingRod.getItemMeta();
        if (meta == null) return;

        if (Math.random() < 0.5) {
            int currentLevel = meta.getEnchantLevel(Enchantment.LUCK_OF_THE_SEA);
            meta.addEnchant(Enchantment.LUCK_OF_THE_SEA, currentLevel + 1, true);
        } else {
            int currentLevel = meta.getEnchantLevel(Enchantment.LURE);
            meta.addEnchant(Enchantment.LURE, currentLevel + 1, true);
        }

        fishingRod.setItemMeta(meta);
    }
}