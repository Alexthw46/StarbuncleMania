package alexthw.starbunclemania.common.block.fluids;

import com.hollingsworth.arsnouveau.common.block.TickableModBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

@SuppressWarnings("deprecation")
public class LiquidJarBlock extends TickableModBlock {

    public LiquidJarBlock() {
        super(defaultProperties().noOcclusion());
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return shape;
    }

    @Override
    public int getLightEmission(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        if (level instanceof Level world && world.getCapability(Capabilities.FluidHandler.BLOCK, pos, null) instanceof FluidTank tank){
            if (!tank.isEmpty()){
               return tank.getFluid().getFluid().getFluidType().getLightLevel();
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


    @Override
    protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        if (super.useItemOn(stack, state, level, pos, player, hand, hitResult) == ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION) {
            if (level.getBlockEntity(pos) instanceof LiquidJarTile be && be.interact(player, hand)) {
                return ItemInteractionResult.sidedSuccess(level.isClientSide());
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }


    @Override
    public boolean hasAnalogOutputSignal(@NotNull BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(@NotNull BlockState blockState, Level worldIn, @NotNull BlockPos pos) {
        LiquidJarTile tile = (LiquidJarTile) worldIn.getBlockEntity(pos);
        if (tile == null || tile.getFluidPercentage() <= 0) return 0;
        return (int) (tile.getFluidPercentage() * 15);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return new LiquidJarTile(pPos, pState);
    }


}
