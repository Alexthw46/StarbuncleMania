package alexthw.starbunclemania.starbuncle.placer;

import alexthw.starbunclemania.StarbuncleMania;
import alexthw.starbunclemania.starbuncle.StarbyItemBehavior;
import alexthw.starbunclemania.starbuncle.TakeItemGoal;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.GoToBedGoal;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.CommonHooks;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class StarbyPlacerBehavior extends StarbyItemBehavior {

    public static final ResourceLocation MINER_ID = ResourceLocation.fromNamespaceAndPath(StarbuncleMania.MODID, "starby_block_placer");

    public StarbyPlacerBehavior(Starbuncle entity, CompoundTag tag) {
        super(entity, tag);
        goals.add(new WrappedGoal(1, new GoToBedGoal(entity, this)));
        goals.add(new WrappedGoal(3, new PlaceBlockGoal<>(entity, this)));
        goals.add(new WrappedGoal(5, new TakeItemGoal<>(entity, this)));
    }

    @Override
    public void getTooltip(Consumer<Component> tooltip) {
        super.getTooltip(tooltip);
        tooltip.accept(Component.translatable("ars_nouveau.starbuncle.placing", TO_LIST.size()));
        tooltip.accept(Component.translatable("ars_nouveau.starbuncle.taking", FROM_LIST.size()));
        if (!itemScroll.isEmpty()) {
            tooltip.accept(Component.translatable("ars_nouveau.filtering_with", itemScroll.getHoverName().getString()));
        }
    }

    @Override
    public void pickUpItem(ItemEntity itemEntity) {
        super.pickUpItem(itemEntity);
        if (getValidStorePos(itemEntity.getItem()) == null)
            return;
        Starbuncle starbuncleWithRoom = starbuncle.getStarbuncleWithSpace();
        starbuncleWithRoom.setHeldStack(itemEntity.getItem());
        itemEntity.remove(Entity.RemovalReason.DISCARDED);
        this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_PICKUP, starbuncle.getSoundSource(), 1.0F, 1.0F);
        for (ItemEntity i : level.getEntitiesOfClass(ItemEntity.class, starbuncle.getBoundingBox().inflate(3))) {
            if (itemEntity.getItem().getCount() >= itemEntity.getItem().getMaxStackSize())
                break;
            int maxTake = starbuncleWithRoom.getHeldStack().getMaxStackSize() - starbuncleWithRoom.getHeldStack().getCount();
            if (ItemStack.isSameItemSameComponents(i.getItem(), starbuncleWithRoom.getHeldStack())) {
                int toTake = Math.min(i.getItem().getCount(), maxTake);
                i.getItem().shrink(toTake);
                starbuncleWithRoom.getHeldStack().grow(toTake);
            }
        }
    }

    @Override
    public void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable Direction side, @Nullable LivingEntity storedEntity, Player
            playerEntity) {
        super.onFinishedConnectionFirst(storedPos, side, storedEntity, playerEntity);
        if (storedPos != null) {
            if (!level.isOutsideBuildHeight(storedPos)) {
                addToPos(storedPos);
                syncTag();
                PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.starbuncle.place"));
            }
        }
    }

    @Override
    public void onFinishedConnectionLast(@Nullable BlockPos storedPos, @Nullable Direction side, @Nullable LivingEntity storedEntity, Player playerEntity) {
        super.onFinishedConnectionLast(storedPos, side, storedEntity, playerEntity);
        if (storedPos != null) {
            if (getItemCapFromTile(storedPos, side) != null) {
                addFromPos(storedPos);
                syncTag();
                PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.starbuncle.take"));
            }
        }
    }

    @Override
    public boolean canGoToBed() {
        return isBedPowered() || (getValidTakePos() == null && (starbuncle.getHeldStack().isEmpty() || getValidPlacePos(starbuncle.getHeldStack()) == null));
    }

    @Override
    public ResourceLocation getRegistryName() {
        return MINER_ID;
    }

    @Override
    public BlockPos getValidStorePos(ItemStack stack) {
        return getValidPlacePos(stack);
    }

    public @Nullable BlockPos getValidPlacePos(ItemStack stack) {
        if (TO_LIST.isEmpty())
            return null;
        if (!(stack.getItem() instanceof BlockItem bi))
            return null;

        for (BlockPos p : TO_LIST) {
            if (isPositionValidPlace(p, bi.getBlock()))
                return p;
        }
        return null;
    }

    public boolean isPositionValidPlace(BlockPos p, Block block) {

        if (p == null || !level.isLoaded(p)) return false;

        return level.getBlockState(p).canBeReplaced() && canPlaceBlock(p) && block.defaultBlockState().canSurvive(level, p);
    }

    public boolean canPlaceBlock(BlockPos targetPos) {
        return CommonHooks.canEntityDestroy(this.starbuncle.level(), targetPos, this.starbuncle);
    }

}

