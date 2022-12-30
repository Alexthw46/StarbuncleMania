package alexthw.starbunclemania.common.block;

import alexthw.starbunclemania.registry.ModRegistry;
import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.potion.PotionData;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.block.tile.PotionJarTile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.List;

public class LiquidJarTile extends AbstractTankTile implements ITooltipProvider, ITickable {

    public static int capacity = 16000;

    public LiquidJarTile(BlockPos pos, BlockState state) {
        super(ModRegistry.FLUID_JAR_TILE.get(), pos, state);
    }

    public FluidStack getFluid() {
        return this.tank.getFluid();
    }

    public float getFluidPercentage() {
        return (float) this.getFluidAmount() / capacity;
    }

    public void setFluid(FluidStack fluidFromTag) {
        this.tank.setFluid(fluidFromTag);
    }

    @Override
    public void tick() {
        if (level instanceof ServerLevel && level.getGameTime() % 20 == 0)
            if (!this.tank.isEmpty() && this.tank.getFluid().hasTag() && this.tank.getFluidAmount() >= 250 && this.tank.getFluid().getFluid().is(ModRegistry.POTION)) {
                BlockEntity be = level.getBlockEntity(this.getBlockPos().above());
                if (be instanceof PotionJarTile potionJar && potionJar.getAmount() < potionJar.getMaxFill()) {
                    PotionData stored = PotionData.fromTag(tank.getFluid().getTag());
                    if (potionJar.canAccept(stored, 100)) {
                        potionJar.add(stored, 100);
                        this.tank.drain(250, IFluidHandler.FluidAction.EXECUTE);
                    }
                }
            }
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
