package io.github.jamalam360.reaping.mixin;

import io.github.jamalam360.reaping.CustomReapableEntityDuck;
import io.github.jamalam360.reaping.ReapingExpectPlatform;
import io.github.jamalam360.reaping.ReapingMod;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Jamalam360
 */

@SuppressWarnings("ConstantConditions")
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements CustomReapableEntityDuck {
    @Shadow
    public abstract boolean damage(DamageSource source, float amount);

    private boolean reapingmod$remainSmall = false;
    private int reapingmod$remainingSmallTicks = 0;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
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
            ReapingExpectPlatform.setScale((PlayerEntity) (Object) this, 1f);
        }
    }

    @Override
    public ActionResult reapingmod$onReaped(ItemStack toolStack) {
        if (!this.reapingmod$remainSmall && ReapingExpectPlatform.getConfig().reapPlayers()) {
            this.reapingmod$remainSmall = true;
            this.reapingmod$remainingSmallTicks = this.world.random.nextInt(50 * 20, 120 * 20);
            ReapingExpectPlatform.setScale((PlayerEntity) (Object) this, 0.45f);

            this.playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0f, 1.0f);

            if (toolStack.getHolder() != null) {
                this.damage(DamageSource.player((PlayerEntity) toolStack.getHolder()), 1.0f);
            } else {
                this.damage(DamageSource.GENERIC, 1.0f);
            }

            this.dropItem(ReapingMod.ITEMS.getRegistrar().get(new Identifier(ReapingMod.MOD_ID, "human_meat")));

            return ActionResult.SUCCESS;
        } else {
            return ActionResult.PASS;
        }
    }
}
