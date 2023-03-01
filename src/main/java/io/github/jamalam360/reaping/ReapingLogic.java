package io.github.jamalam360.reaping;

import io.github.jamalam360.reaping.content.ReaperItem;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
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
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;

public class ReapingLogic {

    public static final ArrayList<Class<?>> VALID_REAPING_TOOLS = new ArrayList<>();

    public static ActionResult tryReap(@Nullable PlayerEntity user, LivingEntity reapedEntity, ItemStack toolStack) {
        int lootingLvl = EnchantmentHelper.getLevel(Enchantments.LOOTING, toolStack);

        ActionResult result;

        if (!VALID_REAPING_TOOLS.contains(toolStack.getItem().getClass()) || reapedEntity.isDead() || reapedEntity.isBlocking()) {
            result = ActionResult.PASS;
        } else if (reapedEntity instanceof CustomReapableEntity reapableEntity) {
            return reapableEntity.reaping$onReaped(user, toolStack);
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

                int bluntnessLevel = EnchantmentHelper.getLevel(ModRegistry.CURSE_OF_BLUNTNESS, toolStack);

                if (bluntnessLevel > 0) {
                    chance += 0.4D;
                }

                int sharpnessLevel = EnchantmentHelper.getLevel(Enchantments.SHARPNESS, toolStack);

                if (sharpnessLevel > 0) {
                    chance -= 0.1D * sharpnessLevel;
                }

                chance = Math.max(0.0D, Math.min(1.0D, chance));

                if (user != null) {
                    user.getItemCooldownManager().set(reaperItem, reaperItem.getCooldownTicks());
                }
            }

            if (reapedEntity.world.random.nextDouble() <= chance) {
                reapedEntity.kill();
            }
        }

        return result;
    }

    private static void dropEntityStacks(@Nullable PlayerEntity user, LivingEntity entity, ItemStack stack) {
        if (!entity.world.isClient) {
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
    }

    public static void addReapingTool(Class<? extends Item> itemClass) {
        if (!VALID_REAPING_TOOLS.contains(itemClass)) {
            VALID_REAPING_TOOLS.add(itemClass);
        }
    }

    public static class ReapingToolDispenserBehavior extends FallibleItemDispenserBehavior {
        private static boolean tryReapEntity(ServerWorld world, BlockPos pos, ItemStack stack) {
            List<LivingEntity> list = world.getEntitiesByClass(LivingEntity.class, new Box(pos), EntityPredicates.EXCEPT_SPECTATOR);

            for (LivingEntity livingEntity : list) {
                return ReapingLogic.tryReap(null, livingEntity, stack) == ActionResult.SUCCESS;
            }

            return false;
        }

        @Override
        public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
            if (ReapingInit.Config.enableDispenserBehavior) {
                BlockPos blockPos = pointer.getPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
                this.setSuccess(tryReapEntity(pointer.getWorld(), blockPos, stack));

                if (this.isSuccess() && stack.damage(1, pointer.getWorld().random, null)) {
                    stack.setCount(0);
                }
            }

            return stack;
        }
    }
}
