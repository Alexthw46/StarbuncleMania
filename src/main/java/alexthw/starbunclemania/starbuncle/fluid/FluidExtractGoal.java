package alexthw.starbunclemania.starbuncle.fluid;

import alexthw.starbunclemania.Configs;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.GoToPosGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public class FluidExtractGoal extends GoToPosGoal<StarbyFluidBehavior> {

    public FluidExtractGoal(Starbuncle starbuncle, StarbyFluidBehavior behavior) {
        super(starbuncle, behavior, () -> behavior.getFluidStack().isEmpty() && !behavior.FROM_LIST.isEmpty());
    }

    @Override
    public void start() {
        super.start();
        starbuncle.goalState = Starbuncle.StarbuncleGoalState.TAKING_ITEM;
    }

    @Nullable
    @Override
    public BlockPos getDestination() {
        return behavior.getTankToExtract();
    }


    @Override
    public boolean isDestinationStillValid(BlockPos pos) {
        return behavior.canExtract(pos);
    }

    /**
     * Returns whether we are done and can end the goal.
     */
    @Override
    public boolean onDestinationReached() {
        IFluidHandler fluidHandlerExtract = behavior.getHandlerFromCap(targetPos);
        int tankIndexE = 0;
        if (fluidHandlerExtract != null) {
            FluidStack toExtract = fluidHandlerExtract.getFluidInTank(tankIndexE);
            BlockPos pos = behavior.getTankForStorage(toExtract);
            if (pos == null) return true;
            IFluidHandler fluidHandlerStore = behavior.getHandlerFromCap(pos);

            if (fluidHandlerStore != null) {
                int maxRoom = -1;
                for (int s = 0; s < fluidHandlerStore.getTanks(); s++) {
                    maxRoom = fluidHandlerStore.fill(toExtract, IFluidHandler.FluidAction.SIMULATE);
                    if (maxRoom > 0) break;
                }
                if (maxRoom <= Configs.STARBUCKET_THRESHOLD.get()) return true;
                int takeAmount = Math.min(toExtract.getAmount(), Math.min(maxRoom, behavior.getRatio()));
                starbuncle.level().playSound(null, targetPos, SoundEvents.BUCKET_FILL, SoundSource.NEUTRAL, 0.5f, 1.3f);
                FluidStack extracted = new FluidStack(toExtract, takeAmount);
                behavior.setFluidStack(extracted);
                fluidHandlerExtract.drain(extracted, IFluidHandler.FluidAction.EXECUTE);
            }
        }

        return true;
    }
}
