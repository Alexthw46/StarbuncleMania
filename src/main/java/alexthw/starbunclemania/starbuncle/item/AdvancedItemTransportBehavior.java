package alexthw.starbunclemania.starbuncle.item;

import alexthw.starbunclemania.StarbuncleMania;
import alexthw.starbunclemania.starbuncle.StarHelper;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.StarbyTransportBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public class AdvancedItemTransportBehavior extends StarbyTransportBehavior {

    public static final ResourceLocation TRANSPORT_ID = ResourceLocation.fromNamespaceAndPath(StarbuncleMania.MODID, "starby_adv_item_transport");

    public AdvancedItemTransportBehavior(Starbuncle entity, CompoundTag tag) {
        super(entity, tag);
    }

    @Override
    public ResourceLocation getRegistryName() {
        return TRANSPORT_ID;
    }

    @Nullable
    @Override
    public IItemHandler getItemCapFromTile(BlockPos pos, Direction side) {
        if (pos == null) return null;
        side = StarHelper.checkItemFramesForSide(pos, level, side);
        return level.getCapability(Capabilities.ItemHandler.BLOCK,pos, side);
    }

    @Override
    public void onWanded(Player playerEntity) {
        // reset to default behavior if the accessory is removed
        if (starbuncle.getCosmeticItem().isEmpty())
            starbuncle.dynamicBehavior = new StarbyTransportBehavior(starbuncle, new CompoundTag());
        super.onWanded(playerEntity);
    }

}
