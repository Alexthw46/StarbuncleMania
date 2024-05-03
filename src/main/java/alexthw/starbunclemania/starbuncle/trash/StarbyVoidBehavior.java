package alexthw.starbunclemania.starbuncle.trash;

import alexthw.starbunclemania.StarbuncleMania;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.StarbyListBehavior;
import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import com.hollingsworth.arsnouveau.common.items.itemscrolls.MimicItemScroll;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StarbyVoidBehavior extends StarbyListBehavior {

    public ItemStack itemScroll;

    public StarbyVoidBehavior(Starbuncle entity, CompoundTag tag) {
        super(entity, tag);
        if (tag.contains("itemScroll")) {
            this.itemScroll = ItemStack.of(tag.getCompound("itemScroll"));
        }
        goals.add(new WrappedGoal(3, new SnatchItem(starbuncle, this)));
        goals.add(new WrappedGoal(3, new VoidFromStorageGoal(starbuncle, this)));

    }

    public @Nullable IItemHandler getItemCapFromTile(BlockEntity blockEntity, @Nullable Direction face) {
        if (blockEntity != null && blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, face).isPresent()) {
            var lazy = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, face).resolve();
            if (lazy.isPresent())
                return lazy.get();
        }
        return null;
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        Item var5 = stack.getItem();
        if (var5 instanceof ItemScroll scroll) {
            this.itemScroll = stack.copy();
            PortUtil.sendMessage(player, Component.translatable("ars_nouveau.filter_set"));
            this.syncTag();
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public void onWanded(Player playerEntity) {
        this.itemScroll = ItemStack.EMPTY;
        super.onWanded(playerEntity);
    }

    public void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable Direction face, @Nullable LivingEntity storedEntity, Player playerEntity) {
        super.onFinishedConnectionFirst(storedPos, face, storedEntity, playerEntity);
    }

    @Override
    public void onFinishedConnectionLast(@Nullable BlockPos storedPos, @Nullable Direction side, @Nullable LivingEntity storedEntity, Player playerEntity) {
        super.onFinishedConnectionLast(storedPos, storedEntity, playerEntity);
        if (storedPos == null)
            return;

        BlockEntity blockEntity = level.getBlockEntity(storedPos);
        if (blockEntity != null && blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, side).isPresent()) {
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.starbuncle.take"));
            addFromPos(storedPos, side);
        }
    }

    @Override
    public void pickUpItem(ItemEntity itemEntity) {
        super.pickUpItem(itemEntity);
        starbuncle.setHeldStack(ItemStack.EMPTY);
        itemEntity.remove(Entity.RemovalReason.DISCARDED);
        this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_PICKUP, starbuncle.getSoundSource(), 1.0F, 1.0F);

    }

    public BlockPos getValidStorePos(ItemStack stack) {
        if (itemScroll != null && itemScroll.getItem() instanceof ItemScroll filter && !(filter instanceof MimicItemScroll)) {
            if (filter.getSortPref(stack, itemScroll, null) != ItemScroll.SortPref.INVALID) {
                return starbuncle.getOnPos();
            }
            return null;
        }
        return starbuncle.getOnPos();
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
        IItemHandler iItemHandler = getItemCapFromTile(level.getBlockEntity(p), face);

        if (iItemHandler == null) return false;
        for (int j = 0; j < iItemHandler.getSlots(); j++) {
            ItemStack stack = iItemHandler.getStackInSlot(j);
            if (!stack.isEmpty() && getValidStorePos(stack) != null) {
                return true;
            }
        }
        return false;
    }

    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        if (this.itemScroll != null) {
            tag.put("itemScroll", this.itemScroll.serializeNBT());
        }

        return tag;
    }

    public void getTooltip(List<Component> tooltip) {
        super.getTooltip(tooltip);
        tooltip.add(Component.translatable("ars_nouveau.starbuncle.trashing_items", FROM_LIST.size()));
        if (this.itemScroll != null && !this.itemScroll.isEmpty()) {
            tooltip.add(Component.translatable("ars_nouveau.filtering_with", this.itemScroll.getHoverName().getString()));
        }

    }

    @Override
    protected ResourceLocation getRegistryName() {
        return TRANSPORT_ID;
    }

    public static final ResourceLocation TRANSPORT_ID = new ResourceLocation(StarbuncleMania.MODID, "starby_item_void");

}
