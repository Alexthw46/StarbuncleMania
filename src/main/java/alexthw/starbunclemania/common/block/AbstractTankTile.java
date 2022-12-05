package alexthw.starbunclemania.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.ModdedTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AbstractTankTile extends ModdedTile {

    public int capacity = 16000;
    private final LazyOptional<IFluidHandler> holder = LazyOptional.of(() -> this.tank);

    protected final FluidTank tank = new FluidTank(capacity) {
        protected void onContentsChanged() {
            AbstractTankTile.this.updateBlock();
            AbstractTankTile.this.setChanged();
        }
    };

    public AbstractTankTile(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
    }

    @NotNull
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction facing) {
        return capability == ForgeCapabilities.FLUID_HANDLER ? this.holder.cast() : super.getCapability(capability, facing);
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
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        if (level != null) level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition),  level.getBlockState(worldPosition), 8);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (!tank.isEmpty()){
            tank.writeToNBT(tag);
        }
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        tank.readFromNBT(pTag);
    }


}
