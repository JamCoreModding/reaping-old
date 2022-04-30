package io.github.jamalam360.reaping.fabriclike.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.loot.context.LootContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * @author Jamalam360
 */
@Mixin(LivingEntity.class)
public interface LootContextBuilderAccessor {
    @Invoker
    LootContext.Builder callGetLootContextBuilder(boolean causedByPlayer, DamageSource source);
}
