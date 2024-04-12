package alexthw.starbunclemania.datagen;

import alexthw.starbunclemania.StarbuncleMania;
import alexthw.starbunclemania.registry.ModRegistry;
import com.google.common.collect.ImmutableMap;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.datagen.advancement.ANAdvancementBuilder;
import com.hollingsworth.arsnouveau.common.datagen.advancement.ANAdvancements;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class StarAdvancementsProvider extends ForgeAdvancementProvider {


    public StarAdvancementsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper existingFileHelper) {
        super(output, registries, existingFileHelper, List.of(new AEAdvancements()));
    }

    public static class AEAdvancements extends ANAdvancements {

        static Consumer<Advancement> advancementConsumer;

        static Advancement dummy(String name) {
            return new Advancement(new ResourceLocation(ArsNouveau.MODID, name), null, null, AdvancementRewards.EMPTY, ImmutableMap.of(), null, false);
        }


        @Override
        public void generate(HolderLookup.Provider registries, Consumer<Advancement> con, ExistingFileHelper existingFileHelper) {
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

            Advancement wixie = dummy("wixie_charm");
            builder("wixie_cook").display(Blocks.FURNACE, FrameType.TASK, false).addCriterion(new PlayerTrigger.TriggerInstance(ModRegistry.WIXIE_1.getId(), ContextAwarePredicate.ANY)).parent(wixie).save(con);
            builder("wixie_stoneworks").display(Blocks.STONECUTTER, FrameType.TASK, false).addCriterion(new PlayerTrigger.TriggerInstance(ModRegistry.WIXIE_2.getId(), ContextAwarePredicate.ANY)).parent(wixie).save(con);

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
