package alexthw.starbunclemania.starbuncle.energy;

import alexthw.starbunclemania.Configs;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
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
            starbuncle.level.playSound(null, targetPos, SoundEvents.BUCKET_FILL, SoundSource.NEUTRAL, 0.2f, 1.3f);
            int actualTake = take.extractEnergy(takeAmount, false);
            behavior.setEnergy(actualTake);
        }
        return true;
    }

}
