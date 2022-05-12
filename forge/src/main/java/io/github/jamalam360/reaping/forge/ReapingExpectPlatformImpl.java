package io.github.jamalam360.reaping.forge;

import io.github.jamalam360.reaping.config.ReapingConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.server.world.ServerWorld;
import net.minecraftforge.fml.ModList;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

public class ReapingExpectPlatformImpl {
    public static ReapingConfig getConfig() {
        return AutoConfig.getConfigHolder(ReapingConfigForge.class).getConfig();
    }

    public static boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    public static  LootContext.Builder getLootContextBuilder(LivingEntity entity, boolean causedByPlayer, DamageSource source) {
        LootContext.Builder builder = new net.minecraft.loot.context.LootContext.Builder((ServerWorld)entity.world)
                .random(entity.world.random)
                .parameter(LootContextParameters.THIS_ENTITY, entity)
                .parameter(LootContextParameters.ORIGIN, entity.getPos())
                .parameter(LootContextParameters.DAMAGE_SOURCE, source)
                .optionalParameter(LootContextParameters.KILLER_ENTITY, source.getAttacker())
                .optionalParameter(LootContextParameters.DIRECT_KILLER_ENTITY, source.getSource());
        if (causedByPlayer && entity.getAttacker() != null && entity.getAttacker() instanceof PlayerEntity attackingPlayer) {
            builder = builder.parameter(LootContextParameters.LAST_DAMAGE_PLAYER,attackingPlayer).luck(attackingPlayer.getLuck());
        }

        return builder;
    }

    public static void setScale(LivingEntity entity, float scale) {
        ScaleData data = ScaleTypes.BASE.getScaleData(entity);
        data.setTargetScale(scale);
    }
}
