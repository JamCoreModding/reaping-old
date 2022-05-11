package io.github.jamalam360.reaping;

import io.github.jamalam360.reaping.config.ReapingConfig;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author Jamalam360
 */
public class ReapingHelper {
    public static final ArrayList<Class<?>> VALID_REAPING_TOOLS = new ArrayList<>();
    private static final Random RANDOM = new Random();

    public static ActionResult tryReap(LivingEntity reapedEntity, ItemStack toolStack) {
        ReapingConfig conf = ReapingExpectPlatform.getConfig();
        int lootingLvl = EnchantmentHelper.getLevel(Enchantments.LOOTING, toolStack);

        ActionResult result;

        if (!VALID_REAPING_TOOLS.contains(toolStack.getItem().getClass())) {
            result = ActionResult.PASS;
        } else if (reapedEntity instanceof AnimalEntity && !reapedEntity.isBaby()) {
            dropEntityStacks(reapedEntity, toolStack);

            ((AnimalEntity) reapedEntity).setBaby(true);

            reapedEntity.playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0f, 1.0f);

            if (conf.damageAnimals()) {
                if (toolStack.getHolder() != null) {
                    reapedEntity.damage(DamageSource.player((PlayerEntity) toolStack.getHolder()), 1.0f);
                } else {
                    reapedEntity.damage(DamageSource.GENERIC, 1.0f);
                }
            }

            if (conf.dropXp()) {
                reapedEntity.world.spawnEntity(EntityType.EXPERIENCE_ORB.create(reapedEntity.world));
            }

            result = ActionResult.SUCCESS;
        } else if (reapedEntity instanceof AnimalEntity && reapedEntity.isBaby() && conf.reapBabies()) {
            reapedEntity.kill();

            reapedEntity.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f, 1.0f);
            reapedEntity.dropStack(new ItemStack(Items.BONE, lootingLvl == 0 ? 1 : reapedEntity.world.random.nextInt(lootingLvl) + 1));

            if (conf.dropXp()) {
                reapedEntity.world.spawnEntity(EntityType.EXPERIENCE_ORB.create(reapedEntity.world));
                reapedEntity.world.spawnEntity(EntityType.EXPERIENCE_ORB.create(reapedEntity.world));
            }

            result = ActionResult.SUCCESS;
        } else {
            result = ActionResult.PASS;
        }

        if (result == ActionResult.SUCCESS) {
            double chance = conf.deathChance() / 100D;

            if (RANDOM.nextDouble() <= chance) {
                reapedEntity.kill();
            }
        }

        return result;
    }

    private static void dropEntityStacks(LivingEntity entity, ItemStack stack) {
        try {
            LootTable lootTable = entity.world.getServer().getLootManager().getTable(entity.getLootTable());
            LootContext.Builder builder = ReapingExpectPlatform.getLootContextBuilder(entity, true, DamageSource.GENERIC);

            int lootingLvl = EnchantmentHelper.getLevel(Enchantments.LOOTING, stack);
            int rollTimes = lootingLvl == 0 ? 1 : RANDOM.nextInt(lootingLvl) + 1;

            for (int i = 0; i < rollTimes; i++) {
                lootTable.generateLoot(builder.build(LootContextTypes.ENTITY), entity::dropStack);
            }
        } catch (NullPointerException ignored) {
        }
    }

    public static void registerValidReapingTool(Class<? extends Item> itemClass) {
        VALID_REAPING_TOOLS.add(itemClass);
    }
}
