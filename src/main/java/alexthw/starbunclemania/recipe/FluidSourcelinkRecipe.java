package alexthw.starbunclemania.recipe;

import alexthw.starbunclemania.registry.ModRegistry;
import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidSourcelinkRecipe implements Recipe<Container> {

    public ResourceLocation id;
    public ResourceLocation fluidType;
    public double conversion_ratio;

    public FluidSourcelinkRecipe(ResourceLocation id, ResourceLocation fluid, double conversion_ratio) {
        this.id = id;
        this.fluidType = fluid;
        this.conversion_ratio = conversion_ratio;
    }

    @Override
    public boolean matches(@NotNull Container pContainer, @NotNull Level pLevel) {
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull Container pContainer, @NotNull RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return false;
    }


    @Override
    public @NotNull ItemStack getResultItem(@NotNull RegistryAccess p_267052_) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return this.id;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRegistry.FLUID_SOURCELINK_RS.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return ModRegistry.FLUID_SOURCELINK_RT.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }


    public static class Serializer implements RecipeSerializer<FluidSourcelinkRecipe> {

        @Override
        public @NotNull FluidSourcelinkRecipe fromJson(@NotNull ResourceLocation pRecipeId, @NotNull JsonObject json) {
            double ratio = GsonHelper.getAsDouble(json, "mb_to_source_ratio");
            ResourceLocation fluid = ResourceLocation.tryParse(GsonHelper.getAsString(json, "fluid"));
            return new FluidSourcelinkRecipe(pRecipeId, fluid, ratio);
        }

        @Override
        public @Nullable FluidSourcelinkRecipe fromNetwork(@NotNull ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            return new FluidSourcelinkRecipe(pRecipeId, pBuffer.readResourceLocation(), pBuffer.readDouble());
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, FluidSourcelinkRecipe pRecipe) {
            pBuffer.writeResourceLocation(pRecipe.fluidType);
            pBuffer.writeDouble(pRecipe.conversion_ratio);
        }
    }
}
