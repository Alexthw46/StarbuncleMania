package alexthw.starbunclemania.datagen;

import alexthw.starbunclemania.StarbuncleMania;
import alexthw.starbunclemania.registry.ModRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = StarbuncleMania.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Setup {

    //use runData configuration to generate stuff, event.includeServer() for data, event.includeClient() for assets
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();

        gen.addProvider(event.includeClient(), new StarItemModelProvider(gen, event.getExistingFileHelper()));
        gen.addProvider(event.includeServer(), new StarBlockTagsProvider(gen, event.getExistingFileHelper()));
        gen.addProvider(event.includeServer(), new FluidTagsProvider(gen, StarbuncleMania.MODID, event.getExistingFileHelper()){
            @Override
            protected void addTags() {
                tag(ModRegistry.POTION).addOptional(new ResourceLocation("create", "potion")).addOptional(new ResourceLocation("hexerei", "potion"));
            }
        });
        gen.addProvider(event.includeServer(), new StarAdvancementsProvider(gen, event.getExistingFileHelper()));
        gen.addProvider(event.includeServer(), new ModRecipeProvider(gen));
        gen.addProvider(event.includeServer(), new ArsProviders.ImbuementProvider(gen));
        gen.addProvider(event.includeServer(), new ArsProviders.GlyphProvider(gen));
        gen.addProvider(event.includeServer(), new ArsProviders.EnchantingAppProvider(gen));

        gen.addProvider(event.includeServer(), new ArsProviders.StarPatchouliProvider(gen));
    }

}
