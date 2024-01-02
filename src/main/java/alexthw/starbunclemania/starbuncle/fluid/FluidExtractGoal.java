package alexthw.starbunclemania.starbuncle.fluid;

import alexthw.starbunclemania.Configs;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.debug.DebugEvent;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.GoToPosGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public class FluidExtractGoal extends GoToPosGoal<StarbyFluidBehavior> {

    public FluidExtractGoal(Starbuncle starbuncle, StarbyFluidBehavior behavior) {
        super(starbuncle, behavior, () -> behavior.getFluidStack().isEmpty());
    }

    @Override
    public boolean canUse() {
        boolean superCan = super.canUse();
        if (!superCan || behavior.FROM_LIST.isEmpty())
            return false;
        if (getDestination() == null) {
            starbuncle.addGoalDebug(this, new DebugEvent("NoTakeDestination", "No valid take destination"));
            starbuncle.setBackOff(5 + starbuncle.level.random.nextInt(20));
            return false;
        }
        if (behavior.isBedPowered()) {
            starbuncle.addGoalDebug(this, new DebugEvent("BedPowered", "Bed Powered, cannot take"));
            return false;
        }
        return true;
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

        if (fluidHandlerExtract != null) {
            FluidStack toExtract;
            for (int tankIndexE = 0; tankIndexE < fluidHandlerExtract.getTanks(); tankIndexE++) {
                toExtract = fluidHandlerExtract.getFluidInTank(tankIndexE);
                if (toExtract.isEmpty()) continue; // there is no fluid in this tank, check next tank
                BlockPos pos = behavior.getTankForStorage(toExtract);
                if (pos == null) continue; // there is no valid storage tank for this fluid, check next tank
                IFluidHandler fluidHandlerStore = behavior.getHandlerFromCap(pos);

                if (fluidHandlerStore != null) {
                    int maxRoom = -1;
                    for (int s = 0; s < fluidHandlerStore.getTanks(); s++) {
                        maxRoom = fluidHandlerStore.fill(toExtract, IFluidHandler.FluidAction.SIMULATE);
                        if (maxRoom > Configs.STARBUCKET_THRESHOLD.get()) break; // we found a tank with enough room
                    }
                    if (maxRoom <= Configs.STARBUCKET_THRESHOLD.get()) continue; // there is no tank with enough room, check next tank
                    int maxTakeAmount = Math.min(maxRoom, behavior.getRatio());
                    starbuncle.level.playSound(null, targetPos, SoundEvents.BUCKET_FILL, SoundSource.NEUTRAL, 0.2f, 1.3f);
                    FluidStack extracted = new FluidStack(toExtract, maxTakeAmount);
                    behavior.setFluidStack(fluidHandlerExtract.drain(extracted, IFluidHandler.FluidAction.EXECUTE));
                    break;
                } else {
                    starbuncle.addGoalDebug(this, new DebugEvent("NoHandler", "No fluid handler at " + pos));
                }
            }

        } else {
            starbuncle.addGoalDebug(this, new DebugEvent("NoHandler", "No fluid handler at " + targetPos.toString()));
        }

        return true;
    }
}
