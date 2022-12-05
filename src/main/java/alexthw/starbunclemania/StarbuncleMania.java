package alexthw.starbunclemania;

import alexthw.starbunclemania.registry.ModRegistry;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import com.hollingsworth.arsnouveau.setup.config.ANModConfig;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidInteractionRegistry;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
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

    public static final CreativeModeTab TAB = new CreativeModeTab(MODID) {
        @Override
        public ItemStack makeIcon() {
            return ItemsRegistry.STARBUNCLE_CHARM.asItem().getDefaultInstance();
        }

    };

    public StarbuncleMania() {
        IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();
        ModRegistry.registerRegistries(modbus);
        ANModConfig serverConfig = new ANModConfig(ModConfig.Type.SERVER, Configs.SERVER_SPEC, ModLoadingContext.get().getActiveContainer(), MODID + "-server");
        ModLoadingContext.get().getActiveContainer().addConfig(serverConfig);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Configs.COMMON_SPEC);
        ArsNouveauRegistry.register();
        modbus.addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> new FluidTypeSourceClient(modbus));
    }

    public static ResourceLocation prefix(String path){
        return new ResourceLocation(MODID, path);
    }

    private void setup(final FMLCommonSetupEvent ignoredEvent)
    {
        ArsNouveauRegistry.postInit();
        try {
            FluidInteractionRegistry.addInteraction(SOURCE_FLUID_TYPE.get(), new FluidInteractionRegistry.InteractionInformation((level, currentPos, relativePos, currentState) -> level.getFluidState(relativePos).getFluidType() == ForgeMod.LAVA_TYPE.get() && level.getFluidState(currentPos).isSource(), Objects.requireNonNull(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(ArsNouveau.MODID, SOURCESTONE))).defaultBlockState()));
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
