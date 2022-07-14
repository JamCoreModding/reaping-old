package io.github.jamalam360.reaping.forge;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.jamalam360.reaping.ReapingMod;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Holder;
import net.minecraft.util.HolderSet;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryCodecs;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraftforge.common.world.ModifiableStructureInfo;
import net.minecraftforge.common.world.StructureModifier;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * @author Jamalam
 */
public record ReapingStructureModifier(HolderSet<StructureFeature> structures, SpawnGroup category,
                                       SpawnSettings.SpawnEntry spawn) implements StructureModifier {
    private static final RegistryObject<Codec<? extends StructureModifier>> SERIALIZER = RegistryObject.create(ReapingMod.id("spawn_modifier"), ForgeRegistries.Keys.STRUCTURE_MODIFIER_SERIALIZERS, ReapingMod.MOD_ID);

    @Override
    public void modify(Holder<StructureFeature> structure, Phase phase, ModifiableStructureInfo.StructureInfo.Builder builder) {
        if (phase == Phase.ADD && this.structures.contains(structure)) {
            builder.getStructureSettings()
                    .getOrAddSpawnOverrides(category)
                    .addSpawn(spawn);
        }
    }

    @Override
    public Codec<? extends StructureModifier> codec() {
        return SERIALIZER.get();
    }

    public static Codec<ReapingStructureModifier> makeCodec() {
        return RecordCodecBuilder.create(builder -> builder.group(
                RegistryCodecs.homogeneousList(Registry.STRUCTURE_WORLDGEN, StructureFeature.DIRECT_CODEC).fieldOf("structures").forGetter(ReapingStructureModifier::structures),
                SpawnGroup.CODEC.fieldOf("category").forGetter(ReapingStructureModifier::category),
                SpawnSettings.SpawnEntry.CODEC.fieldOf("spawn").forGetter(ReapingStructureModifier::spawn)
        ).apply(builder, ReapingStructureModifier::new));
    }
}
