package alexthw.starbunclemania.starbuncle.gas;

import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.debug.DebugEvent;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.GoToPosGoal;
import mekanism.api.Action;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class GasStoreGoal extends GoToPosGoal<StarbyGasBehavior> {

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
        this.starbuncle.getNavigation().stop();

        IGasHandler gasHandler = behavior.getHandlerFromCap(targetPos);
        int tankIndex = 0;
        if (gasHandler != null) {
            int room = (int) (gasHandler.getTankCapacity(tankIndex) - gasHandler.getChemicalInTank(tankIndex).getAmount());
            if (room <= 0) {
                starbuncle.setBackOff(5 + starbuncle.level().random.nextInt(20));
                starbuncle.addGoalDebug(this, new DebugEvent("no_room", targetPos.toString()));
                return true;
            }
            int diff = (int) Math.min(room, behavior.getGasStack().getAmount());
            GasStack fill = new GasStack(behavior.getGasStack(), diff);
            behavior.setGasStack(gasHandler.insertChemical(fill, Action.EXECUTE));
            starbuncle.level().playSound(null, targetPos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.NEUTRAL, 0.2f, 1.3f);
            starbuncle.addGoalDebug(this, new DebugEvent("stored_gas", "successful at " + targetPos.toString() + "set gas stack to " + diff + "x " + fill.getTypeRegistryName()));
        } else {
            starbuncle.addGoalDebug(this, new DebugEvent("NoHandler", "No gas handler at " + targetPos.toString()));
        }
        return true;
    }

}
