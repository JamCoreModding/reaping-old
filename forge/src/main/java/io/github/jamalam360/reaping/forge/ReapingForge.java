package io.github.jamalam360.reaping.forge;

import dev.architectury.platform.forge.EventBuses;
import io.github.jamalam360.reaping.ReapingMod;
import io.github.jamalam360.reaping.config.ReapingConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ReapingMod.MOD_ID)
public class ReapingForge {
    public ReapingForge() {
        EventBuses.registerModEventBus(ReapingMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        ReapingMod.init();

        ModLoadingContext.get().registerExtensionPoint(
                ConfigGuiHandler.ConfigGuiFactory.class,
                () -> new ConfigGuiHandler.ConfigGuiFactory((minecraftClient, screen) -> AutoConfig.getConfigScreen(ReapingConfig.class, screen).get())
        );
    }
}
