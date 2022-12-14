package alexthw.starbunclemania.client;

import alexthw.starbunclemania.common.block.FluidSourcelinkTile;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.hollingsworth.arsnouveau.client.renderer.tile.SourcelinkModel;
import com.hollingsworth.arsnouveau.common.block.tile.SourcelinkTile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.ars_nouveau.geckolib3.geo.render.built.GeoModel;
import software.bernie.ars_nouveau.geckolib3.renderers.geo.GeoBlockRenderer;

import static alexthw.starbunclemania.client.JarRenderer.renderFluid;

public class FluidSourceLinkRenderer extends GeoBlockRenderer<FluidSourcelinkTile> {
    public static final SourcelinkModel model = new SourcelinkModel<>("fluid"){
        public final ResourceLocation animationLoc = new ResourceLocation(ArsNouveau.MODID, "animations/fluid_sourcelink_animations.json");
        @Override
        public ResourceLocation getAnimationResource(SourcelinkTile sourcelinkTile) {
            return animationLoc;
        }

    };

    public FluidSourceLinkRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        super(rendererDispatcherIn, model);
    }
    public static GenericItemBlockRenderer getISTER() {
        return new GenericItemBlockRenderer(model);
    }

    public static final Vector3f LIQUID_DIMENSIONS = new Vector3f(7.5f / 16f, 4.5f / 16f, 1.1f / 16f); //Width, Height, y0

    @Override
    public void render(GeoModel model, FluidSourcelinkTile tile, float partialTicks, RenderType type, PoseStack matrixStackIn, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        super.render(model, tile, partialTicks, type, matrixStackIn, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        matrixStackIn.pushPose();
        matrixStackIn.translate(-0.5,0,-0.5);
        FluidStack fluidHolder = tile.getFluid();
        if (!fluidHolder.isEmpty()) {
            renderFluid(fluidHolder.getAmount() / (float) FluidSourcelinkTile.capacity, IClientFluidTypeExtensions.of(fluidHolder.getFluid()).getTintColor(fluidHolder),
                    fluidHolder.getFluid().getFluidType().getLightLevel(), IClientFluidTypeExtensions.of(fluidHolder.getFluid()).getStillTexture(),
                    matrixStackIn, renderTypeBuffer, packedLightIn, true, LIQUID_DIMENSIONS);
        }
        matrixStackIn.popPose();
    }
}
