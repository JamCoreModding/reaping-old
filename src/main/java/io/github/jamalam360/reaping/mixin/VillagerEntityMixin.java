/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 Jamalam
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package io.github.jamalam360.reaping.mixin;

import io.github.jamalam360.reaping.CustomReapableEntity;
import io.github.jamalam360.reaping.ModRegistry;
import io.github.jamalam360.reaping.ReapingLogic;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends MerchantEntity implements CustomReapableEntity {
    private boolean reaping$remainSmall = false;
    private int reaping$remainingSmallTicks = 0;

    public VillagerEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(
            method = "writeCustomDataToNbt",
            at = @At("TAIL")
    )
    public void reaping$serializeRemainSmall(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean("reaping$remainSmall", this.reaping$remainSmall);
        nbt.putInt("reaping$remainSmallTicks", this.reaping$remainingSmallTicks);
    }

    @Inject(
            method = "readCustomDataFromNbt",
            at = @At("TAIL")
    )
    public void reaping$deserializeRemainSmall(NbtCompound nbt, CallbackInfo ci) {
        this.reaping$remainSmall = nbt.getBoolean("reaping$remainSmall");
        this.reaping$remainingSmallTicks = nbt.getInt("reaping$remainSmallTicks");
    }

    @Inject(
            method = "tick",
            at = @At("TAIL")
    )
    public void reaping$tick(CallbackInfo ci) {
        if (this.reaping$remainingSmallTicks != 0 && this.reaping$remainSmall) {
            this.reaping$remainingSmallTicks--;
        } else if (this.reaping$remainingSmallTicks <= 0 && this.reaping$remainSmall) {
            this.reaping$remainSmall = false;
            ScaleData data = ScaleTypes.BASE.getScaleData(this);
            data.setScale(1F);
        }
    }

    @Inject(
            method = "interactMob",
            at = @At("HEAD"),
            cancellable = true
    )
    public void reaping$reapVillager(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (ReapingLogic.tryReap(player, this, player.getStackInHand(hand)) == ActionResult.SUCCESS) {
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }

    @Override
    public ActionResult reaping$onReaped(@Nullable PlayerEntity user, ItemStack toolStack) {
        int lootingLvl = EnchantmentHelper.getLevel(Enchantments.LOOTING, toolStack);

        if (!this.reaping$remainSmall) {
            this.reaping$remainSmall = true;
            this.reaping$remainingSmallTicks = (this.world.random.nextInt(70) + 50) * 20;

            this.dropStack(new ItemStack(ModRegistry.HUMAN_MEAT, lootingLvl == 0 ? 1 : this.world.random.nextInt(lootingLvl) + 1));
            ScaleData data = ScaleTypes.BASE.getScaleData(this);
            data.setScale(0.45F);
            this.playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0F, 1.0F);

            if (user != null) {
                this.damage(DamageSource.player(user), 1.0F);
            } else {
                this.damage(DamageSource.GENERIC, 1.0F);
            }

            return ActionResult.SUCCESS;
        } else if (!this.isDead()) {
            this.kill();
            this.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 1.0F);
            this.dropStack(new ItemStack(Items.BONE, lootingLvl == 0 ? 1 : this.world.random.nextInt(lootingLvl) + 1));

            return ActionResult.SUCCESS;
        } else {
            return ActionResult.PASS;
        }
    }
}
