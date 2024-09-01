package alexthw.starbunclemania.common.block.fluids;

import com.hollingsworth.arsnouveau.common.block.tile.ModdedTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

public class AbstractTankTile extends ModdedTile {

    public int capacity = 16000;

    protected final FluidTank tank = new FluidTank(capacity) {
        protected void onContentsChanged() {
            AbstractTankTile.this.updateBlock();
            AbstractTankTile.this.setChanged();
        }
    };

    public AbstractTankTile(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
    }

    public boolean isFluidValid(FluidStack stack) {
        return true;
    }

    public boolean interact(Player player, InteractionHand hand) {
        return FluidUtil.interactWithFluidHandler(player, hand, this.tank);
    }

    public int getFluidAmount() {
        return this.tank.getFluid().getAmount();
    }
    public FluidStack getFluid(){
        return this.tank.getFluid();
    }

    @Override
    public void handleUpdateTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider pRegistries) {
        super.handleUpdateTag(tag, pRegistries);
        if (level != null) level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition),  level.getBlockState(worldPosition), 8);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider pRegistries) {
        super.saveAdditional(tag,pRegistries);
        if (!tank.isEmpty()){
            tank.writeToNBT(pRegistries, tag);
        }
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag pTag, HolderLookup.@NotNull Provider pRegistries) {
        super.loadAdditional(pTag,pRegistries);
        tank.readFromNBT(pRegistries, pTag);
    }


}
