package org.hark7.fishingPlugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.Arrays;

/**
 * レシピ登録クラス
 */
public final class Recipes {
    public static void register(FishingPlugin plugin) {
        // 魚のレシピを登録
        var fishList = new Material[] {
                Material.COD, Material.SALMON, Material.TROPICAL_FISH, Material.PUFFERFISH
        };
        Arrays.stream(fishList).forEach(m -> {
            var item = new ItemStack(m);
            ShapelessRecipe recipe = new ShapelessRecipe(
                    new NamespacedKey(plugin, "FishRecipe" + m.name()), item);
            recipe.addIngredient(m);
            Bukkit.addRecipe(recipe);
        });
    }
}
