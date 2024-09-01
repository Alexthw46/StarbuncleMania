package alexthw.starbunclemania.common.block.fluids;

import com.hollingsworth.arsnouveau.common.block.TickableModBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SourceCondenserBlock extends TickableModBlock {

    public SourceCondenserBlock(){
        super(defaultProperties().noOcclusion());
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {

        if (super.useItemOn(stack, state, level, pos, player, hand, hitResult) == ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION) {
            if (level.getBlockEntity(pos) instanceof SourceCondenserTile be && be.interact(player, hand)) {
                return ItemInteractionResult.sidedSuccess(level.isClientSide());
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return new SourceCondenserTile(pPos, pState);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return shape;
    }

    static final VoxelShape shape = Shapes.join(Block.box(3, 1.1, 3, 13, 12.1, 13), Block.box(2, 0, 2, 14, 1, 14), BooleanOp.OR);
}
