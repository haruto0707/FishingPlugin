package org.hark7.fishingPlugin.type;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class EntityFish implements Fishable {
    private final Component name;
    private final Rarity rarity;
    private final EntityType entity;

    public EntityFish(EntityType entity, Rarity rarity) {
        this.name = Component.translatable(entity.getTranslationKey());
        this.rarity = rarity;
        this.entity = entity;
    }

    public EntityFish(EntityType entity, Rarity rarity, String name) {
        this.name = Component.text(name);
        this.rarity = rarity;
        this.entity = entity;
    }

    @Override
    public Component name() {
        return name;
    }

    @Override
    public FishType fishType() {
        return FishType.Entity;
    }

    @Override
    public Rarity rarity() {
        return rarity;
    }

    public EntityType entityType() {
        return entity;
    }
}
