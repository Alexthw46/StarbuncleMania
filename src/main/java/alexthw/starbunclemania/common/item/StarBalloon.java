package alexthw.starbunclemania.common.item;

import alexthw.starbunclemania.starbuncle.gas.StarbyGasBehavior;
import com.hollingsworth.arsnouveau.api.entity.IDecoratable;
import com.hollingsworth.arsnouveau.api.item.ICosmeticItem;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarStarbuncle;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;

public class StarBalloon extends Item implements ICosmeticItem {

    public StarBalloon(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack pStack, Player pPlayer, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {
        if (pInteractionTarget instanceof IDecoratable starbuncle && canWear(pInteractionTarget)) {
            starbuncle.setCosmeticItem(pStack.split(1));
            if ( pInteractionTarget instanceof Starbuncle starby && ModList.get().isLoaded("mekanism")){
                starby.setBehavior(new StarbyGasBehavior(starby, new CompoundTag()));
            }
            return InteractionResult.SUCCESS;
        }

        return super.interactLivingEntity(pStack, pPlayer, pInteractionTarget, pUsedHand);    }

    final Vec3 Translation = new Vec3(+0.0,-0.22,-0.15);
    final Vec3 Scaling = new Vec3(1.3,1.2,1.3);

    @Override
    public Vec3 getTranslations() {
        return Translation;
    }

    @Override
    public Vec3 getScaling() {
        return Scaling;
    }

    /**
     * @param entity check if is compatible with the cosmetic item
     */
    @Override
    public boolean canWear(LivingEntity entity) {
        return entity instanceof Starbuncle || entity instanceof FamiliarStarbuncle;
    }

}
