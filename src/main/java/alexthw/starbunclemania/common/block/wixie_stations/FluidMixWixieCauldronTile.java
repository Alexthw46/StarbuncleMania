package alexthw.starbunclemania.common.block.wixie_stations;

import alexthw.starbunclemania.registry.ModRegistry;
import alexthw.starbunclemania.wixie.FluidCraftManager;
import alexthw.starbunclemania.wixie.FluidRecipeWrapper;
import com.hollingsworth.arsnouveau.common.block.tile.WixieCauldronTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public class FluidMixWixieCauldronTile extends WixieCauldronTile {
    public FluidMixWixieCauldronTile(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public @NotNull BlockEntityType<?> getType() {
        return ModRegistry.FLUID_MIX_WIXIE_CAULDRON_TILE.get();
    }


    @Override
    public FluidRecipeWrapper getRecipesForStack(ItemStack stack) {
        return FluidRecipeWrapper.fromStack(stack, level);
    }

    public static BlockPos getFluidNeeded(FluidStack fluidstack, Level level, BlockPos pos) {
        for (BlockPos bPos : BlockPos.withinManhattan(pos.below(2), 4, 3, 4)) {
            var tile = level.getBlockEntity(bPos);

            if (tile != null && tile.getCapability(ForgeCapabilities.FLUID_HANDLER).orElse(null) instanceof IFluidHandler) {
                var handler = tile.getCapability(ForgeCapabilities.FLUID_HANDLER).orElse(null);
                if (handler.drain(fluidstack, IFluidHandler.FluidAction.SIMULATE).getAmount() == fluidstack.getAmount())
                    return bPos.immutable();
            }
        }
        return null;
    }

    public FluidStack getNeededFluid() {
        return craftManager instanceof FluidCraftManager ? ((FluidCraftManager) craftManager).getNeededFluid() : FluidStack.EMPTY;
    }


    boolean giveFluid(FluidStack fluidStack) {
        boolean ret = craftManager instanceof FluidCraftManager && ((FluidCraftManager) craftManager).giveFluid(fluidStack);
        level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 3);
        return ret;
    }


//    public void rotateCraft(){
//        BlockPos leftBound = worldPosition.below().south().east();
//        BlockPos rightBound = worldPosition.above().north().west();
//        List<ItemStack> itemStacks = new ArrayList<>();
//        if(this.setStack != null && !this.setStack.isEmpty()){
//            itemStacks.add(this.setStack);
//        }
//        for(BlockPos pos : BlockPos.betweenClosed(leftBound, rightBound)){
//            if(level.getBlockEntity(pos) instanceof ArcanePedestalTile pedestalTile
//               && !pedestalTile.getStack().isEmpty()
//               && !pedestalTile.hasSignal){
//                itemStacks.add(pedestalTile.getStack().copy());
//            }
//        }
//        if(itemStacks.isEmpty())
//            return;
//        // Get the next recipe to craft
//        if(this.craftingIndex >= itemStacks.size()){
//            this.craftingIndex = 0;
//        }
//        ItemStack nextStack = itemStacks.get(this.craftingIndex);
//        FluidRecipeWrapper recipeWrapper = getRecipesForStack(nextStack);
//        craftingIndex++;
//        if(recipeWrapper == null || recipeWrapper.isEmpty()){
//            return;
//        }
//        Map<Item, Integer> count = getInventoryCount();
//
//        FluidRecipeWrapper.InstructionsForRecipe instructions = recipeWrapper.canCraftL(count, level, worldPosition);
//        if(instructions == null)
//            return;
//        {
//            craftManager = new FluidCraftManager(instructions.recipe().outputStack.copy(), instructions.itemsNeeded(), instructions.fluidsNeeded());
//            level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 3);
//        }
//        stackBeingCrafted = nextStack.copy();
//        updateBlock();
//    }
}
