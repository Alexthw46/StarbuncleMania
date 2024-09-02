package alexthw.starbunclemania.starbuncle.placer;

import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.debug.DebugEvent;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.GoToPosGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class PlaceBlockGoal<T extends StarbyPlacerBehavior> extends GoToPosGoal<T> {

    public PlaceBlockGoal(Starbuncle starbuncle, T behavior) {
        super(starbuncle, behavior, () -> !starbuncle.getHeldStack().isEmpty());
    }

    @Override
    public void start() {
        super.start();
        starbuncle.goalState = Starbuncle.StarbuncleGoalState.STORING_ITEM;
    }

    @Nullable
    @Override
    public BlockPos getDestination() {
        return behavior.getValidStorePos(starbuncle.getHeldStack());
    }

    @Override
    public boolean onDestinationReached() {
        if (starbuncle.getHeldStack().isEmpty() || !(starbuncle.getHeldStack().getItem() instanceof BlockItem blockItem)) {
            return true;
        }
        if (behavior.isBedPowered()) {
            starbuncle.addGoalDebug(this, new DebugEvent("BedPowered", "Bed Powered, cannot place items"));
            return true;
        }
        BlockState toPlaceState = blockItem.getBlock().defaultBlockState();
        BlockState toReplaceState = starbuncle.level().getBlockState(targetPos);
        if (!(toReplaceState.canBeReplaced() && toPlaceState.canSurvive(starbuncle.level(), targetPos))) {
            starbuncle.addGoalDebug(this, new DebugEvent("CannotPlaceBlock", "Cannot place block at " + targetPos.toString()));
            starbuncle.setBackOff(5 + starbuncle.level().random.nextInt(20));
            return true;
        }

        starbuncle.addGoalDebug(this, new DebugEvent("Placing block", "Placing block " + blockItem + "at " + targetPos.toString()));

        // Place the block
        if (starbuncle.level().setBlockAndUpdate(targetPos, toPlaceState)) {
            starbuncle.getHeldStack().shrink(1);
            return true;
        }

        return false;
    }
}
