package io.github.jamalam360.reaping.content.compat;

import io.github.jamalam360.reaping.ReapingInit;
import io.github.jamalam360.reaping.ReapingLogic;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;

@SuppressWarnings({"unused", "deprecation"})
public class HarvestScythes implements ModInitializer {

    @Override
    public void onInitialize() {
        try {
            @SuppressWarnings("unchecked")
            Class<? extends Item> clazz = (Class<? extends Item>) ReapingInit.class.getClassLoader()
                  .loadClass("wraith.harvest_scythes.item.ScytheItem");
            ReapingLogic.addReapingTool(clazz);
        } catch (Exception e) {
            ReapingInit.LOGGER.warn("Failed to enable Harvest Scythe compatibility");
        }
    }
}
