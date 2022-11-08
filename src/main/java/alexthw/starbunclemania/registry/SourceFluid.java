package alexthw.starbunclemania.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class SourceFluid extends FluidType {
    /**
     * Default constructor.
     */
    public SourceFluid() {
        super(FluidType.Properties.create().supportsBoating(true).canHydrate(true).density(0).temperature(0).viscosity(0));
    }

    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer)
    {
        consumer.accept(new IClientFluidTypeExtensions()
        {
            private static final ResourceLocation STILL = new ResourceLocation(ArsNouveau.MODID,"blocks/potion_still"),
                    FLOW = new ResourceLocation(ArsNouveau.MODID,"blocks/potion_still"),
                    OVERLAY = new ResourceLocation(ArsNouveau.MODID, "blocks/sourcestone"),
                    VIEW_OVERLAY = new ResourceLocation(ArsNouveau.MODID, "textures/blocks/sourcestone.png");

            @Override
            public ResourceLocation getStillTexture()
            {
                return STILL;
            }

            @Override
            public ResourceLocation getFlowingTexture()
            {
                return FLOW;
            }

            @Override
            public ResourceLocation getOverlayTexture()
            {
                return OVERLAY;
            }

            @Override
            public ResourceLocation getRenderOverlayTexture(Minecraft mc)
            {
                return VIEW_OVERLAY;
            }

            @Override
            public int getTintColor()
            {
                return 0xDF9B13FB;
            }

            @Override
            public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor)
            {
                int color = this.getTintColor();
                return new Vector3f((color >> 16 & 0xFF) / 255F, (color >> 8 & 0xFF) / 255F, (color & 0xFF) / 255F);
            }

            @Override
            public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape)
            {
                nearDistance = -8F;
                farDistance = 24F;

                if (farDistance > renderDistance)
                {
                    farDistance = renderDistance;
                    shape = FogShape.CYLINDER;
                }

                RenderSystem.setShaderFogStart(nearDistance);
                RenderSystem.setShaderFogEnd(farDistance);
                RenderSystem.setShaderFogShape(shape);
            }
        });
    }
}
