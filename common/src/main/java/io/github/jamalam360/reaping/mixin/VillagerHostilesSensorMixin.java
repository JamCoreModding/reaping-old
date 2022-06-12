package io.github.jamalam360.reaping.mixin;

import com.google.common.collect.ImmutableMap;
import io.github.jamalam360.reaping.registry.ReapingEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.sensor.VillagerHostilesSensor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Jamalam
 */

@Mixin(VillagerHostilesSensor.class)
public class VillagerHostilesSensorMixin {
    @Redirect(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/common/collect/ImmutableMap$Builder;build()Lcom/google/common/collect/ImmutableMap;"
            )
    )
    private static ImmutableMap<EntityType<?>, Float> reaping$insertReapingPillager(ImmutableMap.Builder<EntityType<?>, Float> instance) {
        return instance.put(ReapingEntities.REAPING_PILLAGER.get(), 16.0F).build();
    }
}
