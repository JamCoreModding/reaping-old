package io.github.jamalam360.reaping.content.pillager;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.IllagerEntityRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.util.Identifier;

public class ReapingPillagerEntityRenderer extends IllagerEntityRenderer<ReapingPillagerEntity> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/illager/pillager.png");

    public ReapingPillagerEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new IllagerEntityModel<>(context.getPart(EntityModelLayers.PILLAGER)), 0.5F);
        this.addFeature(new HeldItemFeatureRenderer<>(this, context.getHeldItemRenderer()));
    }

    public Identifier getTexture(ReapingPillagerEntity entity) {
        return TEXTURE;
    }
}
