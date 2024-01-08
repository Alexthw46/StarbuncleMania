package alexthw.starbunclemania.client;

import alexthw.starbunclemania.common.block.fluids.LiquidJarTile;
import alexthw.starbunclemania.common.item.FluidJarItem;
import com.hollingsworth.arsnouveau.client.renderer.item.FixedGeoItemRenderer;
import com.hollingsworth.arsnouveau.client.renderer.tile.GenericModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class JarRenderer implements BlockEntityRenderer<LiquidJarTile> {

    public JarRenderer() {
        super();
    }

    public static class ISTER extends FixedGeoItemRenderer<FluidJarItem> {

        public ISTER() {
            super(new GenericModel<FluidJarItem>("fluid_jar"));
        }

        @Override
        public void renderByItem(ItemStack pStack, ItemDisplayContext pTransformType, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
            super.renderByItem(pStack, pTransformType, pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
            pPoseStack.pushPose();
            pPoseStack.translate(0, 0.5, 0);
            if (pStack.getItem() instanceof FluidJarItem) {
                FluidStack fluid = FluidJarItem.getFluidFromTag(pStack);
                if (!fluid.isEmpty()) {
                    float percentage = (pStack.getOrCreateTag().contains("Starbuncle")) ? 1 : (float) fluid.getAmount() / LiquidJarTile.capacity;
                    renderFluid(percentage, IClientFluidTypeExtensions.of(fluid.getFluid()).getTintColor(fluid),
                            fluid.getFluid().getFluidType().getLightLevel(), IClientFluidTypeExtensions.of(fluid.getFluid()).getStillTexture(),
                            pPoseStack, pBuffer, pPackedLight, true, LIQUID_DIMENSIONS);
                }
            }
            pPoseStack.popPose();
        }
    }

    public static final Vector3f LIQUID_DIMENSIONS = new Vector3f(9 / 16f, 9.5f / 16f, 1 / 16f); //Width, Height, y0

    public static void renderFluid(float percentageFill, int color, int luminosity, ResourceLocation texture, PoseStack matrixStackIn, MultiBufferSource bufferIn, int light, boolean shading, Vector3f fluidVec) {
        matrixStackIn.pushPose();
        float opacity = 1;
        if (luminosity != 0) light = light & 15728640 | luminosity << 4;
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(texture);
        VertexConsumer builder = bufferIn.getBuffer(RenderType.translucentMovingBlock());
        matrixStackIn.translate(0.5, fluidVec.z(), 0.5);
        addCube(builder, matrixStackIn,
                fluidVec.x(),
                percentageFill * fluidVec.y(),
                sprite, light, color, opacity, true, true, shading, true);
        matrixStackIn.popPose();
    }

    @Override
    public void render(LiquidJarTile tile, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        //render fluid
        FluidStack fluidHolder = tile.getFluid();
        if (!fluidHolder.isEmpty()) {
            renderFluid(tile.getFluidPercentage(), IClientFluidTypeExtensions.of(fluidHolder.getFluid()).getTintColor(fluidHolder),
                    fluidHolder.getFluid().getFluidType().getLightLevel(), IClientFluidTypeExtensions.of(fluidHolder.getFluid()).getStillTexture(),
                    pPoseStack, pBufferSource, pPackedLight, true, LIQUID_DIMENSIONS);
        }
    }

    //credits to MehVahdJukaar and Supplementaries team for magic below

    public static void addCube(VertexConsumer builder, PoseStack matrixStackIn, float w, float h, TextureAtlasSprite sprite, int combinedLightIn,
                               int color, float a, boolean up, boolean down, boolean fakeshading, boolean flippedY) {
        addCube(builder, matrixStackIn, 0, 0, w, h, sprite, combinedLightIn, color, a, up, down, fakeshading, flippedY, false);
    }

    public static void addCube(VertexConsumer builder, PoseStack matrixStackIn, float uOff, float vOff, float w, float h, TextureAtlasSprite sprite, int combinedLightIn,
                               int color, float a, boolean up, boolean down, boolean fakeshading, boolean flippedY, boolean wrap) {
        int lu = combinedLightIn & '\uffff';
        int lv = combinedLightIn >> 16 & '\uffff'; // ok
        float atlasScaleU = sprite.getU1() - sprite.getU0();
        float atlasScaleV = sprite.getV1() - sprite.getV0();
        float minU = sprite.getU0() + atlasScaleU * uOff;
        float minV = sprite.getV0() + atlasScaleV * vOff;
        float maxU = minU + atlasScaleU * w;
        float maxV = minV + atlasScaleV * h;
        float maxV2 = minV + atlasScaleV * w;

        float r = (float) ((color >> 16 & 255)) / 255.0F;
        float g = (float) ((color >> 8 & 255)) / 255.0F;
        float b = (float) ((color & 255)) / 255.0F;


        // float a = 1f;// ((color >> 24) & 0xFF) / 255f;
        // shading:

        float r8, g8, b8, r6, g6, b6, r5, g5, b5;

        r8 = r6 = r5 = r;
        g8 = g6 = g5 = g;
        b8 = b6 = b5 = b;
        //TODO: make this affect uv values not rgb
        if (fakeshading) {
            float s1 = 0.8f, s2 = 0.6f, s3 = 0.5f;
            // 80%: s,n
            r8 *= s1;
            g8 *= s1;
            b8 *= s1;
            // 60%: e,w
            r6 *= s2;
            g6 *= s2;
            b6 *= s2;
            // 50%: d
            r5 *= s3;
            g5 *= s3;
            b5 *= s3;
            //100%

        }

        float hw = w / 2f;

        // up
        if (up)
            addQuadTop(builder, matrixStackIn, -hw, h, hw, hw, h, -hw, minU, minV, maxU, maxV2, r, g, b, a, lu, lv, 0, 1, 0);
        // down
        if (down)
            addQuadTop(builder, matrixStackIn, -hw, 0, -hw, hw, 0, hw, minU, minV, maxU, maxV2, r5, g5, b5, a, lu, lv, 0, -1, 0);


        if (flippedY) {
            float temp = minV;
            minV = maxV;
            maxV = temp;
        }
        float inc = 0;
        if (wrap) {
            inc = atlasScaleU * w;
        }

        // north z-
        // x y z u v r g b a lu lv
        addQuadSide(builder, matrixStackIn, hw, 0, -hw, -hw, h, -hw, minU, minV, maxU, maxV, r8, g8, b8, a, lu, lv, 0, 0, 1);
        // west
        addQuadSide(builder, matrixStackIn, -hw, 0, -hw, -hw, h, hw, minU + inc, minV, maxU + inc, maxV, r6, g6, b6, a, lu, lv, -1, 0, 0);
        // south
        addQuadSide(builder, matrixStackIn, -hw, 0, hw, hw, h, hw, minU + 2 * inc, minV, maxU + 2 * inc, maxV, r8, g8, b8, a, lu, lv, 0, 0, -1);
        // east
        addQuadSide(builder, matrixStackIn, hw, 0, hw, hw, h, -hw, minU + 3 * inc, minV, maxU + 3 * inc, maxV, r6, g6, b6, a, lu, lv, 1, 0, 0);
    }


    public static void addQuadSide(VertexConsumer builder, PoseStack matrixStackIn, float x0, float y0, float z0, float x1, float y1, float z1, float u0, float v0, float u1, float v1, float r, float g,
                                   float b, float a, int lu, int lv, float nx, float ny, float nz) {
        addVert(builder, matrixStackIn, x0, y0, z0, u0, v1, r, g, b, a, lu, lv, nx, ny, nz);
        addVert(builder, matrixStackIn, x1, y0, z1, u1, v1, r, g, b, a, lu, lv, nx, ny, nz);
        addVert(builder, matrixStackIn, x1, y1, z1, u1, v0, r, g, b, a, lu, lv, nx, ny, nz);
        addVert(builder, matrixStackIn, x0, y1, z0, u0, v0, r, g, b, a, lu, lv, nx, ny, nz);
    }

    public static void addQuadTop(VertexConsumer builder, PoseStack matrixStackIn, float x0, float y0, float z0, float x1, float y1, float z1, float u0, float v0, float u1, float v1, float r, float g,
                                  float b, float a, int lu, int lv, float nx, float ny, float nz) {
        addVert(builder, matrixStackIn, x0, y0, z0, u0, v1, r, g, b, a, lu, lv, nx, ny, nz);
        addVert(builder, matrixStackIn, x1, y0, z0, u1, v1, r, g, b, a, lu, lv, nx, ny, nz);
        addVert(builder, matrixStackIn, x1, y1, z1, u1, v0, r, g, b, a, lu, lv, nx, ny, nz);
        addVert(builder, matrixStackIn, x0, y1, z1, u0, v0, r, g, b, a, lu, lv, nx, ny, nz);
    }


    public static void addVert(VertexConsumer builder, PoseStack matrixStackIn, float x, float y, float z, float u, float v, float r, float g,
                               float b, float a, int lu, int lv, float nx, float ny, float nz) {
        builder.vertex(matrixStackIn.last().pose(), x, y, z).color(r, g, b, a).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lu, lv)
                .normal(matrixStackIn.last().normal(), nx, ny, nz).endVertex();
    }

}
