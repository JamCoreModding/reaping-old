package io.github.jamalam360.reaping.registry;

import dev.architectury.registry.registries.DeferredRegister;
import io.github.jamalam360.reaping.ReapingMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;

/**
 * @author Jamalam
 */
@SuppressWarnings("unchecked")
public class ReapingStats {
    public static final DeferredRegister<Identifier> STATS = DeferredRegister.create(ReapingMod.MOD_ID, (RegistryKey<Registry<Identifier>>) Registries.CUSTOM_STAT.getKey());

    public static final Identifier USE_REAPER_TOOL_STAT = ReapingMod.id("use_reaper_tool");

    static {
        STATS.register(USE_REAPER_TOOL_STAT, () -> USE_REAPER_TOOL_STAT);
    }
}
