package alexthw.starbunclemania.client;

import alexthw.starbunclemania.StarbuncleMania;
import alexthw.starbunclemania.registry.ModRegistry;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = StarbuncleMania.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@OnlyIn(Dist.CLIENT)
public class ClientHandler {
    @SubscribeEvent
    public static void bindRenderers(final EntityRenderersEvent.RegisterRenderers event) {

        event.registerEntityRenderer(ModRegistry.STARBY_MOUNT.get(), ResizedStarbRender::new);

    }

    @SubscribeEvent
    public static void init(final FMLClientSetupEvent evt) {

        BlockEntityRenderers.register(ModRegistry.FLUID_JAR_TILE.get(), context -> new JarRenderer());
        evt.enqueueWork(() -> ItemProperties.register(ModRegistry.DIRECTION_SCROLL.get(), new ResourceLocation(StarbuncleMania.MODID, "side"), (stack, level, entity, seed) -> {
            CompoundTag tag = stack.getTag();
            return tag != null ? tag.getInt("side") : -1;
        }));

    }

}
