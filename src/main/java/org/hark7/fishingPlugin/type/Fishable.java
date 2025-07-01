package org.hark7.fishingPlugin.type;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;

public interface Fishable {
    Component name();
    FishType fishType();
    Rarity rarity();

    enum FishType {
        Fish,
        Item,
        Entity
    }

    enum Rarity {
        SCRAP(1, 1, ChatColor.GRAY, NamedTextColor.GRAY),
        COMMON(10, 2, ChatColor.WHITE, NamedTextColor.WHITE),
        UNCOMMON(20, 4, ChatColor.GREEN, NamedTextColor.GREEN),
        RARE(40, 6, ChatColor.BLUE, NamedTextColor.BLUE),
        EPIC(100, 10, ChatColor.DARK_PURPLE, NamedTextColor.DARK_PURPLE),
        LEGENDARY(250, 15, ChatColor.GOLD, NamedTextColor.GOLD),;

        private final int expValue;
        private final int pExpValue;
        private final ChatColor chatColor;
        private final TextColor textColor;

        Rarity(int expValue, int pExpValue, ChatColor chatColor, TextColor textColor) {
            this.expValue = expValue;
            this.pExpValue = pExpValue;
            this.chatColor = chatColor;
            this.textColor = textColor;
        }

        public int playerExp() {
            return pExpValue;
        }

        public int exp() {
            return expValue;
        }

        public ChatColor chatColor() {
            return chatColor;
        }
        public TextColor textColor() {
            return textColor;
        }
    }

    record EnchantmentValue(Enchantment enchantment, int level) {
    }
}
