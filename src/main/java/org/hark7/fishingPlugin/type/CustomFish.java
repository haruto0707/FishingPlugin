package org.hark7.fishingPlugin.type;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * カスタム魚クラス
 * 各魚は名前、素材、説明、レアリティを持ちます。
 */
public class CustomFish implements MaterialFish {
    public final Component name;
    private final Rarity rarity;
    private final Material material;
    private final String description;

    /**
     * 魚のコンストラクタ
     *
     * @param name 魚の名前
     * @param material 魚の素材
     * @param description 魚の説明
     * @param rarity 魚のレアリティ
     */
    public CustomFish(String name, Material material, String description, Rarity rarity) {
        this.name = Component.text(name);
        this.material = material;
        this.description = description;
        this.rarity = rarity;
    }

    public ItemStack createItemStack() {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(PlainTextComponentSerializer.plainText().serialize(name));
            List<String> lore = new ArrayList<>();
            Optional.ofNullable(description).ifPresent(d -> lore.add(ChatColor.GRAY + d));
            lore.add(rarity.chatColor() + "レア度: " + rarity.name());
            meta.setLore(lore);
            //meta.setCustomModelData(1001);
            itemStack.setItemMeta(meta);
        }
        return itemStack;
    }

    @Override
    public Component name() {
        return name;
    }

    @Override
    public FishType fishType() {
        return FishType.Fish;
    }

    @Override
    public Rarity rarity() {
        return rarity;
    }

    @Override
    public Material material() {
        return material;
    }
}
