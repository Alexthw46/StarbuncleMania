package alexthw.starbunclemania.datagen;

import alexthw.starbunclemania.StarbuncleMania;
import alexthw.starbunclemania.registry.ModRegistry;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.datagen.Advancements;
import com.hollingsworth.arsnouveau.common.datagen.advancement.ANAdvancementBuilder;
import com.hollingsworth.arsnouveau.common.datagen.advancement.ANAdvancements;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Consumer;

public class StarAdvancementsProvider extends Advancements {
    public StarAdvancementsProvider(DataGenerator generatorIn, ExistingFileHelper fileHelperIn) {
        super(generatorIn, fileHelperIn);
    }

    @Override
    protected void registerAdvancements(Consumer<Advancement> consumer, ExistingFileHelper fileHelper) {
        for (Consumer<Consumer<Advancement>> consumer1 : ImmutableList.of(new AEAdvancements())) {
            consumer1.accept(consumer);
        }
    }

    public static class AEAdvancements extends ANAdvancements {

        static Consumer<Advancement> advancementConsumer;

        static Advancement dummy(String name) {
            return new Advancement(new ResourceLocation(ArsNouveau.MODID, name), null, null, AdvancementRewards.EMPTY, ImmutableMap.of(), null);
        }

        @Override
        public void accept(Consumer<Advancement> con) {
            advancementConsumer = con;
            Advancement starbyCharm = dummy("starby_charm");
            saveBasicItem(ModRegistry.STARBUCKET.get(), starbyCharm);
            saveBasicItem(ModRegistry.STARBALLON.get(), starbyCharm);
            saveBasicItem(ModRegistry.STARBATTERY.get(), starbyCharm);
            saveBasicItem(ModRegistry.STARTRASH.get(), starbyCharm);
            saveBasicItem(ModRegistry.PROFHAT.get(), starbyCharm);

            Advancement jar = dummy("source_jar");
            var fluidJar = saveBasicItem(ModRegistry.FLUID_JAR.get(), jar);
            saveBasicItem(ModRegistry.SOURCE_CONDENSER.get(), fluidJar);
            saveBasicItem(ModRegistry.FLUID_SOURCELINK.get(), fluidJar);

        }

        public Advancement saveBasicItem(ItemLike item, Advancement parent) {
            return buildBasicItem(item, ForgeRegistries.ITEMS.getKey(item.asItem()).getPath(), FrameType.TASK, parent).save(advancementConsumer);
        }

        public ANAdvancementBuilder buildBasicItem(ItemLike item, String id, FrameType frame, Advancement parent) {
            return builder(id).display(item, frame).requireItem(item).parent(parent);
        }

        public ANAdvancementBuilder builder(String key) {
            return ANAdvancementBuilder.builder(StarbuncleMania.MODID, key);
        }

    }
}
