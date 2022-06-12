package io.github.jamalam360.reaping.entity.pillager;

import com.google.common.collect.Maps;
import dev.architectury.networking.NetworkManager;
import io.github.jamalam360.reaping.ReaperItem;
import io.github.jamalam360.reaping.logic.ReapingHelper;
import io.github.jamalam360.reaping.registry.ReapingItems;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.*;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * @author Jamalam
 */
public class ReapingPillagerEntity extends IllagerEntity implements InventoryOwner {
    private final SimpleInventory inventory = new SimpleInventory(5);
    private int cooldown = 0;

    public ReapingPillagerEntity(EntityType<ReapingPillagerEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createReapingPillagerAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.42F)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 18.0F)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0F)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 44.0);
    }

    public void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(2, new PatrolApproachGoal(this, 10.0F));
        this.goalSelector.add(2, new FleeEntityGoal<>(this, IronGolemEntity.class, 4.0F, 1.0, 1.2));
        this.goalSelector.add(3, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.add(8, new WanderAroundGoal(this, 0.6));
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 15.0F, 1.0F));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 15.0F));
        this.targetSelector.add(1, new RevengeGoal(this, RaiderEntity.class).setGroupRevenge());
        this.targetSelector.add(2, new TargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new TargetGoal<>(this, MerchantEntity.class, false));
        this.targetSelector.add(3, new TargetGoal<>(this, IronGolemEntity.class, true));
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.put("Inventory", inventory.toNbtList());
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        inventory.readNbtList(nbt.getList("Inventory", 10));
        this.setCanPickUpLoot(true);
    }

    public float getPathfindingFavor(BlockPos pos, WorldView world) {
        return 0.0F;
    }

    public int getLimitPerChunk() {
        return 1;
    }

    @Nullable
    public EntityData initialize(
            ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt
    ) {
        RandomGenerator randomGenerator = world.getRandom();
        this.initEquipment(randomGenerator, difficulty);
        this.updateEnchantments(randomGenerator, difficulty);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    protected void initEquipment(RandomGenerator randomGenerator, LocalDifficulty difficulty) {
        this.equipStack(
                EquipmentSlot.MAINHAND,
                (randomGenerator.nextBoolean() ?
                        ReapingItems.IRON_REAPER.get() :
                        randomGenerator.nextBoolean() ?
                                ReapingItems.GOLD_REAPER.get() :
                                ReapingItems.DIAMOND_REAPER.get()).getDefaultStack()
        );
    }

    protected void enchantMainHandItem(RandomGenerator randomGenerator, float power) {
        super.enchantMainHandItem(randomGenerator, power);
        if (randomGenerator.nextInt(200) == 0) {
            ItemStack itemStack = this.getMainHandStack();
            if (itemStack.getItem() instanceof ReaperItem) {
                Map<Enchantment, Integer> map = EnchantmentHelper.get(itemStack);
                EnchantmentHelper.set(map, itemStack);
                this.equipStack(EquipmentSlot.MAINHAND, itemStack);
            }
        }

    }

    @Override
    public void tick() {
        super.tick();

        if (this.cooldown > 0) {
            this.cooldown--;
        }
    }

    @Override
    public boolean tryAttack(Entity target) {
        if (!(this.getMainHandStack().getItem() instanceof ReaperItem)
                || !(target instanceof LivingEntity) || cooldown > 0) return super.tryAttack(target);

        float g = (float) this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_KNOCKBACK) + (float) EnchantmentHelper.getKnockback(this);

        int i = EnchantmentHelper.getFireAspect(this);
        if (i > 0) {
            target.setOnFireFor(i * 4);
        }

        ActionResult result = ReapingHelper.tryReap(null, (LivingEntity) target, this.getMainHandStack());
        if (result == ActionResult.SUCCESS) {
            if (g > 0.0F) {
                ((LivingEntity) target)
                        .takeKnockback(
                                g * 0.5F,
                                MathHelper.sin(this.getYaw() * (float) (Math.PI / 180.0)),
                                -MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0))
                        );
                this.setVelocity(this.getVelocity().multiply(0.6, 1.0, 0.6));
            }

            this.applyDamageEffects(this, target);
            this.onAttacking(target);
            this.swingHand(Hand.MAIN_HAND);
            this.cooldown = ((ReaperItem) this.getMainHandStack().getItem()).getCooldownTicks() + 60;
        }

        return result == ActionResult.SUCCESS;
    }

    public boolean isTeammate(Entity other) {
        if (super.isTeammate(other)) {
            return true;
        } else if (other instanceof LivingEntity && ((LivingEntity) other).getGroup() == EntityGroup.ILLAGER) {
            return this.getScoreboardTeam() == null && other.getScoreboardTeam() == null;
        } else {
            return false;
        }
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_PILLAGER_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PILLAGER_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_PILLAGER_HURT;
    }

    public SimpleInventory getInventory() {
        return this.inventory;
    }

    protected void loot(ItemEntity item) {
        ItemStack itemStack = item.getStack();
        if (itemStack.getItem() instanceof BannerItem) {
            super.loot(item);
        } else if (this.isRaidCaptain(itemStack)) {
            this.triggerItemPickedUpByEntityCriteria(item);
            ItemStack itemStack2 = this.inventory.addStack(itemStack);
            if (itemStack2.isEmpty()) {
                item.discard();
            } else {
                itemStack.setCount(itemStack2.getCount());
            }
        }

    }

    private boolean isRaidCaptain(ItemStack stack) {
        return this.hasActiveRaid() && stack.isOf(Items.WHITE_BANNER);
    }

    public StackReference getStackReference(int mappedIndex) {
        int i = mappedIndex - 300; // 300 = INVENTORY_OFFSET
        return i >= 0 && i < this.inventory.size() ? StackReference.of(this.inventory, i) : super.getStackReference(mappedIndex);
    }

    public void addBonusForWave(int wave, boolean unused) {
        Raid raid = this.getRaid();

        if (raid == null) return;

        boolean bl = this.random.nextFloat() <= raid.getEnchantmentChance();
        if (bl) {
            ItemStack itemStack = new ItemStack(Items.CROSSBOW);
            Map<Enchantment, Integer> map = Maps.newHashMap();
            if (wave > raid.getMaxWaves(Difficulty.NORMAL)) {
                map.put(Enchantments.QUICK_CHARGE, 2);
            } else if (wave > raid.getMaxWaves(Difficulty.EASY)) {
                map.put(Enchantments.QUICK_CHARGE, 1);
            }

            map.put(Enchantments.MULTISHOT, 1);
            EnchantmentHelper.set(map, itemStack);
            this.equipStack(EquipmentSlot.MAINHAND, itemStack);
        }
    }

    public SoundEvent getCelebratingSound() {
        return SoundEvents.ENTITY_PILLAGER_CELEBRATE;
    }

    @Override
    public State getState() {
        return this.isCelebrating() ? State.CELEBRATING : State.ATTACKING;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return NetworkManager.createAddEntityPacket(this);
    }
}
