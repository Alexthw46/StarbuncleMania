package alexthw.starbunclemania.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.function.Consumer;

import static alexthw.starbunclemania.registry.ModRegistry.*;

public class SourceFluid extends FluidType {
    /**
     * Default constructor.
     */
    public SourceFluid() {
        super(FluidType.Properties.create().supportsBoating(true).canHydrate(true).density(1).temperature(1).viscosity(1));
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

    public static class FluidTypeSourceClient
    {
        public FluidTypeSourceClient(IEventBus modEventBus)
        {
            modEventBus.addListener(this::clientSetup);
            modEventBus.addListener(this::registerBlockColors);
        }

        public void clientSetup(FMLClientSetupEvent ignoredEvent)
        {
            ItemBlockRenderTypes.setRenderLayer(SOURCE_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(SOURCE_FLUID_FLOWING.get(), RenderType.translucent());
        }

        private void registerBlockColors(RegisterColorHandlersEvent.Block event)
        {
            event.register((state, getter, pos, index) ->
            {
                if (getter != null && pos != null)
                {
                    FluidState fluidState = getter.getFluidState(pos);
                    return IClientFluidTypeExtensions.of(fluidState).getTintColor(fluidState, getter, pos);
                } else return 0xAF7FFFD4;
            }, SOURCE_FLUID_BLOCK.get());
        }
    }
}
