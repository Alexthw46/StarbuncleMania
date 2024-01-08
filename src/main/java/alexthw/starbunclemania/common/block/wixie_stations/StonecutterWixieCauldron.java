package alexthw.starbunclemania.common.block.wixie_stations;

import com.hollingsworth.arsnouveau.common.block.WixieCauldron;
import com.hollingsworth.arsnouveau.common.block.tile.WixieCauldronTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StonecutterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class StonecutterWixieCauldron extends WixieCauldron {

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(StonecutterBlock.FACING);
    }

    @Override
    public WixieCauldronTile newBlockEntity(BlockPos pos, BlockState state) {
        return new StonecutterWixieCauldronTile(pos, state);
    }
}
