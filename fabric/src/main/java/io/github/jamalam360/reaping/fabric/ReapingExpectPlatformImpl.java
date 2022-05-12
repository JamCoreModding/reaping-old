package io.github.jamalam360.reaping.fabric;

import io.github.jamalam360.reaping.fabriclike.mixin.LootContextBuilderAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.loot.context.LootContext;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

public class ReapingExpectPlatformImpl {
    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    public static LootContext.Builder getLootContextBuilder(LivingEntity entity, boolean causedByPlayer, DamageSource source) {
        return ((LootContextBuilderAccessor) entity).callGetLootContextBuilder(causedByPlayer, source);
    }

    public static void setScale(LivingEntity entity, float scale) {
        ScaleData data = ScaleTypes.BASE.getScaleData(entity);
        data.setScale(scale);
    }
}
