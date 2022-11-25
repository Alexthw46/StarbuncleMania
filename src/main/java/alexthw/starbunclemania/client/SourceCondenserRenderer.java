package alexthw.starbunclemania.client;

import alexthw.starbunclemania.common.block.SourceCondenserTile;
import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.hollingsworth.arsnouveau.client.renderer.tile.GenericModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.ars_nouveau.geckolib3.geo.render.built.GeoModel;
import software.bernie.ars_nouveau.geckolib3.model.AnimatedGeoModel;
import software.bernie.ars_nouveau.geckolib3.renderers.geo.GeoBlockRenderer;

import static alexthw.starbunclemania.client.JarRenderer.renderFluid;

public class SourceCondenserRenderer extends GeoBlockRenderer<SourceCondenserTile> {

    public static final AnimatedGeoModel<SourceCondenserTile> model = new GenericModel<>("imbuement_chamber");

    public static GenericItemBlockRenderer getISTER() {
        return new GenericItemBlockRenderer(model);
    }

    public SourceCondenserRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        super(rendererDispatcherIn, model);
    }

    public static final Vector3f LIQUID_DIMENSIONS = new Vector3f(10 / 16f, 10 / 16f, 1 / 16f); //Width, Height, y0

    @Override
    public void render(GeoModel model, SourceCondenserTile tile, float partialTicks, RenderType type, PoseStack matrixStackIn, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        super.render(model, tile, partialTicks, type, matrixStackIn, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        matrixStackIn.pushPose();
        matrixStackIn.translate(-0.5,0,-0.5);
        FluidStack fluidHolder = SourceCondenserTile.tester;
        if (!fluidHolder.isEmpty()) {
            renderFluid(tile.getFluidPercentage(), IClientFluidTypeExtensions.of(fluidHolder.getFluid()).getTintColor(fluidHolder),
                    fluidHolder.getFluid().getFluidType().getLightLevel(), IClientFluidTypeExtensions.of(fluidHolder.getFluid()).getStillTexture(),
                    matrixStackIn, renderTypeBuffer, packedLightIn, true, LIQUID_DIMENSIONS);
        }
        matrixStackIn.popPose();
    }

}
