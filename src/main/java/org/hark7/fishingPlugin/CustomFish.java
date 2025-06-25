package org.hark7.fishingPlugin;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * カスタム魚クラス
 * 各魚は名前、素材、説明、レアリティを持ちます。
 */
public class CustomFish {
    public final String name;
    public final Rarity rarity;
    public final Material material;
    private final String description;
    private final EnchantmentValue[] enchantments;
    private int damage;
    /**
     * 魚のコンストラクタ
     *
     * @param name 魚の名前
     * @param material 魚の素材
     * @param description 魚の説明
     * @param rarity 魚のレアリティ
     */
    public CustomFish(String name, Material material, String description, Rarity rarity) {
        this.name = name;
        this.material = material;
        this.description = description;
        this.rarity = rarity;
        this.enchantments = new EnchantmentValue[0];
    }

    public CustomFish(Material material, Rarity rarity, int damage, EnchantmentValue... enchantments) {
        this.name = material.name().replace("_", " ").toLowerCase();
        this.material = material;
        this.description = "";
        this.rarity = rarity;
        this.damage = damage;
        this.enchantments = enchantments;
    }
    /**
     * 魚のコンストラクタ（名前と素材のみ）
     *
     * @param material 魚の素材
     * @param rarity 魚のレアリティ
     */
    public CustomFish(Material material, Rarity rarity, EnchantmentValue... enchantments) {
        this.name = material.name().replace("_", " ").toLowerCase();
        this.material = material;
        this.description = "";
        this.rarity = rarity;
        this.enchantments = enchantments;
    }

    public ItemStack createItemStack() {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(rarity.getColor() + name);
            List<String> lore = new ArrayList<>();
            Optional.ofNullable(description).ifPresent(d -> lore.add(ChatColor.GRAY + d));
            lore.add(rarity.getColor() + "レア度: " + rarity.name());
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

    public record EnchantmentValue(Enchantment enchantment, int level) {
    }

    /**
     * レアリティを表す列挙型
     * 各レアリティには経験値の値と色が関連付けられています。
     * レアリティは、釣り上げた魚の種類や価値を示します。
     * レアリティが高いほど、釣り上げるのが難しく、より多くの経験値を獲得できます。
     */
    public enum Rarity {
        SCRAP(1, ChatColor.GRAY),
        COMMON(10, ChatColor.WHITE),
        UNCOMMON(20, ChatColor.GREEN),
        RARE(40, ChatColor.BLUE),
        EPIC(100, ChatColor.DARK_PURPLE),
        LEGENDARY(250, ChatColor.GOLD);

        private final int expValue;
        private final ChatColor color;

        Rarity(int expValue, ChatColor color) {
            this.expValue = expValue;
            this.color = color;
        }

        public int getExpValue() {
            return expValue;
        }

        public ChatColor getColor() {
            return color;
        }
    }
}
