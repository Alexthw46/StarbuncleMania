package alexthw.starbunclemania.common.item.cosmetic;

import com.hollingsworth.arsnouveau.api.entity.IDecoratable;
import com.hollingsworth.arsnouveau.api.item.ICosmeticItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public abstract class AbstractCosmeticItem extends Item implements ICosmeticItem {

    private final String tooltipText;

    public AbstractCosmeticItem(Properties properties, String tooltipText){
        super(properties);
        this.tooltipText = tooltipText;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
        if (interactionTarget instanceof IDecoratable deco && canWear(interactionTarget)) {
            deco.setCosmeticItem(stack.split(1));
            if (!player.isShiftKeyDown())
                changeBehavior(stack, player, deco);
            return InteractionResult.SUCCESS;
        }
        return super.interactLivingEntity(stack, player, interactionTarget, usedHand);
    }

    public void changeBehavior(ItemStack stack, Player player, IDecoratable deco) {

    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if (!tooltipText.isEmpty()){
            tooltipComponents.add(Component.translatable(tooltipText));
        }
    }

}
