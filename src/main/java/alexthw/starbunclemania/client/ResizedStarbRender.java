package alexthw.starbunclemania.client;

import com.hollingsworth.arsnouveau.client.renderer.entity.StarbuncleRenderer;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jetbrains.annotations.Nullable;
import software.bernie.ars_nouveau.geckolib3.geo.render.built.GeoModel;

public class ResizedStarbRender extends StarbuncleRenderer {
    public ResizedStarbRender(EntityRendererProvider.Context manager) {
        super(manager);
    }

    @Override
    public void render(GeoModel model, Starbuncle animatable, float partialTicks, RenderType type, PoseStack matrixStackIn, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        matrixStackIn.pushPose();
        matrixStackIn.scale(3,3,3);
        super.render(model, animatable, partialTicks, type, matrixStackIn, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        matrixStackIn.popPose();
    }
}
