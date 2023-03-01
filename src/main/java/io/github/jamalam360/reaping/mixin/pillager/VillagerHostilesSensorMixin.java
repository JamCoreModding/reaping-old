/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 Jamalam
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package io.github.jamalam360.reaping.mixin.pillager;

import com.google.common.collect.ImmutableMap;
import io.github.jamalam360.reaping.content.pillager.ReapingPillagerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.sensor.VillagerHostilesSensor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
