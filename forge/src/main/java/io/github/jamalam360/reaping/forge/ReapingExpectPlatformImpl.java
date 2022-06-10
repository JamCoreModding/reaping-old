package io.github.jamalam360.reaping.forge;

import net.minecraft.entity.LivingEntity;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

public class ReapingExpectPlatformImpl {
    public static void setScale(LivingEntity entity, float scale) {
        ScaleData data = ScaleTypes.BASE.getScaleData(entity);
        data.setScale(scale);
    }
}
