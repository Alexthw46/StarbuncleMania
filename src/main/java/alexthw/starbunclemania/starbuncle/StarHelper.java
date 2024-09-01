package alexthw.starbunclemania.starbuncle;

import alexthw.starbunclemania.common.item.DirectionScroll;
import alexthw.starbunclemania.common.item.FluidScroll;
import alexthw.starbunclemania.registry.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.FluidStack;

public class StarHelper {

    public static Direction checkItemFramesForSide(BlockPos pos, Level level, Direction side) {
        for (ItemFrame i : level.getEntitiesOfClass(ItemFrame.class, new AABB(pos).inflate(1))) {
            // Check if these frames are attached to the tile
            BlockEntity adjTile = level.getBlockEntity(i.blockPosition().relative(i.getDirection().getOpposite()));
            BlockEntity be = level.getBlockEntity(pos);
            if (adjTile == null || !adjTile.equals(be) || i.getItem().isEmpty()) continue;

            ItemStack stackInFrame = i.getItem();
            if (stackInFrame.getItem() instanceof DirectionScroll && !stackInFrame.isComponentsPatchEmpty()){
                side = stackInFrame.get(ModRegistry.DIRECTION).direction();
                break;
            }
        }
        return side;
    }

    public static boolean checkItemFramesForFluid(BlockPos pos, Level level, boolean scrollCheck, FluidStack fluid) {
        BlockEntity be = level.getBlockEntity(pos);

        for (ItemFrame i : level.getEntitiesOfClass(ItemFrame.class, new AABB(pos).inflate(1))) {
            // Check if these frames are attached to the tile
            BlockEntity adjTile = level.getBlockEntity(i.blockPosition().relative(i.getDirection().getOpposite()));
            if (adjTile == null || !adjTile.equals(be) || i.getItem().isEmpty()) continue;

            ItemStack stackInFrame = i.getItem();
            if (stackInFrame.getItem() instanceof FluidScroll scroll && stackInFrame.has(ModRegistry.FLUID_SCROLL)){
                scrollCheck = scrollCheck || scroll.isDenied(stackInFrame, fluid);
                break;
            }
        }
        return scrollCheck;
    }
}
