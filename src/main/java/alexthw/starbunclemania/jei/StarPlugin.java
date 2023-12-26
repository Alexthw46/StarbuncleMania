package alexthw.starbunclemania.jei;

import alexthw.starbunclemania.StarbuncleMania;
import alexthw.starbunclemania.recipe.FluidSourcelinkRecipe;
import alexthw.starbunclemania.registry.ModRegistry;
import com.hollingsworth.arsnouveau.ArsNouveau;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class StarPlugin implements IModPlugin {

    public static final RecipeType<FluidSourcelinkRecipe> FLUID_SOURCELINK = RecipeType.create(StarbuncleMania.MODID, "fluid_sourcelink", FluidSourcelinkRecipe.class);

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(ArsNouveau.MODID, "extra");
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        //registration.getCraftingCategory().addCategoryExtension(DyeRecipe.class, DyeRecipeCategory::new);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(FLUID_SOURCELINK, ArsNouveau.proxy.getClientWorld().getRecipeManager().getAllRecipesFor(ModRegistry.FLUID_SOURCELINK_RT.get()));
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new FluidLinkRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(ModRegistry.FLUID_SOURCELINK.get().asItem().getDefaultInstance(), FLUID_SOURCELINK);
    }
}
