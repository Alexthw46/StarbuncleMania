package alexthw.starbunclemania.common.item;

import com.hollingsworth.arsnouveau.api.item.ICosmeticItem;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarStarbuncle;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.phys.Vec3;

public class StarSword extends SwordItem implements ICosmeticItem {

    public StarSword(Properties pProperties) {
        super(Tiers.GOLD, 1, 1, pProperties);
    }

    @Override
    public Vec3 getTranslations() {
        return new Vec3(0,0,0);
    }

    @Override
    public Vec3 getScaling() {
        return new Vec3(0,0,0);
    }

    /**
     * @param entity check if is compatible with the cosmetic item
     */
    @Override
    public boolean canWear(LivingEntity entity) {
        return entity instanceof Starbuncle || entity instanceof FamiliarStarbuncle;
    }

}
