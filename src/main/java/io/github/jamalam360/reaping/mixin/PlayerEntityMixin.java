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

import com.mojang.authlib.GameProfile;
import io.github.jamalam360.reaping.CustomReapableEntity;
import io.github.jamalam360.reaping.ModRegistry;
import io.github.jamalam360.reaping.ReapingInit;
import io.github.jamalam360.reaping.ReapingLogic;
import io.github.jamalam360.reaping.content.ReaperItem;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements CustomReapableEntity {

    private boolean reaping$remainSmall = false;
    private int reaping$remainingSmallTicks = 0;

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
            data.setScale(1f);
        }
    }

    @Override
    public ActionResult reaping$onReaped(@Nullable PlayerEntity user, ItemStack toolStack) {
        if (!this.reaping$remainSmall && ReapingInit.Config.reapPlayers) {
            this.reaping$remainSmall = true;
            this.reaping$remainingSmallTicks = (this.world.random.nextInt(70) + 50) * 20;

            this.dropItem(ModRegistry.HUMAN_MEAT);
            ScaleData data = ScaleTypes.BASE.getScaleData(this);
            data.setScale(0.45F);
            this.playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0F, 1.0F);

            if (user != null) {
                this.damage(DamageSource.player(user), 1.0F);
            } else {
                this.damage(DamageSource.GENERIC, 1.0F);
            }

            if (EnchantmentHelper.getLevel(ModRegistry.EXECUTIONER, toolStack) > 0) {
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

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyConstant(
          method = "attack",
          constant = @Constant(classValue = SwordItem.class)
    )
    public boolean reaping$fixSweepCheck(Object item, Class<Item> clazz) {
        return item instanceof ReaperItem || item instanceof SwordItem;
    }

    @Redirect(
          method = "attack",
          at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
          ),
          slice = @Slice(
                from = @At(
                      value = "INVOKE",
                      target = "Lnet/minecraft/entity/player/PlayerEntity;squaredDistanceTo(Lnet/minecraft/entity/Entity;)D"
                )
          )
    )
    public boolean reaping$reapEntitiesOnSweep(LivingEntity instance, DamageSource source, float amount) {
        if (ReapingLogic.tryReap((PlayerEntity) (Object) this, instance, this.getStackInHand(Hand.MAIN_HAND)) == ActionResult.SUCCESS) {
            return true;
        } else {
            return instance.damage(source, amount);
        }
    }
}
