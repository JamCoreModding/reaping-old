package io.github.jamalam360.reaping;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.entity.LivingEntity;

public class ReapingExpectPlatform {
    @ExpectPlatform
    public static void setScale(LivingEntity entity, float scale) {
        throw new AssertionError();
    }
}
