package io.github.jamalam360.reaping;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.loot.context.LootContext;

public class ReapingExpectPlatform {
    @ExpectPlatform
    public static void setScale(LivingEntity entity, float scale) {
        throw new AssertionError();
    }
}
