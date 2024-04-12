package alexthw.starbunclemania.wixie;

import com.hollingsworth.arsnouveau.api.recipe.MultiRecipeWrapper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FarmerDelightRecipeWrappers {


    public static class CuttingRecipeWrapper extends MultiRecipeWrapper {
        public static Map<Item, MultiRecipeWrapper> RECIPE_CACHE = new HashMap<>();

        public static MultiRecipeWrapper fromStack(ItemStack stack, Level level) {
            MultiRecipeWrapper wrapper = new MultiRecipeWrapper();
            if (RECIPE_CACHE.containsKey(stack.getItem())) {
                return RECIPE_CACHE.get(stack.getItem());
            }
            if (level.getServer() == null) return wrapper;
            for (Recipe<?> r : level.getServer().getRecipeManager().getRecipes()) {

                if (r.getResultItem(level.registryAccess()).getItem() != stack.getItem())
                    continue;

                if (r instanceof CuttingBoardRecipe)
                    wrapper.addRecipe(r.getIngredients(), r.getResultItem(level.registryAccess()), r);

            }

            RECIPE_CACHE.put(stack.getItem(), wrapper);

            return wrapper;
        }
    }

    public static class PotRecipeWrapper extends MultiRecipeWrapper {
        public static Map<Item, MultiRecipeWrapper> RECIPE_CACHE = new HashMap<>();

        public static MultiRecipeWrapper fromStack(ItemStack stack, Level level) {
            MultiRecipeWrapper wrapper = new MultiRecipeWrapper();
            if (RECIPE_CACHE.containsKey(stack.getItem())) {
                return RECIPE_CACHE.get(stack.getItem());
            }
            if (level.getServer() == null) return wrapper;
            for (Recipe<?> r : level.getServer().getRecipeManager().getRecipes()) {

                if (r.getResultItem(level.registryAccess()).getItem() != stack.getItem())
                    continue;

                if (r instanceof CookingPotRecipe) {
                    ArrayList<Ingredient> extended_ingredients = new ArrayList<>(r.getIngredients());
                    extended_ingredients.add(Ingredient.of(Items.BOWL));
                    wrapper.addRecipe(extended_ingredients, r.getResultItem(level.registryAccess()), r);
                }

            }

            RECIPE_CACHE.put(stack.getItem(), wrapper);

            return wrapper;
        }
    }

}
