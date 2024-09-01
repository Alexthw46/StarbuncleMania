package alexthw.starbunclemania.client;

import alexthw.starbunclemania.common.block.fluids.SourceCondenserTile;
import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.hollingsworth.arsnouveau.client.renderer.tile.GenericModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import static alexthw.starbunclemania.client.JarRenderer.renderFluid;

public class SourceCondenserRenderer extends GeoBlockRenderer<SourceCondenserTile> {

    public static final GeoModel<SourceCondenserTile> model = new GenericModel<>("source_condenser");

    public static GenericItemBlockRenderer getISTER() {
        return new GenericItemBlockRenderer(model);
    }

    public SourceCondenserRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        super(model);
    }

    public static final Vector3f LIQUID_DIMENSIONS = new Vector3f(10 / 16f, 10 / 16f, 1 / 16f); //Width, Height, y0

    @Override
    public void actuallyRender(PoseStack poseStack, SourceCondenserTile tile, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
        super.actuallyRender(poseStack, tile, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
        poseStack.pushPose();
        poseStack.translate(-0.5, 0.05, -0.5);
        FluidStack fluidHolder = tile.getFluid();
        if (!fluidHolder.isEmpty()) {
            renderFluid(tile.getFluidPercentage(), IClientFluidTypeExtensions.of(fluidHolder.getFluid()).getTintColor(fluidHolder),
                    fluidHolder.getFluid().getFluidType().getLightLevel(), IClientFluidTypeExtensions.of(fluidHolder.getFluid()).getStillTexture(),
                    poseStack, bufferSource, packedLight, true, LIQUID_DIMENSIONS);
        }
        poseStack.popPose();
    }

}
