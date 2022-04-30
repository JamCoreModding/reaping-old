package io.github.jamalam360.reaping.fabric;

import io.github.jamalam360.reaping.fabriclike.ReapingFabricLike;
import net.fabricmc.api.ModInitializer;

public class ReapingFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ReapingFabricLike.init();
    }
}
