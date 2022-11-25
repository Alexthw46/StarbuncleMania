package alexthw.starbunclemania.client;

import alexthw.starbunclemania.common.item.cosmetic.StarBalloon;
import com.hollingsworth.arsnouveau.client.renderer.item.FixedGeoItemRenderer;
import com.hollingsworth.arsnouveau.client.renderer.tile.GenericModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.jetbrains.annotations.Nullable;
import software.bernie.ars_nouveau.geckolib3.core.util.Color;
import software.bernie.ars_nouveau.geckolib3.geo.render.built.GeoBone;

public class BalloonRenderer extends FixedGeoItemRenderer<StarBalloon> {

    public BalloonRenderer() {
        super(new GenericModel<StarBalloon>("star_balloon", "entity"));
    }

    @Override
    public Color getRenderColor(Object animatable, float partialTicks, PoseStack stack, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn) {
        if (currentItemStack.hasTag())
            return Color.ofOpaque(currentItemStack.getOrCreateTag().getInt("color"));
        return Color.WHITE;
    }

    @Override
    public void renderRecursively(GeoBone bone, PoseStack stack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (bone.getName().equals("ball")){
            super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        }else {
            super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, Color.WHITE.getRed() / 255F, Color.WHITE.getGreen() / 255F, Color.WHITE.getBlue() / 255F, alpha);
        }
    }
}
