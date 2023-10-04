package alexthw.starbunclemania.starbuncle.item;

import alexthw.starbunclemania.StarbuncleMania;
import alexthw.starbunclemania.common.item.DirectionScroll;
import alexthw.starbunclemania.starbuncle.StarHelper;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.StarbyTransportBehavior;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.minecraftforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER;

public class AdvancedItemTransportBehavior extends StarbyTransportBehavior {

    public static final ResourceLocation TRANSPORT_ID = new ResourceLocation(StarbuncleMania.MODID, "starby_adv_item_transport");

    public AdvancedItemTransportBehavior(Starbuncle entity, CompoundTag tag) {
        super(entity, tag);
    }

    @Override
    protected ResourceLocation getRegistryName() {
        return TRANSPORT_ID;
    }

    @Nullable
    @Override
    public IItemHandler getItemCapFromTile(BlockEntity be, Direction side) {
        if (be == null) return null;
        int sideOrdinal = StarHelper.checkItemFramesForSide(be.getBlockPos(), level, side == null ? -1 : side.ordinal(), be);
        side = sideOrdinal < 0 ? null : Direction.from3DDataValue(sideOrdinal);
        return be.getCapability(ITEM_HANDLER, side).isPresent() && be.getCapability(ITEM_HANDLER, side).resolve().isPresent() ? be.getCapability(ITEM_HANDLER, side).resolve().get() : null;
    }

}
