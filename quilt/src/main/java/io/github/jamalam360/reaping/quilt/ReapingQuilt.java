package io.github.jamalam360.reaping.quilt;

import io.github.jamalam360.reaping.fabriclike.ReapingFabricLike;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public class ReapingQuilt implements ModInitializer {
    @Override
    public void onInitialize(ModContainer mod) {
        ReapingFabricLike.init();
    }
}
