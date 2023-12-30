package alexthw.starbunclemania.client;

import alexthw.starbunclemania.common.block.FluidSourcelinkTile;
import alexthw.starbunclemania.common.block.SourceCondenserTile;
import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.hollingsworth.arsnouveau.client.renderer.tile.GenericModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import static alexthw.starbunclemania.client.JarRenderer.renderFluid;

public class FluidSourceLinkRenderer extends GeoBlockRenderer<FluidSourcelinkTile> {
    public static final GeoModel<FluidSourcelinkTile> model = new GenericModel<>("fluid_sourcelink");

    public FluidSourceLinkRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        super(model);
    }

    public static GenericItemBlockRenderer getISTER() {
        return new GenericItemBlockRenderer(model);
    }

    public static final Vector3f LIQUID_DIMENSIONS = new Vector3f(7.5f / 16f, 4.5f / 16f, 1.1f / 16f); //Width, Height, y0


    @Override
    public void actuallyRender(PoseStack poseStack, FluidSourcelinkTile tile, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.actuallyRender(poseStack, tile, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        poseStack.pushPose();
        poseStack.translate(-0.5, 0, -0.5);
        FluidStack fluidHolder = tile.getFluid();
        if (!fluidHolder.isEmpty()) {
            renderFluid(fluidHolder.getAmount() / (float) FluidSourcelinkTile.capacity, IClientFluidTypeExtensions.of(fluidHolder.getFluid()).getTintColor(fluidHolder),
                    fluidHolder.getFluid().getFluidType().getLightLevel(), IClientFluidTypeExtensions.of(fluidHolder.getFluid()).getStillTexture(),
                    poseStack, bufferSource, packedLight, true, LIQUID_DIMENSIONS);
        }
        poseStack.popPose();
    }
}
