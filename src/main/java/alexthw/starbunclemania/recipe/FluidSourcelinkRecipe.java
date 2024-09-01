package alexthw.starbunclemania.recipe;

import alexthw.starbunclemania.registry.ModRegistry;
import com.hollingsworth.arsnouveau.common.crafting.recipes.SpecialSingleInputRecipe;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record FluidSourcelinkRecipe(ResourceLocation fluidType, double conversion_ratio) implements SpecialSingleInputRecipe {

    @Override
    public boolean matches(@NotNull SingleRecipeInput pContainer, @NotNull Level pLevel) {
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull SingleRecipeInput pContainer, @NotNull HolderLookup.Provider registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider p_267052_) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRegistry.FLUID_SOURCELINK_RS.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return ModRegistry.FLUID_SOURCELINK_RT.get();
    }


    public static class Serializer implements RecipeSerializer<FluidSourcelinkRecipe> {

        public static @NotNull FluidSourcelinkRecipe fromNetwork(RegistryFriendlyByteBuf pBuffer) {
            return new FluidSourcelinkRecipe(pBuffer.readResourceLocation(), pBuffer.readDouble());
        }

        public static void toNetwork(RegistryFriendlyByteBuf pBuffer, FluidSourcelinkRecipe pRecipe) {
            pBuffer.writeResourceLocation(pRecipe.fluidType);
            pBuffer.writeDouble(pRecipe.conversion_ratio);
        }

        public static final MapCodec<FluidSourcelinkRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("fluid").forGetter(FluidSourcelinkRecipe::fluidType),
                Codec.DOUBLE.fieldOf("mb_to_source_ratio").forGetter(FluidSourcelinkRecipe::conversion_ratio)
        ).apply(instance, FluidSourcelinkRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, FluidSourcelinkRecipe> STREAM_CODEC = StreamCodec.of(
                FluidSourcelinkRecipe.Serializer::toNetwork, FluidSourcelinkRecipe.Serializer::fromNetwork
        );

        @Override
        public @NotNull MapCodec<FluidSourcelinkRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, FluidSourcelinkRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
