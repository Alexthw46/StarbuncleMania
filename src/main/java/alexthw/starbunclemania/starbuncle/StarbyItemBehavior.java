package alexthw.starbunclemania.starbuncle;

import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.StarbyListBehavior;
import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

public class StarbyItemBehavior extends StarbyListBehavior {

    public ItemStack itemScroll = ItemStack.EMPTY;

    public StarbyItemBehavior(Starbuncle entity, CompoundTag tag) {
        super(entity, tag);
        this.itemScroll = ItemStack.parseOptional(entity.level().registryAccess(), tag.getCompound("itemScroll"));
    }

    public @Nullable IItemHandler getItemCapFromTile(BlockPos pos, @Nullable Direction face) {
        return starbuncle.level().getCapability(Capabilities.ItemHandler.BLOCK, pos, face);
    }

    public ItemScroll.SortPref canDepositItem(BlockPos pos, ItemStack stack) {
        ItemScroll.SortPref pref = ItemScroll.SortPref.LOW;
        if (pos == null || stack == null || stack.isEmpty())
            return ItemScroll.SortPref.INVALID;

        IItemHandler handler = getItemCapFromTile(pos, TO_DIRECTION_MAP.get(pos.hashCode()));
        if (handler == null)
            return ItemScroll.SortPref.INVALID;
        for (ItemFrame i : level.getEntitiesOfClass(ItemFrame.class, new AABB(pos).inflate(1))) {
            // Check if these frames are attached to the tile
            BlockEntity adjTile = level.getBlockEntity(i.blockPosition().relative(i.getDirection().getOpposite()));
            if (adjTile == null || !adjTile.equals(level.getBlockEntity(pos)) || i.getItem().isEmpty())
                continue;


            ItemStack stackInFrame = i.getItem();

            if (stackInFrame.getItem() instanceof ItemScroll scrollItem) {
                pref = scrollItem.getSortPref(stack, stackInFrame, handler);
                // If our item frame just contains a normal item
            } else if (i.getItem().getItem() != stack.getItem()) {
                return ItemScroll.SortPref.INVALID;
            } else if (i.getItem().getItem() == stack.getItem()) {
                pref = ItemScroll.SortPref.HIGHEST;
            }
        }
        if (itemScroll != null && itemScroll.getItem() instanceof ItemScroll scrollItem && scrollItem.getSortPref(stack, itemScroll,
                handler) == ItemScroll.SortPref.INVALID) {
            return ItemScroll.SortPref.INVALID;
        }
        return !ItemStack.matches(ItemHandlerHelper.insertItemStacked(handler, stack.copy(), true), stack) ? pref : ItemScroll.SortPref.INVALID;
    }

    public ItemScroll.SortPref sortPrefForStack(@Nullable BlockPos b, ItemStack stack) {
        if (stack == null || stack.isEmpty() || b == null || !level.isLoaded(b))
            return ItemScroll.SortPref.INVALID;
        return canDepositItem(b, stack);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof ItemScroll scroll) {
            this.itemScroll = stack.copy();
            PortUtil.sendMessage(player, Component.translatable("ars_nouveau.filter_set"));
            syncTag();
        }

        return super.mobInteract(player, hand);
    }

    public BlockPos getValidStorePos(ItemStack stack) {
        if (TO_LIST.isEmpty() || stack.isEmpty())
            return null;
        BlockPos returnPos = null;
        ItemScroll.SortPref foundPref = ItemScroll.SortPref.INVALID;

        for (BlockPos b : TO_LIST) {
            ItemScroll.SortPref pref = sortPrefForStack(b, stack);
            // Pick our highest priority
            if (pref.ordinal() > foundPref.ordinal()) {
                foundPref = pref;
                returnPos = b;
                if (foundPref == ItemScroll.SortPref.HIGHEST) {
                    return returnPos;
                }
            }
        }
        return returnPos;
    }

    public @Nullable BlockPos getValidTakePos() {
        if (FROM_LIST.isEmpty())
            return null;

        for (BlockPos p : FROM_LIST) {
            if (isPositionValidTake(p))
                return p;
        }
        return null;
    }

    public boolean isPositionValidTake(BlockPos p) {

        if (p == null || !level.isLoaded(p)) return false;
        Direction face = FROM_DIRECTION_MAP.get(p.hashCode());
        IItemHandler iItemHandler = getItemCapFromTile(p, face);

        if (iItemHandler == null) return false;
        for (int j = 0; j < iItemHandler.getSlots(); j++) {
            ItemStack stack = iItemHandler.getStackInSlot(j);
            if (!stack.isEmpty() && getValidStorePos(stack) != null) {
                return true;
            }
        }
        return false;
    }

    public int getMaxTake(ItemStack stack) {
        return stack.getMaxStackSize();
    }

}
