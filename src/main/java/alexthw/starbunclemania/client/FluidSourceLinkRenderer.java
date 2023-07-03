package alexthw.starbunclemania.client;

import alexthw.starbunclemania.common.block.FluidSourcelinkTile;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import static alexthw.starbunclemania.client.JarRenderer.renderFluid;

public class FluidSourceLinkRenderer extends GeoBlockRenderer<FluidSourcelinkTile> {
    public static final GeoModel<FluidSourcelinkTile> model = new DefaultedBlockGeoModel<>(new ResourceLocation(ArsNouveau.MODID, "fluid_sourcelink"));

    public FluidSourceLinkRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        super(model);
    }

    public static GenericItemBlockRenderer getISTER() {
        return new GenericItemBlockRenderer(model);
    }

    public static final Vector3f LIQUID_DIMENSIONS = new Vector3f(7.5f / 16f, 4.5f / 16f, 1.1f / 16f); //Width, Height, y0


    @Override
    public void preRender(PoseStack poseStack, FluidSourcelinkTile tile, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.preRender(poseStack, tile, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
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
