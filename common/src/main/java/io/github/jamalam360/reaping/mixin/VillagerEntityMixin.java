package io.github.jamalam360.reaping.mixin;

import io.github.jamalam360.reaping.ReapingExpectPlatform;
import io.github.jamalam360.reaping.ReapingMod;
import io.github.jamalam360.reaping.logic.CustomReapableEntityDuck;
import io.github.jamalam360.reaping.logic.ReapingHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Jamalam360
 */

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends MerchantEntity implements CustomReapableEntityDuck {
    private boolean reapingmod$remainSmall = false;
    private int reapingmod$remainingSmallTicks = 0;

    public VillagerEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

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

    @Inject(
            method = "interactMob",
            at = @At("HEAD"),
            cancellable = true
    )
    public void reapingmod$reapVillager(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (ReapingHelper.tryReap(this, player.getStackInHand(hand)) == ActionResult.SUCCESS) {
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }

    @Override
    public ActionResult reapingmod$onReaped(ItemStack toolStack) {
        if (!this.reapingmod$remainSmall) {
            this.reapingmod$remainSmall = true;
            this.reapingmod$remainingSmallTicks = this.world.random.nextInt(50 * 20, 120 * 20);

            this.dropItem(ReapingMod.HUMAN_MEAT);
            ReapingExpectPlatform.setScale(this, 0.45f);
            this.playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0f, 1.0f);

            if (toolStack.getHolder() != null) {
                this.damage(DamageSource.player((PlayerEntity) toolStack.getHolder()), 1.0f);
            } else {
                this.damage(DamageSource.GENERIC, 1.0f);
            }

            return ActionResult.SUCCESS;
        } else {
            return ActionResult.PASS;
        }
    }
}
