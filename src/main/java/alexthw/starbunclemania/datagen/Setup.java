package alexthw.starbunclemania.datagen;

import alexthw.starbunclemania.StarbuncleMania;
import alexthw.starbunclemania.registry.ModRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = StarbuncleMania.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Setup {

    //use runData configuration to generate stuff, event.includeServer() for data, event.includeClient() for assets
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();

        gen.addProvider(event.includeClient(), new StarItemModelProvider(gen, event.getExistingFileHelper()));
        gen.addProvider(event.includeServer(), new StarBlockTagsProvider(gen, event.getLookupProvider(), event.getExistingFileHelper()));
        gen.addProvider(event.includeServer(), new FluidTagsProvider(gen.getPackOutput(), event.getLookupProvider(), StarbuncleMania.MODID, event.getExistingFileHelper()) {
            @Override
            protected void addTags(HolderLookup.@NotNull Provider provider) {
                tag(ModRegistry.POTION).addOptional(ResourceLocation.fromNamespaceAndPath("create", "potion")).addOptional(ResourceLocation.fromNamespaceAndPath("hexerei", "potion"));
            }
        });
        gen.addProvider(event.includeServer(), new StarAdvancementsProvider(gen.getPackOutput(), event.getLookupProvider(), event.getExistingFileHelper()));
        gen.addProvider(event.includeServer(), new ModRecipeProvider(gen, event.getLookupProvider()));
        gen.addProvider(event.includeServer(), new ArsProviders.ImbuementProvider(gen));
        gen.addProvider(event.includeServer(), new ArsProviders.GlyphProvider(gen));
        gen.addProvider(event.includeServer(), new ArsProviders.EnchantingAppProvider(gen));

        gen.addProvider(event.includeServer(), new ArsProviders.StarPatchouliProvider(gen));
    }

}
