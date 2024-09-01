package alexthw.starbunclemania.client;

import alexthw.starbunclemania.StarbuncleMania;
import alexthw.starbunclemania.registry.EidolonCompat;
import alexthw.starbunclemania.registry.ModRegistry;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = StarbuncleMania.MODID, bus = EventBusSubscriber.Bus.MOD)
@OnlyIn(Dist.CLIENT)
public class ClientHandler {
    @SubscribeEvent
    public static void bindRenderers(final EntityRenderersEvent.RegisterRenderers event) {

        event.registerBlockEntityRenderer(ModRegistry.FLUID_SOURCELINK_TILE.get(), FluidSourceLinkRenderer::new);
        event.registerBlockEntityRenderer(ModRegistry.SOURCE_CONDENSER_TILE.get(), SourceCondenserRenderer::new);

        event.registerEntityRenderer(ModRegistry.STARBY_MOUNT.get(), ResizedStarbRender::new);

        if (ModList.get().isLoaded("eidolon")) {
            EidolonCompat.onRegisterRenders(event);
        }
    }

    @SubscribeEvent
    public static void init(final FMLClientSetupEvent evt) {

        BlockEntityRenderers.register(ModRegistry.FLUID_JAR_TILE.get(), context -> new JarRenderer());
        evt.enqueueWork(() -> ItemProperties.register(ModRegistry.DIRECTION_SCROLL.get(), ResourceLocation.fromNamespaceAndPath(StarbuncleMania.MODID, "side"), (stack, level, entity, seed) -> {
            var tag = stack.get(ModRegistry.DIRECTION);
            return tag != null ? tag.direction().ordinal() : -1;
        }));

    }

    @SubscribeEvent
    public static void registerColors(final RegisterColorHandlersEvent.Item event){
        event.register((stack, tintIndex) -> tintIndex == 0 ? stack.get(DataComponents.DYED_COLOR).rgb() : -1,
                ModRegistry.STARBALLON.get());
    }


}
