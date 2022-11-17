package alexthw.starbunclemania.starbuncle.trash;

import alexthw.starbunclemania.StarbuncleMania;
import com.hollingsworth.arsnouveau.common.block.SummonBed;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.StarbyTransportBehavior;
import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import com.hollingsworth.arsnouveau.common.items.itemscrolls.MimicItemScroll;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StarbyVoidBehavior extends StarbyTransportBehavior {

    public StarbyVoidBehavior(Starbuncle entity, CompoundTag tag) {
        super(entity, tag);
        this.goals.add(new WrappedGoal(3, new SnatchItem(starbuncle, this)));
    }

    @Override
    public void onWanded(Player playerEntity) {
        starbuncle.setColor(Starbuncle.COLORS.ORANGE.name());
        super.onWanded(playerEntity);
        starbuncle.dynamicBehavior = new StarbyTransportBehavior(starbuncle, new CompoundTag());
        PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.starbuncle.default_behavior"));
        starbuncle.syncBehavior();
    }

    public void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
        if (storedPos != null && playerEntity.level.getBlockState(storedPos).getBlock() instanceof SummonBed) {
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.starbuncle.set_bed"));
            starbuncle.data.bedPos = storedPos.immutable();
        }
    }

    @Override
    public void onFinishedConnectionLast(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
    }

    @Override
    public void pickUpItem(ItemEntity itemEntity) {
        super.pickUpItem(itemEntity);
        starbuncle.setHeldStack(ItemStack.EMPTY);
    }

    @Override
    public BlockPos getValidStorePos(ItemStack stack) {
        if (itemScroll != null && itemScroll.getItem() instanceof ItemScroll filter && !(filter instanceof MimicItemScroll)){
            if (filter.getSortPref(stack, itemScroll,null) != ItemScroll.SortPref.INVALID) {
                return starbuncle.getOnPos();
            }
            return null;
        }
        return starbuncle.getOnPos();
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        tooltip.add(Component.translatable("ars_nouveau.starbuncle.trashing_items"));
        if (itemScroll != null && !itemScroll.isEmpty()) {
            tooltip.add(Component.translatable("ars_nouveau.filtering_with", itemScroll.getHoverName().getString()));
        }
    }

    @Override
    protected ResourceLocation getRegistryName() {
        return TRANSPORT_ID;
    }
    public static final ResourceLocation TRANSPORT_ID = new ResourceLocation(StarbuncleMania.MODID, "starby_item_void");

}
