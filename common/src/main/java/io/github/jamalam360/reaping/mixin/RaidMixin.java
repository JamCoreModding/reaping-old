package io.github.jamalam360.reaping.mixin;

import io.github.jamalam360.reaping.ReapingMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Jamalam
 */

@Mixin(Raid.class)
public class RaidMixin {
    @Redirect(
            method = "spawnNextWave",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/EntityType;create(Lnet/minecraft/world/World;)Lnet/minecraft/entity/Entity;"
            )
    )
    public Entity reaping$insertReapingPillager(EntityType<? extends RaiderEntity> instance, World world) {
        if (instance == EntityType.PILLAGER && world.random.nextDouble() < 0.35D) {
            return ReapingMod.REAPING_PILLAGER_ENTITY_TYPE.get().create(world);
        } else {
            return instance.create(world);
        }
    }
}
