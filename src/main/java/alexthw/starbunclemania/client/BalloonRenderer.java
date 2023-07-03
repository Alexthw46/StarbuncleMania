package alexthw.starbunclemania.client;

import alexthw.starbunclemania.common.item.cosmetic.StarBalloon;
import com.hollingsworth.arsnouveau.client.renderer.item.FixedGeoItemRenderer;
import com.hollingsworth.arsnouveau.client.renderer.tile.GenericModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.object.Color;


public class BalloonRenderer extends FixedGeoItemRenderer<StarBalloon> {

    public BalloonRenderer() {
        super(new GenericModel<StarBalloon>("star_balloon", "entity"));
    }

    @Override
    public Color getRenderColor(StarBalloon animatable, float partialTick, int packedLight) {
        if (currentItemStack.hasTag())
            return Color.ofOpaque(currentItemStack.getOrCreateTag().getInt("color"));
        return Color.WHITE;
    }

    @Override
    public void renderRecursively(PoseStack poseStack, StarBalloon animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (bone.getName().equals("ball")){
            super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        }else {
            super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay,Color.WHITE.getRed() / 255F, Color.WHITE.getGreen() / 255F, Color.WHITE.getBlue() / 255F, alpha);
        }
    }
}
