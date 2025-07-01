package org.hark7.fishingPlugin.type;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemFish implements MaterialFish {
    private final Component name;
    private final Rarity rarity;
    public final Material material;
    private final int damage;
    private final EnchantmentValue[] enchantments;

    /**
     * 魚のコンストラクタ（名前と素材のみ）
     *
     * @param material アイテム
     * @param rarity レアリティ
     * @param enchantments 付与されるエンチャント
     */
    public ItemFish(Material material, Rarity rarity, EnchantmentValue... enchantments) {
        this.name = Component.translatable(material.getTranslationKey());
        this.material = material;
        this.rarity = rarity;
        this.enchantments = enchantments;
        this.damage = 0; // デフォルトのダメージ値
    }

    /**
     * 魚のコンストラクタ（名前、素材、ダメージ値）
     *
     * @param material アイテム
     * @param rarity レアリティ
     * @param damage ダメージ値
     * @param enchantments 付与されるエンチャント
     */
    public ItemFish(Material material, Rarity rarity, int damage, EnchantmentValue... enchantments) {
        this.name = Component.translatable(material.getTranslationKey());
        this.material = material;
        this.rarity = rarity;
        this.damage = damage;
        this.enchantments = enchantments;
    }

    public ItemStack createItemStack() {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            List<String> lore = new ArrayList<>();
            lore.add(rarity.chatColor() + "レア度: " + rarity.name());
            meta.setLore(lore);
            for (EnchantmentValue enchantment : enchantments) {
                meta.addEnchant(enchantment.enchantment(), enchantment.level(), true);
            }
            //meta.setCustomModelData(1001);
            if (meta instanceof Damageable damageable) {
                damageable.setDamage(damage); // 初期ダメージを0に設定
            }
            itemStack.setItemMeta(meta);
        }
        return itemStack;
    }

    @Override
    public FishType fishType() {
        return FishType.Item;
    }

    @Override
    public Rarity rarity() {
        return rarity;
    }

    @Override
    public Material material() {
        return material;
    }

    @Override
    public Component name() {
        return name;
    }
}
