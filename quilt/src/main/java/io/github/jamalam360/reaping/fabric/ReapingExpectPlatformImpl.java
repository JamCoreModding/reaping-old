package io.github.jamalam360.reaping.fabric;

import io.github.jamalam360.reaping.config.ReapingConfig;
import io.github.jamalam360.reaping.fabriclike.ReapingFabricLike;
import io.github.jamalam360.reaping.fabriclike.mixin.LootContextBuilderAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.loot.context.LootContext;
import org.quiltmc.loader.api.QuiltLoader;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

/**
 * @author Jamalam360
 */
public class ReapingExpectPlatformImpl {
    public static ReapingConfig getConfig() {
        return ReapingFabricLike.getConfig();
    }

    public static boolean isModLoaded(String modId) {
        return QuiltLoader.isModLoaded(modId);
    }

    public static LootContext.Builder getLootContextBuilder(LivingEntity entity, boolean causedByPlayer, DamageSource source) {
        return ((LootContextBuilderAccessor) entity).callGetLootContextBuilder(causedByPlayer, source);
    }

    public static void setScale(LivingEntity entity, float scale) {
        ScaleData data = ScaleTypes.BASE.getScaleData(entity);
        data.setTargetScale(scale);
    }
}
