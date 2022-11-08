package alexthw.starbunclemania;

import alexthw.starbunclemania.registry.ModRegistry;
import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidInteractionRegistry;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

import static alexthw.starbunclemania.registry.ModRegistry.*;
import static com.hollingsworth.arsnouveau.common.lib.LibBlockNames.SOURCESTONE;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(StarbuncleMania.MODID)
public class StarbuncleMania
{
    public static final String MODID = "starbunclemania";

    public StarbuncleMania() {
        IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();
        ModRegistry.registerRegistries(modbus);
        ArsNouveauRegistry.register();
        modbus.addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> new FluidTypeSourceClient(modbus));
    }

    public static ResourceLocation prefix(String path){
        return new ResourceLocation(MODID, path);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        try {
            FluidInteractionRegistry.addInteraction(SOURCE_FLUID_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(ForgeMod.LAVA_TYPE.get(), Objects.requireNonNull(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(ArsNouveau.MODID, SOURCESTONE))).defaultBlockState()));
        }catch (NullPointerException npe){
            System.out.println("Sourcestone not found, skipping interaction.");
        }
    }

    private static class FluidTypeSourceClient
    {
        private FluidTypeSourceClient(IEventBus modEventBus)
        {
            modEventBus.addListener(this::clientSetup);
            modEventBus.addListener(this::registerBlockColors);
        }

        public void clientSetup(FMLClientSetupEvent event)
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
