package alexthw.starbunclemania.common.block.wixie_stations;

import com.hollingsworth.arsnouveau.common.block.WixieCauldron;
import com.hollingsworth.arsnouveau.common.block.tile.WixieCauldronTile;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import vectorwing.farmersdelight.common.block.CookingPotBlock;
import vectorwing.farmersdelight.common.block.state.CookingPotSupport;
import vectorwing.farmersdelight.common.registry.ModSounds;

public class FarmerPotWixieCauldron extends WixieCauldron {

    protected static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 10.0, 14.0);
    protected static final VoxelShape SHAPE_WITH_TRAY = Shapes.or(SHAPE, Block.box(0.0, -1.0, 0.0, 16.0, 0.0, 16.0));

    @Override
    public WixieCauldronTile newBlockEntity(BlockPos pos, BlockState state) {
        return new FarmerPotWixieCauldronTile(pos, state);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return state.getValue(CookingPotBlock.SUPPORT).equals(CookingPotSupport.TRAY) ? SHAPE_WITH_TRAY : SHAPE;
    }

    @OnlyIn(Dist.CLIENT)
    public void animateTick(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        BlockEntity tileEntity = level.getBlockEntity(pos);
        if (tileEntity instanceof WixieCauldronTile cookingPotEntity) {
            if (!cookingPotEntity.isCraftingDone()) {
                SoundEvent boilSound = ModSounds.BLOCK_COOKING_POT_BOIL.get();
                double x = (double) pos.getX() + 0.5;
                double y = pos.getY();
                double z = (double) pos.getZ() + 0.5;
                if (random.nextInt(10) == 0) {
                    level.playLocalSound(x, y, z, boilSound, SoundSource.BLOCKS, 0.5F, random.nextFloat() * 0.2F + 0.9F, false);
                }
            }
        }

    }

    @Override
    public @NotNull BlockState mirror(BlockState pState, Mirror pMirror) {
        pState.setValue(CookingPotBlock.FACING, pMirror.mirror(pState.getValue(CookingPotBlock.FACING)));
        return pState;
    }

    @Override
    public @NotNull BlockState rotate(BlockState pState, Rotation pRot) {
        return pState.setValue(CookingPotBlock.FACING, pRot.rotate(pState.getValue(CookingPotBlock.FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CookingPotBlock.FACING, CookingPotBlock.SUPPORT);
    }
}
