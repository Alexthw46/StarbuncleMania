package alexthw.starbunclemania.common.block;

import alexthw.starbunclemania.registry.ModRegistry;
import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class LiquidJarTile extends AbstractTankTile implements ITooltipProvider {

    public static int capacity = 16000;

    public LiquidJarTile(BlockPos pos, BlockState state) {
        super(ModRegistry.FLUID_JAR_TILE.get(), pos, state);
    }

    public FluidStack getFluid() {
        return this.tank.getFluid();
    }

    public float getFluidPercentage() {
        return (float) this.getFluidAmount() / (capacity);
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
