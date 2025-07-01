package org.hark7.fishingPlugin.type;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface MaterialFish extends Fishable {
    Material material();
    ItemStack createItemStack();
}
