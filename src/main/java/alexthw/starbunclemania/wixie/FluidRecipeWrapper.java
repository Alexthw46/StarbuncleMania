package alexthw.starbunclemania.wixie;

import alexthw.starbunclemania.common.block.wixie_stations.FluidMixWixieCauldronTile;
import com.hollingsworth.arsnouveau.api.recipe.MultiRecipeWrapper;
import com.hollingsworth.arsnouveau.api.recipe.ShapedHelper;
import com.hollingsworth.arsnouveau.api.recipe.SingleRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FluidRecipeWrapper extends MultiRecipeWrapper {
    public static Map<Item, FluidRecipeWrapper> RECIPE_CACHE = new HashMap<>();

    public static FluidRecipeWrapper fromStack(ItemStack stack, Level level) {
        FluidRecipeWrapper wrapper = new FluidRecipeWrapper();
        if (RECIPE_CACHE.containsKey(stack.getItem())) {
            return RECIPE_CACHE.get(stack.getItem());
        }
        for (Recipe<?> r : level.getServer().getRecipeManager().getRecipes()) {

            if (r.getResultItem(level.registryAccess()).getItem() != stack.getItem())
                continue;

            if (r instanceof ShapedRecipe) {
                ShapedHelper helper = new ShapedHelper((ShapedRecipe) r);

                for (List<Ingredient> ingredients : helper.getPossibleRecipes()) {
                    wrapper.addRecipe(ingredients, r.getResultItem(level.registryAccess()), r);
                }
            }

            if (r instanceof ShapelessRecipe) {
                wrapper.addRecipe(r.getIngredients(), r.getResultItem(level.registryAccess()), r);
            }
        }

        RECIPE_CACHE.put(stack.getItem(), wrapper);

        return wrapper;
    }

    @Nullable
    public InstructionsForRecipe canCraftL(Map<Item, Integer> inventory, Level world, BlockPos pos) {

        List<FluidStack> fluidsNeeded;

        for (SingleRecipe recipe : recipes) {
            List<ItemStack> itemsNeeded = getItemsNeeded(inventory, world, pos, recipe);
            fluidsNeeded = this.getFluidsNeeded(inventory, world, pos, recipe);

            if (itemsNeeded != null) {
                return new InstructionsForRecipe(recipe, itemsNeeded, fluidsNeeded);
            }
        }
        return null;
    }

    private List<FluidStack> getFluidsNeeded(Map<Item, Integer> inventory, Level world, BlockPos pos, SingleRecipe recipe) {
        List<FluidStack> fluids = new ArrayList<>();
        for (Ingredient i : recipe.recipeIngredients) {
            boolean foundStack = false;
            for (ItemStack stack : i.getItems()) {

                // Return success if we could consume this item as a liquid from a jar
                if (stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent() && stack.getItem() != Items.BUCKET && stack.getItem() != Items.GLASS_BOTTLE) {
                    var cap = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
                    if (cap == null) continue;
                    FluidStack testStack = cap.getFluidInTank(0).copy();
                    if (FluidMixWixieCauldronTile.getFluidNeeded(testStack, world, pos) != null) {
                        fluids.add(testStack);
                        foundStack = true;
                        break;
                    }
                }

            }
            if (!foundStack)
                return null;
        }
        return fluids;
    }

    @Override
    public List<ItemStack> getItemsNeeded(Map<Item, Integer> inventory, Level world, BlockPos pos, SingleRecipe recipe) {
        Map<Item, Integer> map = new HashMap<>(inventory);

        List<ItemStack> items = new ArrayList<>();
        for (Ingredient i : recipe.recipeIngredients) {
            boolean foundStack = false;
            for (ItemStack stack : i.getItems()) {

                // Return success if we could consume this item as a liquid from a jar
                if (stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent() && stack.getItem() != Items.BUCKET && stack.getItem() != Items.GLASS_BOTTLE) {
                    foundStack = true;
                    break;
                }

                // If our inventory has the item, decrease the effective count
                if (inventory.containsKey(stack.getItem()) && map.get(stack.getItem()) > 0) {
                    map.put(stack.getItem(), map.get(stack.getItem()) - 1);
                    foundStack = true;
                    items.add(stack.copy());
                    break;
                }
            }
            if (!foundStack)
                return null;
        }
        return items;
    }

    public record InstructionsForRecipe(SingleRecipe recipe, List<ItemStack> itemsNeeded,
                                        List<FluidStack> fluidsNeeded) {
    }

}
