package alexthw.starbunclemania.starbuncle.item;

import alexthw.starbunclemania.StarbuncleMania;
import alexthw.starbunclemania.common.item.DirectionScroll;
import alexthw.starbunclemania.starbuncle.StarHelper;
import com.hollingsworth.arsnouveau.common.datagen.BlockTagProvider;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.StarbyTransportBehavior;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.minecraftforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER;

public class AdvancedItemTransportBehavior extends StarbyTransportBehavior{

    public static final ResourceLocation TRANSPORT_ID = new ResourceLocation(StarbuncleMania.MODID, "starby_adv_item_transport");
    public int side = -1;
    public AdvancedItemTransportBehavior(Starbuncle entity, CompoundTag tag) {
        super(entity, tag);
        if (tag.contains("Direction")) side = tag.getInt("Direction");
    }

    @Override
    public void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
        if (storedPos != null && playerEntity.level.getBlockState(storedPos).is(BlockTagProvider.SUMMON_SLEEPABLE)) {
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.starbuncle.set_bed"));
            starbuncle.data.bedPos = storedPos.immutable();
        }
        if (storedPos == null)
            return;
        BlockEntity blockEntity = level.getBlockEntity(storedPos);
        if (blockEntity != null && (blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).isPresent() || getItemCapFromTile(blockEntity) != null)) {
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.starbuncle.store"));
            addToPos(storedPos);
        }
    }

    @Override
    public void onFinishedConnectionLast(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
        if (storedPos == null)
            return;

        BlockEntity blockEntity = level.getBlockEntity(storedPos);
        if (blockEntity != null && (blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).isPresent() || getItemCapFromTile(blockEntity) != null)) {
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.starbuncle.take"));
            addFromPos(storedPos);
        }
    }

    @Override
    protected ResourceLocation getRegistryName() {
        return TRANSPORT_ID;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof DirectionScroll && stack.hasTag()){
            side = stack.getOrCreateTag().getInt("side");
            PortUtil.sendMessage(player, Component.translatable("ars_nouveau.filter_set"));
            syncTag();
        }
        return super.mobInteract(player, hand);
    }

    @Nullable
    @Override
    public IItemHandler getItemCapFromTile(BlockEntity be) {
        if (be == null) return null;
        int sideOrdinal = StarHelper.checkItemFramesForSide(be.getBlockPos(), level, side, be);
        Direction side = sideOrdinal < 0 ? Direction.UP : Direction.values()[sideOrdinal];
        return be.getCapability(ITEM_HANDLER, side).isPresent() && be.getCapability(ITEM_HANDLER, side).resolve().isPresent() ? be.getCapability(ITEM_HANDLER, side).resolve().get() : null;
    }


    @Override
    public CompoundTag toTag(CompoundTag tag) {
        if (side >= 0) tag.putInt("Direction", side);
        return super.toTag(tag);
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        super.getTooltip(tooltip);
        if (side >= 0){
            tooltip.add(Component.literal("Preferred Side : " + Direction.values()[side].name()));
        }
    }
}
