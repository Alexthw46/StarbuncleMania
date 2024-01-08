package alexthw.starbunclemania.common.block.wixie_stations;

import com.hollingsworth.arsnouveau.common.block.WixieCauldron;
import com.hollingsworth.arsnouveau.common.block.tile.WixieCauldronTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class SmeltingWixieCauldron extends WixieCauldron {

    public SmeltingWixieCauldron() {
        super();
    }


    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FurnaceBlock.FACING);
    }

    @Override
    public WixieCauldronTile newBlockEntity(BlockPos pos, BlockState state) {
        return new SmeltingWixieCauldronTile(pos, state);
    }
}
