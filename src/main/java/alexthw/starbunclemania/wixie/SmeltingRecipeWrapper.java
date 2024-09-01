package alexthw.starbunclemania.wixie;

import com.hollingsworth.arsnouveau.api.recipe.MultiRecipeWrapper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public class SmeltingRecipeWrapper extends MultiRecipeWrapper {

    public static Map<Item, MultiRecipeWrapper> RECIPE_CACHE = new HashMap<>();

    public static MultiRecipeWrapper fromStack(ItemStack stack, Level level) {
        MultiRecipeWrapper wrapper = new MultiRecipeWrapper();
        if (RECIPE_CACHE.containsKey(stack.getItem())) {
            return RECIPE_CACHE.get(stack.getItem());
        }
        if (level.getServer() == null) return wrapper;

        for (RecipeHolder<?> r : level.getServer().getRecipeManager().getRecipes()) {

            if (r.value().getResultItem(level.registryAccess()).getItem() != stack.getItem())
                continue;

            if (r.value() instanceof AbstractCookingRecipe cookingRecipe)
                wrapper.addRecipe(cookingRecipe.getIngredients(), cookingRecipe.getResultItem(level.registryAccess()), cookingRecipe);

        }

        RECIPE_CACHE.put(stack.getItem(), wrapper);

        return wrapper;
    }


}
