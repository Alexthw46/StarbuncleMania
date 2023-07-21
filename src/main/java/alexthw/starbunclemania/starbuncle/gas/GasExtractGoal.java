package alexthw.starbunclemania.starbuncle.gas;

import alexthw.starbunclemania.Configs;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.debug.DebugEvent;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.GoToPosGoal;
import mekanism.api.Action;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class GasExtractGoal extends GoToPosGoal<StarbyGasBehavior>{

    public GasExtractGoal(Starbuncle starbuncle, StarbyGasBehavior behavior) {
        super(starbuncle, behavior, () -> behavior.getGasStack().isEmpty());
    }

    @Override
    public void start() {
        super.start();
        starbuncle.goalState = Starbuncle.StarbuncleGoalState.TAKING_ITEM;
    }

    @Override
    public BlockPos getDestination() {
        return behavior.getTankToExtract();
    }

    /**
     * Returns whether we are done and can end the goal.
     */
    @Override
    public boolean onDestinationReached() {

        IGasHandler gasHandlerExtract = behavior.getHandlerFromCap(targetPos);

        int tankIndexE = 0;
        if (gasHandlerExtract != null){
            GasStack toExtract = gasHandlerExtract.getChemicalInTank(tankIndexE);
            BlockPos pos = behavior.getTankForStorage(new GasStack(toExtract, behavior.getRatio()));
            if (pos == null) {
                starbuncle.addGoalDebug(this, new DebugEvent("NoRoom", "No Room for " + toExtract.getTypeRegistryName() + " from " + targetPos.toString()));
                return true;
            }
            IGasHandler gasHandlerStore = behavior.getHandlerFromCap(pos);

            if (gasHandlerStore != null){
                long maxRoom = -1;
                for (int s = 0; s < gasHandlerStore.getTanks(); s++) {
                    if (gasHandlerStore.getChemicalInTank(s).isEmpty()) {
                        maxRoom = gasHandlerStore.getTankCapacity(s);
                        if (maxRoom > 0) break;
                    } else if (gasHandlerStore.getChemicalInTank(s).isTypeEqual(toExtract)) {
                        maxRoom = gasHandlerStore.getTankCapacity(s) - gasHandlerStore.getChemicalInTank(s).getAmount();
                        if (maxRoom > 0) break;
                    }
                }
                if (maxRoom <= Configs.STARBALLOON_THRESHOLD.get()) return true;
                int takeAmount = (int) Math.min(toExtract.getAmount(), Math.min(maxRoom, behavior.getRatio()));
                starbuncle.level().playSound(null, targetPos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.NEUTRAL, 0.2f, 1.3f);
                GasStack extracted = new GasStack(toExtract, takeAmount);
                behavior.setGasStack(gasHandlerExtract.extractChemical(extracted, Action.EXECUTE));
                starbuncle.addGoalDebug(this, new DebugEvent("SetHeld", "Taking " + takeAmount + "x " + extracted.getTypeRegistryName() + " from " + targetPos.toString()));

            }else {
                starbuncle.addGoalDebug(this, new DebugEvent("NoHandler", "No gas handler at " + targetPos.toString()));
            }
        }else {
            starbuncle.addGoalDebug(this, new DebugEvent("NoHandler", "No gas handler at " + targetPos.toString()));
        }
        return true;
    }

}