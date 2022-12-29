package alexthw.starbunclemania.starbuncle;

import alexthw.starbunclemania.common.item.DirectionScroll;
import alexthw.starbunclemania.common.item.FluidScroll;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fluids.FluidStack;

public class StarHelper {

    public static int checkItemFramesForSide(BlockPos pos, Level level, int sideOrdinal, BlockEntity be) {
        for (ItemFrame i : level.getEntitiesOfClass(ItemFrame.class, new AABB(pos).inflate(1))) {
            // Check if these frames are attached to the tile
            BlockEntity adjTile = level.getBlockEntity(i.blockPosition().relative(i.getDirection().getOpposite()));
            if (adjTile == null || !adjTile.equals(be) || i.getItem().isEmpty()) continue;

            ItemStack stackInFrame = i.getItem();
            if (stackInFrame.getItem() instanceof DirectionScroll && stackInFrame.hasTag()){
                sideOrdinal = stackInFrame.getOrCreateTag().getInt("side");
                break;
            }
        }
        return sideOrdinal;
    }

    public static boolean checkItemFramesForFluid(BlockPos pos, Level level, boolean scrollCheck, FluidStack fluid) {
        BlockEntity be = level.getBlockEntity(pos);

        for (ItemFrame i : level.getEntitiesOfClass(ItemFrame.class, new AABB(pos).inflate(1))) {
            // Check if these frames are attached to the tile
            BlockEntity adjTile = level.getBlockEntity(i.blockPosition().relative(i.getDirection().getOpposite()));
            if (adjTile == null || !adjTile.equals(be) || i.getItem().isEmpty()) continue;

            ItemStack stackInFrame = i.getItem();
            if (stackInFrame.getItem() instanceof FluidScroll scroll && stackInFrame.hasTag()){
                scrollCheck = scrollCheck || scroll.isDenied(stackInFrame, fluid);
                break;
            }
        }
        return scrollCheck;
    }
}
