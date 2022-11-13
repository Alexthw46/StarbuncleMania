package alexthw.starbunclemania.starbuncle.gas;

import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.GoToPosGoal;
import mekanism.api.Action;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class GasStoreGoal extends GoToPosGoal<StarbyGasBehavior>{

    public GasStoreGoal(Starbuncle starbuncle, StarbyGasBehavior behavior) {
        super(starbuncle, behavior, () -> !behavior.getGasStack().isEmpty());
    }

    @Override
    public void start() {
        super.start();
        starbuncle.goalState = Starbuncle.StarbuncleGoalState.STORING_ITEM;
    }

    @Override
    public BlockPos getDestination() {
        return behavior.getTankForStorage();
    }

    /**
     * Returns whether we are done and can end the goal.
     */
    @Override
    public boolean onDestinationReached() {
        IGasHandler gasHandler = behavior.getHandlerFromCap(targetPos);
        int tankIndex = 0;
        if (gasHandler != null) {
            int room = (int) (gasHandler.getTankCapacity(tankIndex) - gasHandler.getChemicalInTank(tankIndex).getAmount());
            int diff = (int) Math.min(room, behavior.getGasStack().getAmount());
            GasStack fill = new GasStack(behavior.getGasStack(), diff);
            if (gasHandler.insertChemical(fill, Action.EXECUTE).isEmpty()){
                behavior.getGasStack().shrink(diff);
                behavior.syncTag();
                starbuncle.level.playSound(null, targetPos, SoundEvents.BUCKET_EMPTY, SoundSource.NEUTRAL, 0.5f, 1.3f);
            }
        }
        return false;
    }

}
