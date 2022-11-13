package alexthw.starbunclemania.common.block;

import alexthw.starbunclemania.registry.ModRegistry;
import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.common.block.tile.ModdedTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LiquidJarTile extends ModdedTile implements ITooltipProvider {

    static public final int capacity = 16000;

    protected final FluidTank tank = new FluidTank(capacity) {
        protected void onContentsChanged() {
            LiquidJarTile.this.updateBlock();
            LiquidJarTile.this.setChanged();
        }
    };

    private final LazyOptional<IFluidHandler> holder = LazyOptional.of(() -> this.tank);

    public LiquidJarTile(BlockPos pos, BlockState state) {
        super(ModRegistry.FLUID_JAR_TILE.get(), pos, state);
    }

    @NotNull
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction facing) {
        return capability == ForgeCapabilities.FLUID_HANDLER ? this.holder.cast() : super.getCapability(capability, facing);
    }

    public boolean interact(Player player, InteractionHand hand) {
        return FluidUtil.interactWithFluidHandler(player, hand, this.tank);
    }

    public int getFluidAmount() {
        return this.getFluid().getAmount();
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

    public FluidStack getFluid() {
        return this.tank.getFluid();
    }

    public int getFluidPercentage() {
        return this.getFluidAmount() / (capacity / 10) + 1;
    }

    public void setFluid(FluidStack fluidFromTag) {
        this.tank.setFluid(fluidFromTag);
    }

    /**
     * A list of tool tips to render on the screen when looking at this target.
     */
    @Override
    public void getTooltip(List<Component> tooltip) {
        FluidStack fluid = getFluid();
        LiquidJarTile.displayFluidTooltip(tooltip, fluid);
    }

    public static void displayFluidTooltip(List<Component> tooltip, FluidStack fluid) {
        if (fluid.isEmpty()) return;
        tooltip.add(Component.translatable("starbunclemania.tooltip.fluid_jar", fluid.getAmount()).append(fluid.getDisplayName()));
    }

}
