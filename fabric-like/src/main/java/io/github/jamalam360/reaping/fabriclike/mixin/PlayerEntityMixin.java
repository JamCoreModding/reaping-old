package io.github.jamalam360.reaping.fabriclike.mixin;

import io.github.jamalam360.reaping.ReaperItem;
import io.github.jamalam360.reaping.ReapingHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

/**
 * @author Jamalam360
 */
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

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
        if (ReapingHelper.tryReap(instance, this.getStackInHand(Hand.MAIN_HAND)) == ActionResult.SUCCESS) {
            return true;
        } else {
            return instance.damage(source, amount);
        }
    }
}
