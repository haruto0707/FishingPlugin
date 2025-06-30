package org.hark7.fishingPlugin.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.hark7.fishingPlugin.type.CustomFish;
import org.hark7.fishingPlugin.type.Fishable.*;
import org.hark7.fishingPlugin.FishingPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * VillagerAcquireTradeListener
 * <p>
 * このクラスは、村人が新しい取引を獲得した際に呼び出されるイベントリスナーです。
 * 取引が獲得されたときに特定の処理を実行するために使用されます。
 */
public class VillagerAcquireTradeListener implements Listener {
    private static final List<MerchantRecipe> NOVICE_RECIPE_TABLE = new ArrayList<>();
    private static final List<MerchantRecipe> APPRENTICE_RECIPE_TABLE = new ArrayList<>();
    private static final List<MerchantRecipe> JOURNEYMAN_RECIPE_TABLE = new ArrayList<>();
    private static final List<MerchantRecipe> EXPERT_RECIPE_TABLE = new ArrayList<>();
    private static final List<MerchantRecipe> MASTER_RECIPE_TABLE = new ArrayList<>();
    private static final Enchantment[] fishRodEnchantments = new Enchantment[] {
            Enchantment.LUCK_OF_THE_SEA,
            Enchantment.LURE,
            Enchantment.UNBREAKING,
            Enchantment.MENDING,
    };

    /**
     * レシピテーブルを設定します。
     * @param plugin FishingPluginのインスタンス
     */
    public VillagerAcquireTradeListener(FishingPlugin plugin) {
        // 新米漁師のレシピテーブルにレシピを追加
        addRecipeToTable(NOVICE_RECIPE_TABLE, 16, 0.05f, 5,
                new ItemStack(Material.EMERALD), new ItemStack(Material.COD, 15));
        addRecipeToTable(NOVICE_RECIPE_TABLE, 16, 0.05f, 5,
                new ItemStack(Material.EMERALD), new ItemStack(Material.SALMON, 15));
        addRecipeToTable(NOVICE_RECIPE_TABLE, 16, 0.05f, 2,
                new ItemStack(Material.EMERALD), new ItemStack(Material.STRING, 20));
        addRecipeToTable(NOVICE_RECIPE_TABLE, 16, 0.05f, 2,
                new ItemStack(Material.EMERALD), new ItemStack(Material.COAL, 10));
        addRecipeToTable(NOVICE_RECIPE_TABLE, 16, 0.05f, 1,
                new ItemStack(Material.COD_BUCKET), new ItemStack(Material.EMERALD, 3));
        addRecipeToTable(NOVICE_RECIPE_TABLE, 16, 0.05f, 1, new ItemStack(Material.COOKED_COD),
                new ItemStack(Material.COD, 6), new ItemStack(Material.EMERALD));
        // 見習い漁師のレシピテーブルにレシピを追加
        /*
        addRecipeToTable(APPRENTICE_RECIPE_TABLE, 16, 0.05f, 5,
                new ItemStack(Material.EMERALD), new ItemStack(Material.COD, 15));
        addRecipeToTable(APPRENTICE_RECIPE_TABLE, 16, 0.05f, 5,
                new ItemStack(Material.EMERALD), new ItemStack(Material.SALMON, 15));
        addRecipeToTable(APPRENTICE_RECIPE_TABLE, 16, 0.05f, 5,
                new ItemStack(Material.CAMPFIRE), new ItemStack(Material.EMERALD, 2));
        addRecipeToTable(APPRENTICE_RECIPE_TABLE, 16, 0.05f, 5,
                new ItemStack(Material.COOKED_SALMON),
                new ItemStack(Material.SALMON, 20), new ItemStack(Material.EMERALD));
         */
        plugin.fishList().stream()
                .filter(i -> i.rarity() == Rarity.RARE)
                .filter(i -> i instanceof CustomFish)
                .map(i -> (CustomFish) i)
                .forEach(fish -> {
                    var itemStack = fish.createItemStack();
                    itemStack.setAmount(5);
                    addRecipeToTable(APPRENTICE_RECIPE_TABLE, 16, 0.05f, 10,
                            new ItemStack(Material.EMERALD), itemStack);
                    addRecipeToTable(JOURNEYMAN_RECIPE_TABLE, 16, 0.05f, 10,
                            new ItemStack(Material.EMERALD), itemStack);
                });
        // 一人前漁師のレシピテーブルにレシピを追加
        addRecipeToTable(JOURNEYMAN_RECIPE_TABLE, 16, 0.05f, 5,
                new ItemStack(Material.EMERALD), new ItemStack(Material.COD, 15));
        addRecipeToTable(JOURNEYMAN_RECIPE_TABLE, 16, 0.05f, 5,
                new ItemStack(Material.EMERALD), new ItemStack(Material.SALMON, 15));
        Arrays.stream(fishRodEnchantments).forEach(enchant -> {
            var itemStack = new ItemStack(Material.FISHING_ROD);
            var rand = new Random();
            var level = rand.nextInt(enchant.getMaxLevel()) + 1;
            itemStack.addEnchantment(enchant, level);
            addRecipeToTable(JOURNEYMAN_RECIPE_TABLE, 3, 0.02f, 5,
                    itemStack, new ItemStack(Material.EMERALD, 8 + (level - 1) * 7));
        });
        plugin.fishList().stream()
                .filter(i -> i.rarity() == Rarity.EPIC)
                .filter(i -> i instanceof CustomFish)
                .map(i -> (CustomFish) i)
                .forEach(fish -> {
                    var itemStack = fish.createItemStack();
                    addRecipeToTable(JOURNEYMAN_RECIPE_TABLE, 8, 0.05f, 20,
                            new ItemStack(Material.EMERALD), itemStack);
                    addRecipeToTable(EXPERT_RECIPE_TABLE, 8, 0.05f, 20,
                            new ItemStack(Material.EMERALD), itemStack);
                });
        // 熟練漁師のレシピテーブルにレシピを追加
        addRecipeToTable(EXPERT_RECIPE_TABLE, 12, 0.05f, 30,
                new ItemStack(Material.EMERALD), new ItemStack(Material.TROPICAL_FISH, 6));
        // マスター漁師のレシピテーブルにレシピを追加
        addRecipeToTable(MASTER_RECIPE_TABLE, 12, 0.05f, 50,
                new ItemStack(Material.EMERALD), new ItemStack(Material.PUFFERFISH, 4));
        plugin.fishList().stream().filter(i -> i.rarity() == Rarity.LEGENDARY)
                .filter(e -> e instanceof CustomFish)
                .map(i -> (CustomFish) i)
                .forEach(fish -> {
                    var itemStack = fish.createItemStack();
                    addRecipeToTable(MASTER_RECIPE_TABLE, 3, 0.05f, 30,
                            new ItemStack(Material.EMERALD, 5), itemStack);
                });
    }

    // レシピをテーブルに追加するメソッド
    private void addRecipeToTable(List<MerchantRecipe> table, int maxUses, float priceMul, int exp,
                                  ItemStack output, ItemStack... inputs) {
        MerchantRecipe recipe = new MerchantRecipe(output, maxUses);
        for (ItemStack input : inputs) {
            recipe.addIngredient(input);
        }
        recipe.setPriceMultiplier(priceMul);
        recipe.setVillagerExperience(exp);
        table.add(recipe);
    }


    @EventHandler
    public void onVillagerAcquireTrade(VillagerAcquireTradeEvent event) {
        // 村人が漁師の取引を獲得した場合の処理
        if (!(event.getEntity() instanceof Villager villager)) return;
        if (!villager.getProfession().equals(Villager.Profession.FISHERMAN)) return;

        var rand = new Random();
        switch (villager.getVillagerLevel()) {
            case 1:
                setRecipes(NOVICE_RECIPE_TABLE, villager, rand, 2);
                break;
            case 2:
                setRecipes(APPRENTICE_RECIPE_TABLE, villager, rand, 2);
                break;
            case 3:
                setRecipes(JOURNEYMAN_RECIPE_TABLE, villager, rand, 2);
                break;
            case 4:
                setRecipes(EXPERT_RECIPE_TABLE, villager, rand, 1);
                break;
            case 5:
                setRecipes(MASTER_RECIPE_TABLE, villager, rand, 2);
                break;
        }
        event.setCancelled(true);
    }

    private void setRecipes(List<MerchantRecipe> table, Villager villager, Random rand, int count) {
        var newRecipes = new ArrayList<>(villager.getRecipes());
        var candidates = new ArrayList<MerchantRecipe>();
        for (var recipe : table) {
            if (!checkExistRecipe(recipe, newRecipes)) {
                candidates.add(recipe);
            }
        }
        int addCount = Math.min(count, candidates.size());
        for (int i = 0; i < addCount; i++) {
            var recipe = candidates.remove(rand.nextInt(candidates.size()));
            if (checkExistRecipe(recipe, newRecipes)) continue;
            newRecipes.add(recipe);
        }
        Bukkit.getScheduler().runTask(JavaPlugin.getProvidingPlugin(getClass()), () -> villager.setRecipes(newRecipes));
    }

    // 既存のレシピと同じかどうかをチェックするメソッド
    private boolean checkExistRecipe(MerchantRecipe recipe, List<MerchantRecipe> recipes) {
        for (MerchantRecipe r : recipes) {
            if (r.getResult().isSimilar(recipe.getResult()) &&
                    ingredientsSimilar(r.getIngredients(), recipe.getIngredients())) {
                return true;
            }
        }
        return false;
    }

    private boolean ingredientsSimilar(List<ItemStack> a, List<ItemStack> b) {
        if (a.size() != b.size()) return false;
        for (int i = 0; i < a.size(); i++) {
            if (!a.get(i).isSimilar(b.get(i))) return false;
        }
        return true;
    }
}