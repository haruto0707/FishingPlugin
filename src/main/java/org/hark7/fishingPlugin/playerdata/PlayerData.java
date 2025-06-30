package org.hark7.fishingPlugin.playerdata;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.hark7.fishingPlugin.type.Fishable.*;

public class PlayerData {
    private final String playerName; // プレイヤー名
    private int fishingLevel;  // 釣りレベル
    private int fishingExp;    // 釣り経験値
    private final HashMap<Rarity, Integer> caughtFishCount = new HashMap<>();  // 釣った魚の数

    public PlayerData(String playerName, int fishingLevel, int fishingExp, Map<Rarity, Integer> caughtFishCount) {
        this.playerName = playerName;
        this.fishingLevel = fishingLevel;
        this.fishingExp = fishingExp;
        Optional.ofNullable(caughtFishCount)
                .ifPresentOrElse(this.caughtFishCount::putAll, this::getInitialCount);
    }

    public PlayerData(String playerName, int fishingLevel, int fishingExp) {
        this.playerName = playerName;
        this.fishingLevel = fishingLevel;
        this.fishingExp = fishingExp;
        getInitialCount();
    }

    public int level() {
        return fishingLevel;
    }

    public void setLevel(int fishingLevel) {
        this.fishingLevel = fishingLevel;
    }

    public int exp() {
        return fishingExp;
    }

    public void setExp(int fishingExp) {
        this.fishingExp = fishingExp;
    }
    public Map<Rarity, Integer> getCounts() {
        return caughtFishCount;
    }

    public int count(Rarity rarity) {
        return caughtFishCount.getOrDefault(rarity, 0);
    }

    public int countAll() {
        return caughtFishCount.values().stream().mapToInt(Integer::intValue).sum();
    }

    public void addCount(Rarity rarity) {
        caughtFishCount.putIfAbsent(rarity, 0);
    }
    public String playerName() {
        return playerName;
    }

    private void getInitialCount() {
        for (Rarity rarity : Rarity.values()) {
            caughtFishCount.putIfAbsent(rarity, 0);
        }
    }
}