package alexthw.starbunclemania.common.block;

import com.hollingsworth.arsnouveau.common.block.ModBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class LiquidJarBlock extends ModBlock implements EntityBlock {

    public LiquidJarBlock() {
        super(defaultProperties().noOcclusion());
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return shape;
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof LiquidJarTile tile){
            if (!tile.tank.isEmpty()){
               return tile.tank.getFluid().getFluid().getFluidType().getLightLevel();
            }
        }
        return super.getLightEmission(state, level, pos);
    }

    public static final VoxelShape shape = Stream.of(
            Block.box(3, 13.1, 3, 13, 15.1, 13),
            Block.box(6, 15.1, 6, 10, 16.1, 10),
            Block.box(3, 1.1, 3, 13, 11.1, 13),
            Block.box(4, 11.1, 4, 12, 13.1, 12),
            Stream.of(
                    Block.box(1.9, 1.4, 1.9, 3.5, 11, 3.5),
                    Block.box(1.9, 1.4, 12.5, 3.5, 11, 14.1),
                    Block.box(1.9, 0.2, 1.9, 14.1, 1.4, 14.1),
                    Block.box(12.5, 1.4, 12.5, 14.1, 11, 14.1),
                    Block.box(12.5, 1.4, 1.9, 14.1, 11, 3.5)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get()
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (super.use(state, level, pos, player, hand, hit) == InteractionResult.PASS) {
            if (level.getBlockEntity(pos) instanceof LiquidJarTile be && be.interact(player, hand)) {
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
        }

        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new LiquidJarTile(pPos, pState);
    }


}
