package alexthw.starbunclemania.common.block;

import alexthw.starbunclemania.Configs;
import alexthw.starbunclemania.registry.ModRegistry;
import com.hollingsworth.arsnouveau.common.block.tile.SourcelinkTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidSourcelinkTile extends SourcelinkTile {

    public FluidSourcelinkTile(BlockPos pos, BlockState state) {
        super(ModRegistry.FLUID_SOURCELINK_TILE.get(), pos, state);
    }

    public static int capacity = 16000;

    private final LazyOptional<IFluidHandler> holder = LazyOptional.of(() -> this.tank);

    protected final FluidTank tank = new FluidTank(capacity) {
        protected void onContentsChanged() {
            FluidSourcelinkTile.this.updateBlock();
            FluidSourcelinkTile.this.setChanged();
        }
    };

    @Override
    public void tick() {
        super.tick();
        if (level != null && !level.isClientSide() && level.getGameTime() % 20 == 0 && this.canAcceptSource()) {
            int sourceFromFluid = getSourceFromFluid(this.getFluid());
            if (sourceFromFluid > 0) {
                int drain = this.tank.drain(1000, IFluidHandler.FluidAction.EXECUTE).getAmount();
                this.addSource(drain * sourceFromFluid);
            }
        }
    }

    int getSourceFromFluid(FluidStack tank) {
        if (!tank.isEmpty()) {
            ResourceLocation fluid = ForgeRegistries.FLUIDS.getKey(tank.getFluid());
            if (fluid != null && Configs.FLUID_TO_SOURCE_MAP.containsKey(fluid)){
                return Configs.FLUID_TO_SOURCE_MAP.get(fluid);
            }
        }
        return 0;
    }

    @NotNull
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction facing) {
        return capability == ForgeCapabilities.FLUID_HANDLER ? this.holder.cast() : super.getCapability(capability, facing);
    }

    public boolean interact(Player player, InteractionHand hand) {
        return FluidUtil.interactWithFluidHandler(player, hand, this.tank);
    }

    public FluidStack getFluid() {
        return this.tank.getFluid();
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        if (level != null)
            level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 8);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (!tank.isEmpty()) {
            tank.writeToNBT(tag);
        }
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        tank.readFromNBT(pTag);
    }

}
