package alexthw.starbunclemania.starbuncle.energy;

import alexthw.starbunclemania.Configs;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.debug.DebugEvent;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.GoToPosGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;


public class EnergyExtractGoal extends GoToPosGoal<StarbyEnergyBehavior> {

    public EnergyExtractGoal(Starbuncle entity, StarbyEnergyBehavior energyBehavior) {
        super(entity, energyBehavior, () -> energyBehavior.getEnergy() <= Configs.STARBATTERY_THRESHOLD.get());
    }

    @Override
    public boolean canUse() {
        boolean superCan = super.canUse();
        if(!superCan || behavior.FROM_LIST.isEmpty())
            return false;
        if(getDestination() == null){
            starbuncle.addGoalDebug(this, new DebugEvent("NoTakeDestination", "No valid take destination"));
            starbuncle.setBackOff(5 + starbuncle.level.random.nextInt(20));
            return false;
        }
        if(behavior.isBedPowered()){
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
        return behavior.getBatteryForTake();
    }

    /**
     * Returns whether we are done and can end the goal.
     */
    @Override
    public boolean onDestinationReached() {
        BlockPos pos = behavior.getBatteryForStore();
        if (pos == null) return true;

        IEnergyStorage take = behavior.getHandlerFromCap(targetPos);
        IEnergyStorage storage = behavior.getHandlerFromCap(pos);

        if (take != null && storage != null) {
            int takeAmount = behavior.getRatio();
            starbuncle.level.playSound(null, targetPos, SoundEvents.BEE_LOOP, SoundSource.NEUTRAL, 0.15f, 2.0f);
            int actualTake = take.extractEnergy(takeAmount, false);
            behavior.setEnergy(behavior.getEnergy() + actualTake);
        }
        return true;
    }

}
