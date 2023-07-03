package alexthw.starbunclemania.starbuncle.fluid;

import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.GoToPosGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public class FluidStoreGoal extends GoToPosGoal<StarbyFluidBehavior> {


    public FluidStoreGoal(Starbuncle starbuncle, StarbyFluidBehavior behavior) {
        super(starbuncle, behavior, () -> !behavior.getFluidStack().isEmpty());
    }

    @Override
    public void start() {
        super.start();
        starbuncle.goalState = Starbuncle.StarbuncleGoalState.STORING_ITEM;
    }

    @Nullable
    @Override
    public BlockPos getDestination() {
        return behavior.getTankForStorage();
    }

    @Override
    public boolean isDestinationStillValid(BlockPos pos) {
        return behavior.canStore(pos, behavior.getFluidStack());
    }

    /**
     * Returns whether we are done and can end the goal.
     */
    @Override
    public boolean onDestinationReached() {
        IFluidHandler fluidHandler = behavior.getHandlerFromCap(targetPos);
        int tankIndex = 0;
        if (fluidHandler != null) {
            int room = fluidHandler.fill(behavior.getFluidStack(), IFluidHandler.FluidAction.SIMULATE);
            if (room <= 0) return true;
            FluidStack fill = new FluidStack(behavior.getFluidStack(), room);
            int diff = fluidHandler.fill(fill, IFluidHandler.FluidAction.EXECUTE);
            behavior.getFluidStack().shrink(diff);
            behavior.syncTag();
            starbuncle.level.playSound(null, targetPos, SoundEvents.BUCKET_EMPTY, SoundSource.NEUTRAL, 0.5f, 1.3f);
        }
        return true;
    }
}
