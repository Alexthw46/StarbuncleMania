package alexthw.starbunclemania.common.item;

import alexthw.starbunclemania.common.data.DirectionData;
import alexthw.starbunclemania.registry.ModRegistry;
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

    public DirectionScroll(Properties basicItemProperties) {
        super(basicItemProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {

        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        if (stack.getItem() instanceof DirectionScroll && pPlayer.isShiftKeyDown() && !pPlayer.level().isClientSide()){
            Direction side = Direction.fromYRot(pPlayer.getYRot());
            if (pPlayer.getXRot() > 60){
                side = Direction.DOWN;
            } else if (pPlayer.getXRot() < -60) {
                side = Direction.UP;
            }
            stack.set(ModRegistry.DIRECTION, new DirectionData(side));
            PortUtil.sendMessage(pPlayer, Component.literal("Direction set to " + side.name()));
        }

        return super.use(pLevel, pPlayer, pUsedHand);
    }

}
