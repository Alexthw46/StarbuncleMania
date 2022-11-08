package alexthw.starbunclemania.client;

import alexthw.starbunclemania.StarbuncleMania;
import alexthw.starbunclemania.registry.ModRegistry;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = StarbuncleMania.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@OnlyIn(Dist.CLIENT)
public class ClientHandler {
/*
    @SubscribeEvent
    public static void initColors(final RegisterColorHandlersEvent.Item event) {
        event.getBlockColors().register((state, reader, pos, tIndex) ->
                reader != null && pos != null && reader.getBlockEntity(pos) instanceof LiquidJarTile jarTile
                        ? jarTile.getColor()
                        : -1, ModRegistry.FLUID_JAR.get());

        event.register((stack, color) -> {
            if (color > 0 || !stack.hasTag()) {
                return -1;
            }
            CompoundTag blockTag = stack.getOrCreateTag().getCompound("BlockEntityTag");
            if(blockTag.contains("FluidName")){
                FluidStack data = FluidStack.loadFluidStackFromNBT(blockTag);
                return IClientFluidTypeExtensions.of(data.getFluid()).getTintColor(data);
            }
            return -1;
        }, ModRegistry.FLUID_JAR.get().asItem());
    }
*/
    @SubscribeEvent
    public static void init(final FMLClientSetupEvent evt) {

        BlockEntityRenderers.register(ModRegistry.FLUID_JAR_TILE.get(), JarRenderer::new);

        /*evt.enqueueWork(() -> {
            ItemProperties.register(ModRegistry.FLUID_JAR.get().asItem(), new ResourceLocation(ArsNouveau.MODID, "source"), (stack, level, entity, seed) -> {
                CompoundTag tag = stack.getTag();
                return tag != null ? (tag.getCompound("BlockEntityTag").getInt("source") / 10000.0F) : 0.0F;
            });
        });
         */
    }
}
