package io.github.jamalam360.reaping.logic;

import io.github.jamalam360.reaping.ReaperItem;
import io.github.jamalam360.reaping.ReapingMod;
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
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;

import javax.annotation.Nullable;
import java.util.ArrayList;

/**
 * @author Jamalam360
 */
public class ReapingHelper {
    public static final ArrayList<Class<?>> VALID_REAPING_TOOLS = new ArrayList<>();

    public static ActionResult tryReap(@Nullable PlayerEntity user, LivingEntity reapedEntity, ItemStack toolStack) {
        int lootingLvl = EnchantmentHelper.getLevel(Enchantments.LOOTING, toolStack);

        ActionResult result;

        if (!VALID_REAPING_TOOLS.contains(toolStack.getItem().getClass()) || reapedEntity.isDead()) {
            result = ActionResult.PASS;
        } else if (reapedEntity instanceof CustomReapableEntityDuck reapableEntity) {
            return reapableEntity.reapingmod$onReaped(user, toolStack);
        } else if (reapedEntity instanceof AnimalEntity && !reapedEntity.isBaby()) {
            dropEntityStacks(user, reapedEntity, toolStack);

            ((AnimalEntity) reapedEntity).setBaby(true);

            reapedEntity.playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0f, 1.0f);

            if (user != null) {
                reapedEntity.damage(DamageSource.player(user), 1.0f);
            } else {
                reapedEntity.damage(DamageSource.GENERIC, 1.0f);
            }

            reapedEntity.world.spawnEntity(EntityType.EXPERIENCE_ORB.create(reapedEntity.world));

            result = ActionResult.SUCCESS;
        } else if (reapedEntity instanceof AnimalEntity && reapedEntity.isBaby()) {
            reapedEntity.kill();

            reapedEntity.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f, 1.0f);
            reapedEntity.dropStack(new ItemStack(Items.BONE, lootingLvl == 0 ? 1 : reapedEntity.world.random.nextInt(lootingLvl) + 1));

            reapedEntity.world.spawnEntity(EntityType.EXPERIENCE_ORB.create(reapedEntity.world));
            reapedEntity.world.spawnEntity(EntityType.EXPERIENCE_ORB.create(reapedEntity.world));

            result = ActionResult.SUCCESS;
        } else {
            result = ActionResult.PASS;
        }

        if (result == ActionResult.SUCCESS) {
            double chance = 0.45D;

            if (toolStack.getItem() instanceof ReaperItem reaperItem) {
                chance *= reaperItem.sharpnessModifier;

                if (user != null) {
                    user.getItemCooldownManager().set(reaperItem, reaperItem.getCooldownTicks());
                }
            }

            if (ReapingMod.RANDOM.nextDouble() <= chance) {
                reapedEntity.kill();
            }
        }

        return result;
    }

    private static void dropEntityStacks(@Nullable PlayerEntity user, LivingEntity entity, ItemStack stack) {
        LootTable lootTable = entity.world.getServer().getLootManager().getTable(entity.getLootTable());

        DamageSource source = DamageSource.GENERIC;

        if (user != null) {
            source = DamageSource.player(user);
        }

        LootContext.Builder builder = new LootContext.Builder((ServerWorld) entity.world)
                .random(entity.world.random)
                .parameter(LootContextParameters.THIS_ENTITY, entity)
                .parameter(LootContextParameters.ORIGIN, entity.getPos())
                .parameter(LootContextParameters.DAMAGE_SOURCE, source)
                .optionalParameter(LootContextParameters.KILLER_ENTITY, source.getAttacker())
                .optionalParameter(LootContextParameters.DIRECT_KILLER_ENTITY, source.getSource());

        int lootingLvl = EnchantmentHelper.getLevel(Enchantments.LOOTING, stack);
        int rollTimes = lootingLvl == 0 ? 1 : entity.world.random.nextInt(lootingLvl) + 1;

        for (int i = 0; i < rollTimes; i++) {
            lootTable.generateLoot(builder.build(LootContextTypes.ENTITY), entity::dropStack);
        }
    }

    public static void addReapingTool(Class<? extends Item> itemClass) {
        if (!VALID_REAPING_TOOLS.contains(itemClass)) {
            VALID_REAPING_TOOLS.add(itemClass);
        }
    }
}
