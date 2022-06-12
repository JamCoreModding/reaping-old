package io.github.jamalam360.reaping.mixin;

import com.mojang.authlib.GameProfile;
import io.github.jamalam360.reaping.ReapingExpectPlatform;
import io.github.jamalam360.reaping.ReapingMod;
import io.github.jamalam360.reaping.config.ReapingConfig;
import io.github.jamalam360.reaping.logic.CustomReapableEntityDuck;
import io.github.jamalam360.reaping.registry.ReapingEnchantments;
import io.github.jamalam360.reaping.registry.ReapingItems;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Jamalam360
 */

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements CustomReapableEntityDuck {
    private boolean reapingmod$remainSmall = false;
    private int reapingmod$remainingSmallTicks = 0;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    public abstract boolean damage(DamageSource source, float amount);

    @Shadow
    public abstract GameProfile getGameProfile();

    @Inject(
            method = "writeCustomDataToNbt",
            at = @At("TAIL")
    )
    public void reapingmod$serializeRemainSmall(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean("reapingmod$remainSmall", this.reapingmod$remainSmall);
        nbt.putInt("reapingmod$remainSmallTicks", this.reapingmod$remainingSmallTicks);
    }

    @Inject(
            method = "readCustomDataFromNbt",
            at = @At("TAIL")
    )
    public void reapingmod$deserializeRemainSmall(NbtCompound nbt, CallbackInfo ci) {
        this.reapingmod$remainSmall = nbt.getBoolean("reapingmod$remainSmall");
        this.reapingmod$remainingSmallTicks = nbt.getInt("reapingmod$remainSmallTicks");
    }

    @Inject(
            method = "tick",
            at = @At("TAIL")
    )
    public void reapingmod$tick(CallbackInfo ci) {
        if (this.reapingmod$remainingSmallTicks != 0 && this.reapingmod$remainSmall) {
            this.reapingmod$remainingSmallTicks--;
        } else if (this.reapingmod$remainingSmallTicks <= 0 && this.reapingmod$remainSmall) {
            this.reapingmod$remainSmall = false;
            ReapingExpectPlatform.setScale(this, 1f);
        }
    }

    @Override
    public ActionResult reapingmod$onReaped(@Nullable PlayerEntity user, ItemStack toolStack) {
        if (!this.reapingmod$remainSmall && AutoConfig.getConfigHolder(ReapingConfig.class).getConfig().reapPlayers) {
            this.reapingmod$remainSmall = true;
            this.reapingmod$remainingSmallTicks = ReapingMod.RANDOM.nextInt(50 * 20, 120 * 20);

            this.dropItem(ReapingItems.HUMAN_MEAT.get());
            ReapingExpectPlatform.setScale(this, 0.45f);
            this.playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0f, 1.0f);

            if (user != null) {
                this.damage(DamageSource.player(user), 1.0f);
            } else {
                this.damage(DamageSource.GENERIC, 1.0f);
            }

            if (EnchantmentHelper.getLevel(ReapingEnchantments.EXECUTIONER.get(), toolStack) > 0) {
                if (this.world.random.nextDouble() < 0.15D) {
                    ItemStack stack = new ItemStack(Items.PLAYER_HEAD);

                    GameProfile gameProfile = this.getGameProfile();
                    stack.getOrCreateNbt().put("SkullOwner", NbtHelper.writeGameProfile(new NbtCompound(), gameProfile));
                    this.dropStack(stack);
                }
            }

            return ActionResult.SUCCESS;
        } else {
            return ActionResult.PASS;
        }
    }
}
