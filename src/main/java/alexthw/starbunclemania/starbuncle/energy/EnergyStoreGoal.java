package alexthw.starbunclemania.starbuncle.energy;

import alexthw.starbunclemania.Configs;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.debug.DebugEvent;
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
        this.starbuncle.getNavigation().stop();

        IEnergyStorage batteryTile = behavior.getHandlerFromCap(targetPos);


        if (batteryTile == null) {
            starbuncle.addGoalDebug(this, new DebugEvent("NoEnergyHandler", "No energy handler at " + targetPos.toString()));
            return true;
        }

        int room = batteryTile.getMaxEnergyStored() - batteryTile.getEnergyStored();
        if (room <= Configs.STARBATTERY_THRESHOLD.get()) {
            starbuncle.setBackOff(5 + starbuncle.level().random.nextInt(20));
            starbuncle.addGoalDebug(this, new DebugEvent("no_room", targetPos.toString()));
            return true;
        }
        int diff = Math.min(room, behavior.getEnergy());
        int actualTake = batteryTile.receiveEnergy(diff, false);
        behavior.setEnergy(behavior.getEnergy() - actualTake);
        starbuncle.addGoalDebug(this, new DebugEvent("stored_energy", "successful at " + targetPos.toString() + ". Trasferred RFs : " + actualTake));

        return true;
    }

}
