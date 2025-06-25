package org.hark7.fishingPlugin;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class FishItems {
    public final List<CustomFish> fishList = new ArrayList<>();       // 釣り可能な魚のリスト

    public void initializeFishList() {
        // Scrap fish
        addFish(Material.LILY_PAD, CustomFish.Rarity.SCRAP);
        addFish(Material.BOWL, CustomFish.Rarity.SCRAP);
        addFish(Material.STICK, CustomFish.Rarity.SCRAP);
        addFish(Material.STRING, CustomFish.Rarity.SCRAP);
        addFish(Material.ROTTEN_FLESH, CustomFish.Rarity.SCRAP);
        addFish(Material.BONE, CustomFish.Rarity.SCRAP);
        addFish(Material.LEATHER, CustomFish.Rarity.SCRAP);
        addFish(Material.INK_SAC, CustomFish.Rarity.SCRAP);
        // Common fish (40 types)
        addFish("コイ", "淡水の定番魚", Material.COD, CustomFish.Rarity.COMMON);
        addFish("フナ", "日本の川でよく見られる魚", Material.COD, CustomFish.Rarity.COMMON);
        addFish("ワカサギ", "小さくて美味しい魚", Material.COD, CustomFish.Rarity.COMMON);
        addFish("アユ", "香り豊かな清流の女王", Material.SALMON, CustomFish.Rarity.COMMON);
        addFish("ヤマメ", "渓流釣りの人気者", Material.SALMON, CustomFish.Rarity.COMMON);
        addFish("ニジマス", "虹色に輝く美しい魚", Material.SALMON, CustomFish.Rarity.COMMON);
        addFish("イワナ", "渓流の王者", Material.SALMON, CustomFish.Rarity.COMMON);
        addFish("ウグイ", "清流に住む魚", Material.COD, CustomFish.Rarity.COMMON);
        addFish("オイカワ", "小川によく見られる魚", Material.COD, CustomFish.Rarity.COMMON);
        addFish("カワムツ", "川の中流によく生息する魚", Material.COD, CustomFish.Rarity.COMMON);
        addFish("タナゴ", "二枚貝に卵を産む珍しい魚", Material.COD, CustomFish.Rarity.COMMON);
        addFish("ドジョウ", "田んぼにも生息する魚", Material.COD, CustomFish.Rarity.COMMON);
        addFish("ナマズ", "ひげが特徴的な大型淡水魚", Material.COD, CustomFish.Rarity.COMMON);
        addFish("ハヤ", "清流を好む小型の魚", Material.COD, CustomFish.Rarity.COMMON);
        addFish("フナ", "コイの仲間で pond fish とも呼ばれる", Material.COD, CustomFish.Rarity.COMMON);
        addFish("メダカ", "日本の原風景を思わせる小魚", Material.COD, CustomFish.Rarity.COMMON);
        addFish("モツゴ", "小川や池に生息する小魚", Material.COD, CustomFish.Rarity.COMMON);
        addFish("ライギョ", "外来種で強い生命力を持つ魚", Material.COD, CustomFish.Rarity.COMMON);
        addFish("ワカサギ", "湖に生息する小魚で佃煮にも", Material.COD, CustomFish.Rarity.COMMON);
        addFish("アジ", "釣り人に人気の海水魚", Material.COD, CustomFish.Rarity.COMMON);

        // Uncommon fish (30 types)
        addFish("サケ", "遡上する魚の代表", Material.SALMON, CustomFish.Rarity.UNCOMMON);
        addFish("マス", "冷たい川に住む魚", Material.SALMON, CustomFish.Rarity.UNCOMMON);
        addFish("ブラックバス", "外来種だが人気の高い魚", Material.COD, CustomFish.Rarity.UNCOMMON);
        addFish("ナマズ", "大きくなる底生魚", Material.COD, CustomFish.Rarity.UNCOMMON);
        addFish("ウナギ", "夏の風物詩", Material.COD, CustomFish.Rarity.UNCOMMON);
        addFish("ハゼ", "河口域でよく見られる", Material.COD, CustomFish.Rarity.UNCOMMON);
        addFish("キンギョ", "観賞用として人気の魚", Material.COD, CustomFish.Rarity.UNCOMMON);
        addFish("グッピー", "カラフルな熱帯魚", Material.TROPICAL_FISH, CustomFish.Rarity.UNCOMMON);
        addFish("ティラピア", "養殖が盛んな食用魚", Material.COD, CustomFish.Rarity.UNCOMMON);
        addFish("コチ", "毒のある棘を持つ魚", Material.COD, CustomFish.Rarity.UNCOMMON);
        addFish("カレイ", "片側に目がある平たい魚", Material.COD, CustomFish.Rarity.UNCOMMON);
        addFish("ヒラメ", "高級食材として知られる魚", Material.COD, CustomFish.Rarity.UNCOMMON);
        addFish("スズキ", "沿岸でよく釣れる魚", Material.COD, CustomFish.Rarity.UNCOMMON);
        addFish("タイ", "祝い事に使われる縁起の良い魚", Material.COD, CustomFish.Rarity.UNCOMMON);
        addFish("ブリ", "出世魚として知られる", Material.COD, CustomFish.Rarity.UNCOMMON);

        // Rare fish (20 types)
        addFish("マグロ", "寿司の王様", Material.SALMON, CustomFish.Rarity.RARE);
        addFish("カツオ", "初がつおが有名", Material.SALMON, CustomFish.Rarity.RARE);
        addFish("サンマ", "秋の味覚", Material.COD, CustomFish.Rarity.RARE);
        addFish("イカ", "触手を持つ魚ではない生き物", Material.COD, CustomFish.Rarity.RARE);
        addFish("タコ", "知能の高い八本足", Material.COD, CustomFish.Rarity.RARE);
        addFish("エビ", "様々な料理に使われる", Material.COD, CustomFish.Rarity.RARE);
        addFish("カニ", "甲殻類の代表", Material.COD, CustomFish.Rarity.RARE);
        addFish("ホタテ", "貝類の中でも人気", Material.COD, CustomFish.Rarity.RARE);
        addFish("アワビ", "高級食材として知られる", Material.COD, CustomFish.Rarity.RARE);
        addFish("ウニ", "海のフォアグラと呼ばれる", Material.COD, CustomFish.Rarity.RARE);
        addFish("フグ", "毒を持つ高級魚", Material.PUFFERFISH, CustomFish.Rarity.RARE);
        addFish("アンコウ", "深海に住む奇妙な魚", Material.COD, CustomFish.Rarity.RARE);
        addFish("ノコギリザメ", "特徴的な吻を持つサメ", Material.COD, CustomFish.Rarity.RARE);
        addFish("イセエビ", "高級食材として知られる甲殻類", Material.COD, CustomFish.Rarity.RARE);
        addFish("タツノオトシゴ", "ユニークな形状の魚", Material.TROPICAL_FISH, CustomFish.Rarity.RARE);

        // Epic fish (8 types)
        addFish("マンボウ", "巨大な体の奇妙な魚", Material.COD, CustomFish.Rarity.EPIC);
        addFish("チョウザメ", "キャビアの源となる魚", Material.COD, CustomFish.Rarity.EPIC);
        addFish("ガー", "古代から姿を変えない魚", Material.COD, CustomFish.Rarity.EPIC);
        addFish("ピラルク", "アマゾンの巨大魚", Material.COD, CustomFish.Rarity.EPIC);
        addFish("オオメジロザメ", "大型の猛烈な捕食者", Material.COD, CustomFish.Rarity.EPIC);
        addFish("シーラカンス", "生きた化石", Material.COD, CustomFish.Rarity.EPIC);
        addFish("リュウグウノツカイ", "深海の不思議な魚", Material.COD, CustomFish.Rarity.EPIC);
        addFish("ラブカ", "原始的な姿の深海ザメ", Material.COD, CustomFish.Rarity.EPIC);

        // Legendary fish (2 types)
        addFish("幻の金色コイ", "伝説の魚", Material.COD, CustomFish.Rarity.LEGENDARY);
        addFish("深海の巨大イカ", "伝説の海獣", Material.COD, CustomFish.Rarity.LEGENDARY);
        addFish(Material.NAME_TAG, CustomFish.Rarity.LEGENDARY);
        addFish(Material.ENCHANTED_BOOK, CustomFish.Rarity.LEGENDARY);
        addFish(Material.BOW, CustomFish.Rarity.LEGENDARY);
        addFish(Material.ENCHANTED_BOOK, CustomFish.Rarity.LEGENDARY);
        addFish(Material.NAUTILUS_SHELL, CustomFish.Rarity.LEGENDARY);
        addFish(Material.SADDLE, CustomFish.Rarity.LEGENDARY);
    }

    private void addFish(String name, String description, Material material, CustomFish.Rarity rarity) {
        fishList.add(new CustomFish(name, material, description, rarity));
    }

    private void addFish(Material material, CustomFish.Rarity rarity) {
        fishList.add(new CustomFish(material, rarity));
    }
}
