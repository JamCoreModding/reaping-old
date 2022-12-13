package io.github.jamalam360.reaping.registry;

import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.jamalam360.reaping.ReapingMod;
import io.github.jamalam360.reaping.entity.pillager.ReapingPillagerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

/**
 * @author Jamalam
 */
@SuppressWarnings("unchecked")
public class ReapingEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ReapingMod.MOD_ID, (RegistryKey<Registry<EntityType<?>>>) Registries.ENTITY_TYPE.getKey());

    public static final RegistrySupplier<EntityType<ReapingPillagerEntity>> REAPING_PILLAGER;

    static {
        REAPING_PILLAGER = ENTITY_TYPES.register(
                ReapingMod.id("reaping_pillager"),
                () -> EntityType.Builder.
                        create(ReapingPillagerEntity::new, SpawnGroup.MONSTER)
                        .spawnableFarFromPlayer()
                        .setDimensions(0.6F, 1.95F)
                        .maxTrackingRange(8)
                        .build("reaping_pillager")
        );
    }

    public static void registerAttributes() {
        EntityAttributeRegistry.register(
                REAPING_PILLAGER,
                ReapingPillagerEntity::createReapingPillagerAttributes
        );
    }
}
