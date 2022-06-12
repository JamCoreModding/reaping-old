package io.github.jamalam360.reaping.registry;

import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.jamalam360.reaping.ReapingMod;
import io.github.jamalam360.reaping.entity.pillager.ReapingPillagerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.registry.Registry;

/**
 * @author Jamalam
 */
public class ReapingEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ReapingMod.MOD_ID, Registry.ENTITY_TYPE_KEY);

    public static final RegistrySupplier<EntityType<ReapingPillagerEntity>> REAPING_PILLAGER;

    public static void registerAttributes() {
        EntityAttributeRegistry.register(
                REAPING_PILLAGER,
                ReapingPillagerEntity::createReapingPillagerAttributes
        );
    }

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
}
