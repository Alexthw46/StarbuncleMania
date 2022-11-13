package alexthw.starbunclemania.starbuncle.trash;

import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.FindItem;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.StarbyTransportBehavior;
import net.minecraft.world.item.ItemStack;

public class SnatchItem extends FindItem {
    public SnatchItem(Starbuncle starbuncle, StarbyTransportBehavior transportBehavior) {
        super(starbuncle, transportBehavior);
    }

    @Override
    public boolean canUse() {
        behavior.starbuncle.setHeldStack(ItemStack.EMPTY);
        return super.canUse();
    }

}
