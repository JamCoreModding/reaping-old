package io.github.jamalam360.reaping;

import dev.architectury.injectables.annotations.ExpectPlatform;
import io.github.jamalam360.reaping.config.ReapingConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.context.LootContext;

public class ReapingExpectPlatform {
    @ExpectPlatform
    public static ReapingConfig getConfig() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isModLoaded(String modId) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static  LootContext.Builder getLootContextBuilder(LivingEntity entity, boolean causedByPlayer, DamageSource source) {
        throw new AssertionError();
    }

    public void setScale(PlayerEntity player, double scale) { throw new AssertionError(); }
}
