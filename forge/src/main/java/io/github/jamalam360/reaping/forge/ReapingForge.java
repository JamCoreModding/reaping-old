package io.github.jamalam360.reaping.forge;

import com.mojang.serialization.Codec;
import dev.architectury.platform.forge.EventBuses;
import io.github.jamalam360.reaping.ReapingMod;
import io.github.jamalam360.reaping.config.ReapingConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.world.StructureModifier;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(ReapingMod.MOD_ID)
public class ReapingForge {
    public ReapingForge() {
        EventBuses.registerModEventBus(ReapingMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        ReapingMod.init();

        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((client, screen) -> AutoConfig.getConfigScreen(ReapingConfig.class, screen).get())
        );

        DeferredRegister<Codec<? extends StructureModifier>> serializers = DeferredRegister.create(ForgeRegistries.Keys.STRUCTURE_MODIFIER_SERIALIZERS, ReapingMod.MOD_ID);
        serializers.register(FMLJavaModLoadingContext.get().getModEventBus());
        serializers.register("spawn_modifier", ReapingStructureModifier::makeCodec);
    }
}
