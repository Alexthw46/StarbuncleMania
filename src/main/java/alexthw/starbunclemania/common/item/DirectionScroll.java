package alexthw.starbunclemania.common.item;

import com.hollingsworth.arsnouveau.common.items.ModItem;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DirectionScroll extends ModItem {

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {

        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        if (stack.getItem() instanceof DirectionScroll){
            Direction side = pPlayer.getDirection();
            stack.getOrCreateTag().putInt("side", side.ordinal());
            PortUtil.sendMessage(pPlayer, Component.literal("Direction set to " + side.getName()));
        }

        return super.use(pLevel, pPlayer, pUsedHand);

    }



}
