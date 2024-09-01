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
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class StarbyVoidBehavior extends StarbyListBehavior {

    public ItemStack itemScroll = ItemStack.EMPTY;

    public StarbyVoidBehavior(Starbuncle entity, CompoundTag tag) {
        super(entity, tag);
        if (tag.contains("itemScroll")) {
            this.itemScroll = ItemStack.parseOptional(level.registryAccess(), tag.getCompound("itemScroll"));
        }
        goals.add(new WrappedGoal(3, new SnatchItem(starbuncle, this)));
        goals.add(new WrappedGoal(3, new VoidFromStorageGoal(starbuncle, this)));

    }

    public @Nullable IItemHandler getItemCapFromTile(@NotNull BlockPos pos, @Nullable Direction face) {
        return starbuncle.level().getCapability(Capabilities.ItemHandler.BLOCK, pos, face);
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

        if (starbuncle.level().getCapability(Capabilities.ItemHandler.BLOCK, storedPos, side) != null) {
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

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        if (!itemScroll.isEmpty()) {
            tag.put("itemScroll", itemScroll.save(level.registryAccess()));
        }
        return tag;
    }


    @Override
    public void getTooltip(Consumer<Component> tooltip) {
        super.getTooltip(tooltip);
        tooltip.accept(Component.translatable("ars_nouveau.starbuncle.trashing_items", FROM_LIST.size()));
        if (this.itemScroll != null && !this.itemScroll.isEmpty()) {
            tooltip.accept(Component.translatable("ars_nouveau.filtering_with", this.itemScroll.getHoverName().getString()));
        }

    }

    @Override
    public ResourceLocation getRegistryName() {
        return TRANSPORT_ID;
    }

    public static final ResourceLocation TRANSPORT_ID = ResourceLocation.fromNamespaceAndPath(StarbuncleMania.MODID, "starby_item_void");

}
