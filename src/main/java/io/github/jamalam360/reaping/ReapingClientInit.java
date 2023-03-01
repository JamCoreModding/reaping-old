package io.github.jamalam360.reaping;

import io.github.jamalam360.reaping.content.pillager.ReapingPillagerEntityRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

public class ReapingClientInit implements ClientModInitializer {

    @Override
    public void onInitializeClient(ModContainer mod) {
        EntityRendererRegistry.register(
              ModRegistry.REAPING_PILLAGER,
              ReapingPillagerEntityRenderer::new
        );
    }
}
