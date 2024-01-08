package alexthw.starbunclemania.common.block.wixie_stations;

import alexthw.starbunclemania.registry.FarmerDelightCompat;
import alexthw.starbunclemania.wixie.FarmerDelightRecipeWrappers;
import com.hollingsworth.arsnouveau.api.recipe.MultiRecipeWrapper;
import com.hollingsworth.arsnouveau.common.block.tile.WixieCauldronTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class FarmerPotWixieCauldronTile extends WixieCauldronTile {
    public FarmerPotWixieCauldronTile(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public @NotNull BlockEntityType<?> getType() {
        return FarmerDelightCompat.COOKING_POT_WIXIE_CAULDRON_TILE.get();
    }

    @Override
    public MultiRecipeWrapper getRecipesForStack(ItemStack stack) {
        return FarmerDelightRecipeWrappers.PotRecipeWrapper.fromStack(stack, level);
    }

}
