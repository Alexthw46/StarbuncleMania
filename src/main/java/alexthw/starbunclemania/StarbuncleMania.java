package alexthw.starbunclemania;

import alexthw.starbunclemania.registry.ModRegistry;
import alexthw.starbunclemania.registry.SourceFluid;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidInteractionRegistry;

import java.util.Objects;

import static alexthw.starbunclemania.registry.ModRegistry.SOURCE_FLUID_TYPE;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(StarbuncleMania.MODID)
public class StarbuncleMania {
    public static final String MODID = "starbunclemania";

    public StarbuncleMania(ModContainer modContainer, IEventBus modbus) {
        //ArsNouveau.isDebug = false;
        NeoForgeMod.enableMilkFluid();
        ModRegistry.registerRegistries(modbus);
        modContainer.registerConfig(ModConfig.Type.SERVER, Configs.SERVER_SPEC);
        modContainer.registerConfig(ModConfig.Type.COMMON, Configs.COMMON_SPEC);
        ArsNouveauRegistry.register();
        modbus.addListener(this::setup);
        if (FMLEnvironment.dist.isClient()) {
            new SourceFluid.FluidTypeSourceClient(modbus);
        }
    }

    public static ResourceLocation prefix(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    private void setup(final FMLCommonSetupEvent ignoredEvent) {
        ArsNouveauRegistry.postInit();
        try {
            FluidInteractionRegistry.addInteraction(SOURCE_FLUID_TYPE.get(),
                    new FluidInteractionRegistry.InteractionInformation(
                            (level, currentPos, relativePos, currentState) ->
                                    level.getFluidState(relativePos).getFluidType() == NeoForgeMod.LAVA_TYPE.value() && level.getBlockState(currentPos.below()).is(Blocks.BLUE_ICE),
                            Objects.requireNonNull(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ArsNouveau.MODID, LibBlockNames.SMOOTH_SOURCESTONE))).defaultBlockState()));
            FluidInteractionRegistry.addInteraction(SOURCE_FLUID_TYPE.get(),
                    new FluidInteractionRegistry.InteractionInformation(
                            (level, currentPos, relativePos, currentState) ->
                                    level.getFluidState(relativePos).getFluidType() == NeoForgeMod.LAVA_TYPE.value(),
                            Objects.requireNonNull(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ArsNouveau.MODID, LibBlockNames.SOURCESTONE))).defaultBlockState()));
        } catch (NullPointerException npe) {
            System.out.println("Sourcestone not found, skipping interaction.");
        }
    }

}
