package alexthw.starbunclemania.starbuncle.energy;

import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.GoToPosGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public class EnergyStoreGoal extends GoToPosGoal<StarbyEnergyBehavior> {

    public EnergyStoreGoal(Starbuncle starbuncle, StarbyEnergyBehavior behavior) {
        super(starbuncle, behavior, () -> behavior.getEnergy() > 0);
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
            int diff = Math.min(room, behavior.getEnergy());
            int actualTake = batteryTile.receiveEnergy(diff, false);
            behavior.setEnergy(behavior.getEnergy() - actualTake);
            starbuncle.level.playSound(null, targetPos, SoundEvents.BUCKET_EMPTY, SoundSource.NEUTRAL, 0.5f, 1.3f);
        }
        return true;
    }

}
