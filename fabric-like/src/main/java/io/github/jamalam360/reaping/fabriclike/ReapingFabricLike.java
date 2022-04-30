package io.github.jamalam360.reaping.fabriclike;

import io.github.jamalam360.reaping.ReapingMod;
import io.github.jamalam360.reaping.config.ReapingConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

public class ReapingFabricLike {
    public static void init() {
        ReapingMod.init();
        AutoConfig.register(ReapingConfigFabricLike.class, GsonConfigSerializer::new);
    }

    public static ReapingConfig getConfig() {
        return AutoConfig.getConfigHolder(ReapingConfigFabricLike.class).getConfig();
    }
}
