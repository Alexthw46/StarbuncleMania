package alexthw.starbunclemania.datagen;

import alexthw.starbunclemania.StarbuncleMania;
import alexthw.starbunclemania.registry.ModRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class StarBlockTagsProvider extends BlockTagsProvider {

        public StarBlockTagsProvider(DataGenerator gen, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
            super(gen.getPackOutput(), lookupProvider ,StarbuncleMania.MODID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.@NotNull Provider tagProvider) {
            addPickMineable(0, ModRegistry.SOURCE_CONDENSER.get(), ModRegistry.FLUID_SOURCELINK.get(), ModRegistry.FLUID_JAR.get());
        }

        void logsTag(Block... blocks) {
            tag(BlockTags.LOGS).add(blocks);
            tag(BlockTags.LOGS_THAT_BURN).add(blocks);
            tag(BlockTags.MINEABLE_WITH_AXE).add(blocks);
        }

        void addPickMineable(int level, Block... blocks) {
            for (Block block : blocks) {
                tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block);
                switch (level) {
                    case (1) -> tag(BlockTags.NEEDS_STONE_TOOL).add(block);
                    case (2) -> tag(BlockTags.NEEDS_IRON_TOOL).add(block);
                    case (3) -> tag(BlockTags.NEEDS_DIAMOND_TOOL).add(block);
                    case (4) -> tag(Tags.Blocks.NEEDS_NETHERITE_TOOL).add(block);
                }
            }

        }

        @Override
        public @NotNull String getName() {
            return "StarbuncleMania Block Tags";
        }
    }