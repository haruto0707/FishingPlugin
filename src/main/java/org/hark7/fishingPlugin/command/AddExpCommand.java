package org.hark7.fishingPlugin.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.hark7.fishingPlugin.FishingPlugin;
import org.jetbrains.annotations.NotNull;

public class AddExpCommand implements CommandExecutor {
    private final FishingPlugin plugin;

    public AddExpCommand(FishingPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "このコマンドはOP権限が必要です。");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "使用方法: /addexp <プレイヤー名> <経験値>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "指定されたプレイヤーが見つかりません。");
            return true;
        }

        int expToAdd;
        try {
            expToAdd = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "経験値は数値で指定してください。");
            return true;
        }

        if (expToAdd <= 0) {
            sender.sendMessage(ChatColor.RED + "経験値は正の数で指定してください。");
            return true;
        }

        plugin.addExperience(target.getUniqueId(), expToAdd);
        sender.sendMessage(ChatColor.GREEN + target.getName() + "に" + expToAdd + "の経験値を追加しました。");
        target.sendMessage(ChatColor.GREEN + "管理者により" + expToAdd + "の経験値が追加されました。");

        return true;
    }
}