package alexthw.starbunclemania.starbuncle.miner;

import alexthw.starbunclemania.StarbuncleMania;
import alexthw.starbunclemania.starbuncle.AuthorizedBehavior;
import alexthw.starbunclemania.starbuncle.StarbyItemBehavior;
import alexthw.starbunclemania.starbuncle.StoreItemGoal;
import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.item.ICosmeticItem;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.GoToBedGoal;
import com.hollingsworth.arsnouveau.common.items.DominionWand;
import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;


public class StarbyMinerBehavior extends StarbyItemBehavior implements AuthorizedBehavior {

    public static final ResourceLocation MINER_ID = ResourceLocation.fromNamespaceAndPath(StarbuncleMania.MODID, "starby_block_breaker");

    ItemStack toolToUse = ItemStack.EMPTY;
    UUID ownerUUID;

    public StarbyMinerBehavior(Starbuncle entity, CompoundTag tag) {
        super(entity, tag);
        if (tag.contains("givenTool")) {
            toolToUse = ItemStack.parseOptional(entity.registryAccess(), tag.getCompound("givenTool"));
        }
        if (tag.contains("ownerUUID")) {
            ownerUUID = tag.getUUID("ownerUUID");
        }
        goals.add(new WrappedGoal(1, new GoToBedGoal(entity, this)));
        goals.add(new WrappedGoal(3, new MineBlockGoal<>(entity, this)));
        goals.add(new WrappedGoal(5, new StoreItemGoal<>(entity, this)));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND) return super.mobInteract(player, hand);
        ItemStack playerHand = player.getItemInHand(hand);
        if (playerHand.isEmpty() || playerHand.getItem() instanceof BlockItem || playerHand.getItem() instanceof ICosmeticItem || playerHand.getItem() instanceof DominionWand || playerHand.getItem() instanceof ItemScroll)
            return super.mobInteract(player, hand);
        if (!toolToUse.isEmpty()) {
            // drop the item in world
            starbuncle.level().addFreshEntity(new ItemEntity(starbuncle.level(), starbuncle.getX(), starbuncle.getY(), starbuncle.getZ(), toolToUse.copy()));
        }
        toolToUse = playerHand.split(1);
        syncTag();
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean clearOrRemove() {
        boolean no_connections = super.clearOrRemove();
        // drop the tool given before removing the behavior
        if (no_connections && !toolToUse.isEmpty()) {
            starbuncle.level().addFreshEntity(new ItemEntity(starbuncle.level(), starbuncle.getX(), starbuncle.getY(), starbuncle.getZ(), toolToUse.copy()));
            toolToUse = ItemStack.EMPTY;
            return false;
        }
        return no_connections;
    }

    @Override
    public void setOwnerUUID(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    @Override
    public @NotNull UUID getOwnerUUID() {
        return ownerUUID == null ? ANFakePlayer.getPlayer((ServerLevel) starbuncle.level()).getUUID() : ownerUUID;
    }

    public ItemStack getToolToUse() {
        return toolToUse.isEmpty() ? Items.IRON_PICKAXE.getDefaultInstance() : toolToUse;
    }

    @Override
    public void getTooltip(Consumer<Component> tooltip) {
        super.getTooltip(tooltip);
        tooltip.accept(Component.translatable("ars_nouveau.starbuncle.storing", TO_LIST.size()));
        tooltip.accept(Component.translatable("ars_nouveau.starbuncle.mining", FROM_LIST.size()));
        if (!itemScroll.isEmpty()) {
            tooltip.accept(Component.translatable("ars_nouveau.filtering_with", itemScroll.getHoverName().getString()));
        }
        tooltip.accept(Component.translatable("ars_nouveau.starbuncle.tool", getToolToUse().getHoverName().getString()));
    }

    public boolean isPickupDisabled() {
        return starbuncle.getCosmeticItem().getItem() == ItemsRegistry.STARBUNCLE_SHADES.get();
    }

    @Override
    public void pickUpItem(ItemEntity itemEntity) {
        super.pickUpItem(itemEntity);
        if (getValidStorePos(itemEntity.getItem()) == null || isPickupDisabled())
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
    public void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable Direction side, @Nullable LivingEntity storedEntity, Player playerEntity) {
        super.onFinishedConnectionFirst(storedPos, side, storedEntity, playerEntity);
        if (storedPos != null) {
            if (getItemCapFromTile(storedPos, side) != null) {
                addToPos(storedPos);
                syncTag();
                PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.starbuncle.store"));
            }
        }
    }

    @Override
    public void onFinishedConnectionLast(@Nullable BlockPos storedPos, @Nullable Direction side, @Nullable LivingEntity storedEntity, Player playerEntity) {
        super.onFinishedConnectionLast(storedPos, side, storedEntity, playerEntity);
        if (storedPos != null) {
            if (!level.isOutsideBuildHeight(storedPos)) {
                addFromPos(storedPos);
                syncTag();
                PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.starbuncle.miner"));
            }
        }
    }

    @Override
    public boolean canGoToBed() {
        return isBedPowered() || (getValidMinePos() == null && (starbuncle.getHeldStack().isEmpty() || getValidStorePos(starbuncle.getHeldStack()) == null));
    }

    @Override
    public ResourceLocation getRegistryName() {
        return MINER_ID;
    }


    public @Nullable BlockPos getValidMinePos() {
        if (FROM_LIST.isEmpty())
            return null;

        for (BlockPos p : FROM_LIST) {
            if (isPositionValidMine(p))
                return p;
        }
        return null;
    }

    public boolean isPositionValidMine(BlockPos p) {

        if (p == null || !level.isLoaded(p)) return false;

        BlockState blockState = level.getBlockState(p);

        if (blockState.isAir()) return false;

        if (getToolToUse().getDestroySpeed(blockState) <= 0 || blockState.getDestroySpeed(level, p) <= 0) return false;

        // the tool is not right
        if (blockState.requiresCorrectToolForDrops() && !getToolToUse().isCorrectToolForDrops(blockState)) {
            return false;
        }

        // get the drops of the block
        List<ItemStack> drops = blockState.getDrops(
                new LootParams.Builder((ServerLevel) level)
                        .withParameter(LootContextParams.ORIGIN, starbuncle.position())
                        .withParameter(LootContextParams.TOOL, getToolToUse())
                        .withParameter(LootContextParams.ENCHANTMENT_ACTIVE, true)
        );

        for (ItemStack stack : drops) {
            if (!stack.isEmpty() && getValidStorePos(stack) == null) {
                return false;
            }
        }
        return true;

    }

    public boolean canMineBlock(BlockPos targetPos) {
        Entity playerEntity = ownerUUID != null && starbuncle.level() instanceof ServerLevel serverLevel ? FakePlayerFactory.get(serverLevel, new GameProfile(getOwnerUUID(), "")) : starbuncle;
        return BlockUtil.destroyRespectsClaim(playerEntity, starbuncle.level(), targetPos);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        if (!toolToUse.isEmpty())
            tag.put("givenTool", toolToUse.save(starbuncle.registryAccess()));
        if (ownerUUID != null) {
            tag.putUUID("ownerUUID", ownerUUID);
        }
        return super.toTag(tag);
    }

    @Override
    public ItemStack getStackForRender() {
        if (!toolToUse.isEmpty()) return toolToUse;
        return super.getStackForRender();
    }
}
