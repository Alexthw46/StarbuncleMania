package alexthw.starbunclemania.common.item.cosmetic;

import com.hollingsworth.arsnouveau.api.entity.IDecoratable;
import com.hollingsworth.arsnouveau.api.item.ICosmeticItem;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarBookwyrm;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class ProfHat extends Item implements ICosmeticItem {

    public ProfHat(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack pStack, Player pPlayer, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {

        if (pInteractionTarget instanceof IDecoratable deco && canWear(pInteractionTarget)){
            deco.setCosmeticItem(pStack.split(1));
        }

        return InteractionResult.PASS;
    }

    @Override
    public Vec3 getTranslations() {
        return new Vec3(0,0.75,0);
    }

    @Override
    public Vec3 getScaling() {
        return new Vec3(2,2,2);
    }

    @Override
    public boolean canWear(LivingEntity entity) {
        return entity instanceof FamiliarBookwyrm;
    }
}
