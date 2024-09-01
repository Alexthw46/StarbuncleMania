package alexthw.starbunclemania.jei;

import alexthw.starbunclemania.recipe.FluidSourcelinkRecipe;
import alexthw.starbunclemania.registry.ModRegistry;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import mezz.jei.api.constants.ModIds;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class FluidLinkRecipeCategory implements IRecipeCategory<FluidSourcelinkRecipe> {
    private final LoadingCache<Integer, IDrawableAnimated> cachedArrows;
    public final IDrawable background;
    public final IDrawable icon;

    static final ResourceLocation ARROW = ResourceLocation.fromNamespaceAndPath(ModIds.JEI_ID, "textures/jei/gui/gui_vanilla.png");

    public FluidLinkRecipeCategory(final IGuiHelper helper) {
        this.background = helper.createBlankDrawable(120, 32);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, ModRegistry.FLUID_SOURCELINK.get().asItem().getDefaultInstance());
        this.cachedArrows = CacheBuilder.newBuilder().maximumSize(25L).build(new CacheLoader<>() {
            public @NotNull IDrawableAnimated load(@NotNull Integer cookTime) {
                return helper.drawableBuilder(ARROW, 82, 128, 24, 32)
                        .buildAnimated(cookTime, IDrawableAnimated.StartDirection.LEFT, false);
            }
        });
    }


    /**
     * @return the type of recipe that this category handles.
     * @since 9.5.0
     */
    @Override
    public @NotNull RecipeType<FluidSourcelinkRecipe> getRecipeType() {
        return StarPlugin.FLUID_SOURCELINK;
    }

    /**
     * Returns a text component representing the name of this recipe type.
     * Drawn at the top of the recipe GUI pages for this category.
     *
     * @since 7.6.4
     */
    @Override
    public @NotNull Component getTitle() {
        return Component.literal("Fluid Sourcelink conversion");
    }

    public @NotNull IDrawable getBackground() {
        return this.background;
    }

    public @NotNull IDrawable getIcon() {
        return this.icon;
    }

    /**
     * Sets all the recipe's ingredients by filling out an instance of {@link IRecipeLayoutBuilder}.
     * This is used by JEI for lookups, to figure out what ingredients are inputs and outputs for a recipe.
     *
     * @since 9.4.0
     */
    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, FluidSourcelinkRecipe recipe, @NotNull IFocusGroup focuses) {
        ResourceLocation fluid_name = recipe.fluidType();
        try {
            var fluid = BuiltInRegistries.FLUID.get(fluid_name);
            builder.addSlot(RecipeIngredientRole.INPUT, 6,5).setFluidRenderer(1000, false, 16, 24).addIngredient(NeoForgeTypes.FLUID_STACK, new FluidStack(fluid, 1000));
        }catch (Exception ignored) {
        }
    }

    @Override
    public void draw(FluidSourcelinkRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        IDrawableAnimated arrow = this.cachedArrows.getUnchecked(40);
        arrow.draw(guiGraphics, 25, 10);
        Font font = Minecraft.getInstance().font;
        double ratio = recipe.conversion_ratio() * 1000;
        guiGraphics.drawString(font, String.format("%.0f Source", ratio), 55, 12, 0x000000, false);
    }
}
