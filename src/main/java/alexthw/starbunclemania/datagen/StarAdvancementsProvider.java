package alexthw.starbunclemania.datagen;

import alexthw.starbunclemania.StarbuncleMania;
import alexthw.starbunclemania.registry.ModRegistry;
import com.google.common.collect.ImmutableMap;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.advancement.ANCriteriaTriggers;
import com.hollingsworth.arsnouveau.common.datagen.advancement.ANAdvancementBuilder;
import com.hollingsworth.arsnouveau.common.datagen.advancement.ANAdvancements;
import net.minecraft.advancements.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class StarAdvancementsProvider extends AdvancementProvider {


    public StarAdvancementsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper existingFileHelper) {
        super(output, registries, existingFileHelper, List.of(new AEAdvancements()));
    }

    public static class AEAdvancements extends ANAdvancements {

        static Consumer<AdvancementHolder> advancementConsumer;

        static AdvancementHolder dummy(String name) {
            return new AdvancementHolder(ResourceLocation.fromNamespaceAndPath(ArsNouveau.MODID, name), new Advancement( Optional.empty(), Optional.empty(), AdvancementRewards.EMPTY, Map.of(), new AdvancementRequirements(List.of()) , false));
        }


        @Override
        public void generate(HolderLookup.Provider registries, @NotNull Consumer<AdvancementHolder> con, @NotNull ExistingFileHelper existingFileHelper) {
            advancementConsumer = con;
            AdvancementHolder starbyCharm = dummy("starby_charm");
            saveBasicItem(ModRegistry.STARBUCKET.get(), starbyCharm);
            saveBasicItem(ModRegistry.STARBALLON.get(), starbyCharm);
            saveBasicItem(ModRegistry.STARBATTERY.get(), starbyCharm);
            saveBasicItem(ModRegistry.STARTRASH.get(), starbyCharm);
            saveBasicItem(ModRegistry.PROFHAT.get(), starbyCharm);

            AdvancementHolder jar = dummy("source_jar");
            var fluidJar = saveBasicItem(ModRegistry.FLUID_JAR.get(), jar);
            saveBasicItem(ModRegistry.SOURCE_CONDENSER.get(), fluidJar);
            saveBasicItem(ModRegistry.FLUID_SOURCELINK.get(), fluidJar);

            AdvancementHolder wixie = dummy("wixie_charm");
            builder("wixie_cook").display(Blocks.FURNACE, AdvancementType.TASK, false).addCriterion(ANCriteriaTriggers.createCriterion(ModRegistry.WIXIE_1)).parent(wixie).save(con);
            builder("wixie_stoneworks").display(Blocks.STONECUTTER, AdvancementType.TASK, false).addCriterion(ANCriteriaTriggers.createCriterion(ModRegistry.WIXIE_2)).parent(wixie).save(con);

        }

        public AdvancementHolder saveBasicItem(ItemLike item, AdvancementHolder parent) {
            return buildBasicItem(item, BuiltInRegistries.ITEM.getKey(item.asItem()).getPath(), AdvancementType.TASK, parent).save(advancementConsumer);
        }

        public ANAdvancementBuilder buildBasicItem(ItemLike item, String id, AdvancementType frame, AdvancementHolder parent) {
            return builder(id).display(item, frame).requireItem(item).parent(parent);
        }

        public ANAdvancementBuilder builder(String key) {
            return ANAdvancementBuilder.builder(StarbuncleMania.MODID, key);
        }

    }
}
