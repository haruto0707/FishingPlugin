package org.hark7.fishingPlugin.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.hark7.fishingPlugin.FishingPlugin;
import org.hark7.fishingPlugin.playerdata.PlayerData;
import org.hark7.fishingPlugin.playerdata.PlayerDataManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ResetLevelCommand implements CommandExecutor, TabExecutor {
    private final FishingPlugin plugin;
    private final PlayerDataManager manager;
    public ResetLevelCommand(FishingPlugin plugin, PlayerDataManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage("このコマンドはOP権限が必要です。");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage("使用方法: /resetlevel <プレイヤー名>");
            return true;
        }

        if (args[0].equalsIgnoreCase("@a")) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                var uuid = player.getUniqueId();
                var data = new PlayerData(player.getName(), 1, 0);
                manager.setPlayerData(uuid, data);
            });
            sender.sendMessage("全てのプレイヤーのレベルをリセットしました。");
            return true;
        }

        var targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null) {
            sender.sendMessage("指定されたプレイヤーが見つかりません。");
            return true;
        }

        var uuid = targetPlayer.getUniqueId();
        var data = new PlayerData(targetPlayer.getName(), 1, 0);
        manager.setPlayerData(uuid, data);
        sender.sendMessage(targetPlayer.getName() + "のレベルをリセットしました。");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            var list = new ArrayList<String>();
            list.add("@a");
            Bukkit.getOnlinePlayers().forEach(player -> list.add(player.getName()));
            return list;
        }
        return null;
    }
}
