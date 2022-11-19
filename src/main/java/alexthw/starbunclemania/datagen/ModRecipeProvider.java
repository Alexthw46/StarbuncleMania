package alexthw.starbunclemania.datagen;

import static alexthw.starbunclemania.registry.ModRegistry.*;
import static com.hollingsworth.arsnouveau.setup.ItemsRegistry.SOURCE_GEM;

import alexthw.starbunclemania.registry.ModRegistry;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(DataGenerator pGenerator) {
        super(pGenerator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        //items
        shaped(STARBATTERY)
                .define('R', Items.REDSTONE)
                .define('C', Items.COPPER_INGOT)
                .define('N', Items.GOLD_NUGGET)
                .pattern(" N ")
                .pattern("CRC")
                .pattern("CRC")
                .save(consumer);

        shaped(STARTRASH)
                .define('I', Items.IRON_INGOT)
                .define('N', Items.IRON_NUGGET)
                .pattern(" N ")
                .pattern("I I")
                .pattern("NIN")
                .save(consumer);

        //blocks
        shapedB(FLUID_JAR)
                .define('G', Items.GLASS)
                .define('C', BlockRegistry.CASCADING_LOG.asItem())
                .define('W', ItemsRegistry.WATER_ESSENCE)
                .pattern(" C ")
                .pattern("GWG")
                .pattern("CCC")
                .save(consumer);
        shapedB(FLUID_SOURCELINK)
                .pattern(" S ")
                .pattern("GBG")
                .pattern(" S ")
                .define('G', Tags.Items.INGOTS_GOLD)
                .define('S', SOURCE_GEM)
                .define('B', SOURCE_FLUID_BUCKET.get())
                .save(consumer);
        shapedB(SOURCE_CONDENSER)
                .define('J', FLUID_JAR.get())
                .define('S', SOURCE_GEM)
                .pattern("SSS")
                .pattern("SJS")
                .pattern("SSS")
                .save(consumer);
    }

    public ShapedRecipeBuilder shaped(RegistryObject<Item> result){
        return ShapedRecipeBuilder.shaped(result.get()).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK));
    }
    public ShapedRecipeBuilder shapedB(RegistryObject<Block> result){
        return ShapedRecipeBuilder.shaped(result.get()).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK));
    }
    public ShapelessRecipeBuilder shapelessBuilder(ItemLike result) {
        return shapelessBuilder(result, 1);
    }

    public ShapelessRecipeBuilder shapelessBuilder(ItemLike result, int resultCount) {
        return ShapelessRecipeBuilder.shapeless(result, resultCount).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK));
    }

}
