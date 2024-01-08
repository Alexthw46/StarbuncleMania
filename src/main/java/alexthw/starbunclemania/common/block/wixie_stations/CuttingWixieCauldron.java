package alexthw.starbunclemania.common.block.wixie_stations;

import com.hollingsworth.arsnouveau.common.block.WixieCauldron;
import com.hollingsworth.arsnouveau.common.block.tile.WixieCauldronTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import vectorwing.farmersdelight.common.block.CuttingBoardBlock;

public class CuttingWixieCauldron extends WixieCauldron {


    protected static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 1.0, 15.0);


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CuttingBoardBlock.FACING);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @NotNull BlockState mirror(BlockState pState, Mirror pMirror) {
        pState.setValue(CuttingBoardBlock.FACING, pMirror.mirror(pState.getValue(CuttingBoardBlock.FACING)));
        return pState;
    }

    @Override
    public @NotNull BlockState rotate(BlockState pState, Rotation pRot) {
        return pState.setValue(CuttingBoardBlock.FACING, pRot.rotate(pState.getValue(CuttingBoardBlock.FACING)));
    }

    @Override
    public WixieCauldronTile newBlockEntity(BlockPos pos, BlockState state) {
        return new CuttingWixieCauldronTile(pos, state);
    }
}
