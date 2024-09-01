package alexthw.starbunclemania.common.block.fluids;

import alexthw.starbunclemania.registry.ModRegistry;
import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.block.tile.PotionJarTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class LiquidJarTile extends AbstractTankTile implements ITooltipProvider, ITickable {

    public static final int capacity = 16000;

    public LiquidJarTile(BlockPos pos, BlockState state) {
        super(ModRegistry.FLUID_JAR_TILE.get(), pos, state);
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
            if (!this.tank.isEmpty() && this.tank.getFluid().has(DataComponents.POTION_CONTENTS) && this.tank.getFluidAmount() >= 250 && this.tank.getFluid().is(ModRegistry.POTION)) {
                BlockEntity be = level.getBlockEntity(this.getBlockPos().above());
                if (be instanceof PotionJarTile potionJar && potionJar.getAmount() < potionJar.getMaxFill()) {
                    PotionContents potion = tank.getFluid().get(DataComponents.POTION_CONTENTS);
                    if (potionJar.canAccept(potion, 100)) {
                        potionJar.add(potion, 100);
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

    public static void displayFluidTooltip(Consumer<Component> tooltip, FluidStack fluid) {
        if (fluid.isEmpty()) return;
        tooltip.accept(Component.translatable("starbunclemania.tooltip.fluid_jar", fluid.getAmount()).append(fluid.getHoverName()));
    }
    public static void displayFluidTooltip(List<Component> tooltip, FluidStack fluid) {
        if (fluid.isEmpty()) return;
        tooltip.add(Component.translatable("starbunclemania.tooltip.fluid_jar", fluid.getAmount()).append(fluid.getHoverName()));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.@NotNull Builder components) {
        components.set(ModRegistry.FLUID_CONTENT, SimpleFluidContent.copyOf(this.tank.getFluid()));
    }

    @Override
    protected void applyImplicitComponents(@NotNull DataComponentInput componentInput) {
        var tankData = componentInput.get(ModRegistry.FLUID_CONTENT);
        if (tankData != null) {
            this.tank.setFluid(tankData.copy());
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void removeComponentsFromTag(CompoundTag tag) {
        tag.remove("Fluid");
    }
}
