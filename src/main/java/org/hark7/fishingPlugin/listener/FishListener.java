package org.hark7.fishingPlugin.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.hark7.fishingPlugin.type.*;
import org.hark7.fishingPlugin.FishingPlugin;
import org.hark7.fishingPlugin.type.Fishable.*;

import java.util.*;

/**
 * プレイヤーが魚を釣り上げた際に呼び出され、ランダムな魚を生成してプレイヤーに通知します。
 * また、釣り竿のエンチャントによるボーナス経験値も計算します。
 */
public class FishListener implements Listener {
    private static final float BASE_LEGENDARY_CHANCE = 0.001F; // 基本のレジェンダリー魚の出現確率
    private static final float BASE_EPIC_CHANCE = 0.01F;       // 基本のエピック魚の出現確率
    private static final float BASE_RARE_CHANCE = 0.05F;       // 基本のレア魚の出現確率
    private static final float BASE_UNCOMMON_CHANCE = 0.2F;    // 基本のアンコモン魚の出現確率
    private static final float BASE_COMMON_CHANCE = 0.8F;      // 基本のコモン魚の出現確率
    // エンチャント本に含まれる可能性のあるエンチャントのリスト
    private final Enchantment[] enchantments = {
            Enchantment.BINDING_CURSE, Enchantment.VANISHING_CURSE, Enchantment.FROST_WALKER, Enchantment.MENDING,
            Enchantment.SOUL_SPEED, Enchantment.SWIFT_SNEAK, Enchantment.WIND_BURST, Enchantment.AQUA_AFFINITY,
            Enchantment.BANE_OF_ARTHROPODS, Enchantment.BLAST_PROTECTION, Enchantment.BREACH, Enchantment.CHANNELING,
            Enchantment.DEPTH_STRIDER, Enchantment.DENSITY, Enchantment.EFFICIENCY, Enchantment.FEATHER_FALLING,
            Enchantment.FIRE_ASPECT, Enchantment.FIRE_PROTECTION, Enchantment.FLAME, Enchantment.FORTUNE,
            Enchantment.IMPALING, Enchantment.INFINITY, Enchantment.KNOCKBACK, Enchantment.LOOTING, Enchantment.LOYALTY,
            Enchantment.LUCK_OF_THE_SEA, Enchantment.LURE, Enchantment.MULTISHOT, Enchantment.PIERCING,
            Enchantment.POWER, Enchantment.PROJECTILE_PROTECTION, Enchantment.PROTECTION, Enchantment.PUNCH,
            Enchantment.QUICK_CHARGE, Enchantment.RESPIRATION, Enchantment.RIPTIDE, Enchantment.SHARPNESS,
            Enchantment.SILK_TOUCH, Enchantment.SMITE, Enchantment.SWEEPING_EDGE, Enchantment.THORNS,
            Enchantment.UNBREAKING
    };
    private final FishingPlugin plugin;

    public FishListener(FishingPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * プレイヤーが魚を釣り上げた際に呼び出されるイベントハンドラー。
     * 釣り上げた魚の種類をランダムに決定し、プレイヤーに通知します。
     * また、釣り竿のエンチャントによるボーナス経験値も計算します。
     *
     * @param event PlayerFishEvent イベント
     */
    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            Player player = event.getPlayer();
            UUID playerUUID = player.getUniqueId();
            Fishable caughtFish = getRandomFish(playerUUID, player.getInventory().getItemInMainHand());
            if (caughtFish == null) return;
            event.setExpToDrop(caughtFish.rarity().playerExp());

            if (caughtFish instanceof MaterialFish customFish) {
                if (event.getCaught() instanceof Item caughtItem) {
                    caughtItem.setItemStack(customFish.createItemStack());
                }
            } else if (caughtFish instanceof EntityFish entityFish) {
                var location = event.getHook().getLocation();
                var world = event.getHook().getWorld();
                world.spawnEntity(location, entityFish.entityType());
                if (event.getCaught() != null) event.getCaught().remove();
            } else return;

            int baseExp = caughtFish.rarity().exp();
            int bonusExp = calculateBonusExp(player.getInventory().getItemInMainHand());
            int totalExp = baseExp + bonusExp;
            plugin.addExperience(playerUUID, totalExp);
            plugin.addCount(playerUUID, caughtFish.rarity());

            var adventure = plugin.adventure();
            if (adventure != null) {
                adventure.player(playerUUID);
                adventure.player(playerUUID).sendMessage(Component
                        .text("あなたは")
                        .append(caughtFish.name().color(caughtFish.rarity().textColor()))
                        .append(Component.text("を釣りあげました！")));
                adventure.player(playerUUID).sendMessage(Component
                        .text("経験値を獲得しました: ")
                        .append(Component.text(totalExp))
                        .append(Component.text(" (ボーナス: "))
                        .append(Component.text(bonusExp))
                        .append(Component.text(")"))
                        .color(NamedTextColor.YELLOW));
            }
        }
    }

    /**
     * 釣り竿のエンチャントからボーナス経験値を計算します。
     * 「宝釣り」と「入れ食い」のレベルに応じて経験値が増加します。
     *
     * @param fishingRod 釣り竿のアイテムスタック
     * @return ボーナス経験値
     */
    private int calculateBonusExp(ItemStack fishingRod) {
        if (fishingRod.getType() != Material.FISHING_ROD) return 0;

        int luckOfTheSeaLevel = fishingRod.getEnchantmentLevel(Enchantment.LUCK_OF_THE_SEA);
        int lureLevel = fishingRod.getEnchantmentLevel(Enchantment.LURE);

        return (luckOfTheSeaLevel * 5) + (lureLevel * 3);
    }

    /**
     * プレイヤーのレベルと釣り竿のエンチャントからレアリティボーナスを計算します。
     * プレイヤーのレベルが高いほど、レアリティの確率が上昇します。
     *
     * @param playerLevel プレイヤーのレベル
     * @param fishingRod 釣り竿のアイテムスタック
     * @return レアリティボーナス
     */
    private double calculateRarityBonus(int playerLevel, ItemStack fishingRod) {
        int luckOfTheSeaLevel = fishingRod.getEnchantmentLevel(Enchantment.LUCK_OF_THE_SEA);
        return playerLevel * 0.001 + luckOfTheSeaLevel * 0.005;
    }

    /**
     * ランダムな魚を取得します。
     * プレイヤーのレベルに応じてレアリティの確率が変動します。
     *
     * @param playerUUID プレイヤーのUUID
     * @return ランダムに選ばれた魚
     */
    private Fishable getRandomFish(UUID playerUUID, ItemStack fishingRod) {
        int playerLevel = plugin.playerDataMap().get(playerUUID).level();
        double rarityBonus = calculateRarityBonus(playerLevel, fishingRod);
        double random = Math.random();
        Rarity selectedRarity = getRarity(random, rarityBonus);

        List<Fishable> fishOfSelectedRarity = plugin.fishList().stream()
                .filter(fish -> fish.rarity() == selectedRarity)
                .toList();
        var selectedFish = fishOfSelectedRarity.get(new Random().nextInt(fishOfSelectedRarity.size()));

        if (selectedFish instanceof ItemFish item) {
            return switch (item.material) {
                case BOW -> getBow(playerLevel);
                case ENCHANTED_BOOK -> getEnchantBook(playerLevel);
                case FISHING_ROD -> getFishingLod(playerLevel);
                default -> item;
            };
        }
        return selectedFish;
    }

    /**
     * ランダムな値に基づいてレアリティを決定します。
     * レアリティの確率はプレイヤーのレベルと釣り竿のエンチャントによって変動します。
     *
     * @param random ランダムな値
     * @param rarityBonus レアリティボーナス
     * @return 選択されたレアリティ
     */
    private static Rarity getRarity(double random, double rarityBonus) {
        Rarity selectedRarity;
        if (random < (BASE_LEGENDARY_CHANCE + rarityBonus)) selectedRarity = Rarity.LEGENDARY;
        else if (random < (BASE_EPIC_CHANCE + rarityBonus)) selectedRarity = Rarity.EPIC;
        else if (random < (BASE_RARE_CHANCE + rarityBonus)) selectedRarity = Rarity.RARE;
        else if (random < (BASE_UNCOMMON_CHANCE + rarityBonus)) selectedRarity = Rarity.UNCOMMON;
        else if (random < (BASE_COMMON_CHANCE + rarityBonus)) selectedRarity = Rarity.COMMON;
        else selectedRarity = Rarity.SCRAP;
        return selectedRarity;
    }

    /**
     * プレイヤーのレベルに応じて弓を生成します。
     * 耐久力、衝撃、パワー、フレイム、無限、修繕のエンチャントがランダムに付与されます。
     *
     * @param playerLevel プレイヤーのレベル
     * @return 弓
     */
    private ItemFish getBow(int playerLevel) {
        var rand = new Random();
        var enchantments = new ArrayList<EnchantmentValue>();
        var damage = 350;
        // 耐久力のエンチャントを追加
        if (playerLevel >= 3 && rand.nextInt(4) == 0) {
            var level = 1;
            if (playerLevel > 10) level += rand.nextInt(3);
            else if (playerLevel > 7) level += rand.nextInt(2);
            enchantments.add(new EnchantmentValue(Enchantment.UNBREAKING, level));
        }
        // 衝撃のエンチャントを追加
        if (playerLevel >= 5 && rand.nextInt(4) == 0) {
            var level = 1;
            if (playerLevel > 10) level += rand.nextInt(2);
            enchantments.add(new EnchantmentValue(Enchantment.PUNCH, level));
        }
        // パワーのエンチャントを追加
        if (playerLevel >= 5 && rand.nextInt(4) == 0) {
            var level = 1;
            if (playerLevel > 18) level += rand.nextInt(5);
            else if (playerLevel > 15) level += rand.nextInt(4);
            else if (playerLevel > 12) level += rand.nextInt(3);
            else if (playerLevel > 10) level += rand.nextInt(2);
            enchantments.add(new EnchantmentValue(Enchantment.POWER, level));
        }
        // フレイムのエンチャントを追加
        if (playerLevel >= 15 && rand.nextInt(4) == 0) {
            enchantments.add(new EnchantmentValue(Enchantment.FLAME, 1));
        }
        // 無限のエンチャントを追加
        if (playerLevel >= 18 && rand.nextInt(4) == 0) {
            enchantments.add(new EnchantmentValue(Enchantment.INFINITY, 1));
        }
        // 耐久値を設定
        if (playerLevel >=  3) damage = Math.max(0, damage - rand.nextInt(30));
        if (playerLevel >=  5) damage = Math.max(0, damage - rand.nextInt(30));
        if (playerLevel >=  7) damage = Math.max(0, damage - rand.nextInt(30));
        if (playerLevel >= 10) damage = Math.max(0, damage - rand.nextInt(30));
        if (playerLevel >= 12) damage = Math.max(0, damage - rand.nextInt(30));
        if (playerLevel >= 15) damage = Math.max(0, damage - rand.nextInt(40));
        if (playerLevel >= 18) damage = Math.max(0, damage - rand.nextInt(40));
        if (playerLevel >= 20) damage = Math.max(0, damage - rand.nextInt(50));
        return new ItemFish(Material.BOW, Rarity.LEGENDARY, damage, enchantments.toArray(new EnchantmentValue[0]));
    }

    /**
     * プレイヤーのレベルに応じて釣り竿を生成します。
     * 耐久力、入れ食い、宝釣りのエンチャントがランダムに付与されます。
     * @param playerLevel　プレイヤーのレベル
     * return 釣り竿
     */
    private ItemFish getFishingLod(int playerLevel) {
        var rand = new Random();
        var enchantments = new ArrayList<EnchantmentValue>();
        var damage = 60;
        // 耐久力のエンチャントを追加
        if (playerLevel >= 3 && rand.nextInt(4) == 0) {
            var level = 1;
            if (playerLevel > 10) level += rand.nextInt(3);
            else if (playerLevel > 7) level += rand.nextInt(2);
            enchantments.add(new EnchantmentValue(Enchantment.UNBREAKING, level));
        }
        // 入れ食いのエンチャントを追加
        if (playerLevel >= 7 && rand.nextInt(4) == 0) {
            var level = 1;
            if (playerLevel > 12) level += rand.nextInt(3);
            else if (playerLevel > 10) level += rand.nextInt(2);
            enchantments.add(new EnchantmentValue(Enchantment.LURE, level));
        }
        // 宝釣りのエンチャントを追加
        if (playerLevel >= 10 && rand.nextInt(4) == 0) {
            var level = 1;
            if (playerLevel > 15) level += rand.nextInt(3);
            else if (playerLevel > 12) level += rand.nextInt(2);
            enchantments.add(new EnchantmentValue(Enchantment.LUCK_OF_THE_SEA, level));
        }
        // 耐久値を設定
        if (playerLevel >=  3) damage = Math.max(0, damage - rand.nextInt(10));
        if (playerLevel >=  5) damage = Math.max(0, damage - rand.nextInt(10));
        if (playerLevel >=  7) damage = Math.max(0, damage - rand.nextInt(10));
        if (playerLevel >= 10) damage = Math.max(0, damage - rand.nextInt(10));
        if (playerLevel >= 12) damage = Math.max(0, damage - rand.nextInt(10));
        if (playerLevel >= 15) damage = Math.max(0, damage - rand.nextInt(10));
        if (playerLevel >= 18) damage = Math.max(0, damage - rand.nextInt(10));
        if (playerLevel >= 20) damage = Math.max(0, damage - rand.nextInt(10));
        return new ItemFish(Material.BOW, Rarity.LEGENDARY, damage, enchantments.toArray(new EnchantmentValue[0]));
    }

    /**
     * プレイヤーのレベルに応じてエンチャントされた本を生成します。
     * エンチャントの種類とレベルはランダムに決定されます。
     *
     * @param playerLevel プレイヤーのレベル
     * @return エンチャントされた本
     */
    private ItemFish getEnchantBook(int playerLevel) {
        var rand = new Random();
        var enchantment = enchantments[rand.nextInt(enchantments.length)];
        var level = 1;
        if (2 < enchantment.getMaxLevel()) level += rand.nextInt(Math.min(enchantment.getMaxLevel(), playerLevel / 5) + 1);
        else level += rand.nextInt(Math.min(enchantment.getMaxLevel(), playerLevel / 10) + 1);
        return new ItemFish(Material.ENCHANTED_BOOK, Rarity.LEGENDARY, 0,
                new EnchantmentValue(enchantment, level));
    }
}
