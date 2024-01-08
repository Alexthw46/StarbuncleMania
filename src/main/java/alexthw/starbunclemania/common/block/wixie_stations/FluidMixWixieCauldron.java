package alexthw.starbunclemania.common.block.wixie_stations;

import com.hollingsworth.arsnouveau.common.block.WixieCauldron;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class FluidMixWixieCauldron extends WixieCauldron {


    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FluidMixWixieCauldronTile(pos, state);
    }


}
