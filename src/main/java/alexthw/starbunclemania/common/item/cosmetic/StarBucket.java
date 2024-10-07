package alexthw.starbunclemania.common.item.cosmetic;

import alexthw.starbunclemania.starbuncle.fluid.StarbyFluidBehavior;
import com.hollingsworth.arsnouveau.api.entity.IDecoratable;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarStarbuncle;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class StarBucket extends AbstractCosmeticItem {

    public StarBucket(Properties pProperties, String s) {
        super(pProperties, s);
    }

    @Override
    public void changeBehavior(ItemStack stack, Player player, IDecoratable deco) {
        if (deco instanceof Starbuncle starby) {
            starby.setBehavior(new StarbyFluidBehavior(starby, new CompoundTag()));
            PortUtil.sendMessage(player, Component.translatable("ars_nouveau.starbuncle.fluid_behavior_set"));
        }
    }

    final Vec3 Translation = new Vec3(0, 0, -0.05);
    final Vec3 Scaling = new Vec3(1.2, 1.075, 1.05);

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
