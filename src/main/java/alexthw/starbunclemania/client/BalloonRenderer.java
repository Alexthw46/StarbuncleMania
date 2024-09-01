package alexthw.starbunclemania.client;

import alexthw.starbunclemania.common.item.cosmetic.StarBalloon;
import com.hollingsworth.arsnouveau.client.renderer.tile.GenericModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.component.DataComponents;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.Color;


public class BalloonRenderer extends GeoItemRenderer<StarBalloon> {

    public BalloonRenderer() {
        super(new GenericModel<>("star_balloon", "entity"));
    }

    @Override
    public Color getRenderColor(StarBalloon animatable, float partialTick, int packedLight) {
        if (currentItemStack.has(DataComponents.DYED_COLOR))
            return Color.ofOpaque(currentItemStack.get(DataComponents.DYED_COLOR).rgb());
        return Color.WHITE;
    }

    @Override
    public void renderRecursively(PoseStack poseStack, StarBalloon animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        if (bone.getName().equals("ball")) {
            super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
        } else {
            super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, Color.WHITE.getColor());
        }
    }
}
