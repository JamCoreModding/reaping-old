package io.github.jamalam360.reaping.registry;

import dev.architectury.registry.registries.DeferredRegister;
import io.github.jamalam360.reaping.ReapingMod;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * @author Jamalam
 */
public class ReapingStats {
    public static final DeferredRegister<Identifier> STATS = DeferredRegister.create(ReapingMod.MOD_ID, Registry.CUSTOM_STAT_KEY);

    public static final Identifier USE_REAPER_TOOL_STAT = ReapingMod.id("use_reaper_tool");

    static {
        STATS.register(USE_REAPER_TOOL_STAT, () -> USE_REAPER_TOOL_STAT);
    }
}
