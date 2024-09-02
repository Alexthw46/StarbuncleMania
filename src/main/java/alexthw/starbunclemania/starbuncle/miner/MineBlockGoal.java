package alexthw.starbunclemania.starbuncle.miner;

import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.debug.DebugEvent;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.GoToPosGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class MineBlockGoal<T extends StarbyMinerBehavior> extends GoToPosGoal<T> {

    public MineBlockGoal(Starbuncle starbuncle, T behavior) {
        super(starbuncle, behavior, () -> starbuncle.getHeldStack().isEmpty());
    }

    @Override
    public void start() {
        super.start();
        starbuncle.goalState = Starbuncle.StarbuncleGoalState.TAKING_ITEM;
    }

    BlockPos miningPos;
    int destroyTimer;

    @Override
    public BlockPos getDestination() {
        return behavior.getValidMinePos();
    }

    @Override
    public boolean canUse() {
        return super.canUse() && starbuncle.getHeldStack().isEmpty();
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && !starbuncle.level().getBlockState(targetPos).isAir();
    }

    @Override
    public void tick() {
        super.tick();
        if (miningPos != null && destroyTimer != -1) {
            BlockState blockState = starbuncle.level().getBlockState(miningPos);
            if (blockState.isAir()) {
                destroyTimer = -1;
                miningPos = null;
                return;
            }
            destroyTimer++;
            float percentage = destroyTimer / (blockState.getBlock().defaultDestroyTime() * 2);
            if (percentage > 1) {
                destroyTimer = -1;
                starbuncle.level().destroyBlock(miningPos, true, starbuncle);
                miningPos = null;
                return;
            }
            starbuncle.level().destroyBlockProgress(starbuncle.getId(), miningPos, (int) (percentage * 10));
        }
    }

    @Override
    public boolean onDestinationReached() {
        if (destroyTimer > 0) return true;
        BlockState blockState = starbuncle.level().getBlockState(targetPos);
        if (blockState.isAir()) {
            starbuncle.setBackOff(5 + starbuncle.level().random.nextInt(10));
            starbuncle.addGoalDebug(this, new DebugEvent("no_block", "No block at " + targetPos.toString()));
            return true;
        }
        // check if the entity can mine the block
        if (!behavior.canMineBlock(targetPos)) {
            starbuncle.setBackOff(5 + starbuncle.level().random.nextInt(20));
            starbuncle.addGoalDebug(this, new DebugEvent("permission_denied", "Can't mine " + blockState.getBlock() + " at " + targetPos.toString()));
            return true;
        }
        // start mining the block
        miningPos = targetPos;
        if (destroyTimer == -1) destroyTimer = 0;
        starbuncle.addGoalDebug(this, new DebugEvent("start_mine", "Started mining " + blockState.getBlock() + " at " + targetPos.toString()));

        return false;
    }
}
