package io.github.jamalam360.reaping.mixin.pillager;

import com.google.common.collect.ImmutableMap;
import io.github.jamalam360.reaping.entity.pillager.ReapingPillagerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.sensor.VillagerHostilesSensor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Jamalam
 */

@Mixin(VillagerHostilesSensor.class)
public class VillagerHostilesSensorMixin {
    @Shadow
    @Final
    private static ImmutableMap<EntityType<?>, Float> SQUARED_DISTANCES_FOR_DANGER;

    @Inject(
            method = "isHostile",
            at = @At("HEAD"),
            cancellable = true
    )
    public void reaping$injectCustomPillagerIsHostile(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof ReapingPillagerEntity) {
            cir.setReturnValue(true);
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(
            method = "isCloseEnoughForDanger",
            at = @At("HEAD"),
            cancellable = true
    )
    public void reaping$injectCustomPillagerIsCloseEnoughForDanger(LivingEntity villager, LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        if (target instanceof ReapingPillagerEntity) {
            float f = SQUARED_DISTANCES_FOR_DANGER.get(EntityType.PILLAGER);
            cir.setReturnValue(target.squaredDistanceTo(villager) <= (double) (f * f));
        }
    }
}
