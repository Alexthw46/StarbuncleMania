package alexthw.starbunclemania.starbuncle.energy;

import alexthw.starbunclemania.Configs;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.GoToPosGoal;
import net.minecraft.core.BlockPos;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public class EnergyStoreGoal extends GoToPosGoal<StarbyEnergyBehavior> {

    public EnergyStoreGoal(Starbuncle starbuncle, StarbyEnergyBehavior behavior) {
        super(starbuncle, behavior, () -> behavior.getEnergy() > 0);
    }

    @Override
    public void start() {
        super.start();
        starbuncle.goalState = Starbuncle.StarbuncleGoalState.STORING_ITEM;
    }

    @Nullable
    @Override
    public BlockPos getDestination() {
        return behavior.getBatteryForStore();
    }

    /**
     * Returns whether we are done and can end the goal.
     */
    @Override
    public boolean onDestinationReached() {
        IEnergyStorage batteryTile = behavior.getHandlerFromCap(targetPos);
        if (batteryTile != null){
            int room = batteryTile.getMaxEnergyStored() - batteryTile.getEnergyStored();
            if (room <= Configs.STARBATTERY_THRESHOLD.get()) return true;
            int diff = Math.min(room, behavior.getEnergy());
            int actualTake = batteryTile.receiveEnergy(diff, false);
            behavior.setEnergy(behavior.getEnergy() - actualTake);
        }
        return true;
    }

}
