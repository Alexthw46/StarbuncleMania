package alexthw.starbunclemania.common.item.cosmetic;

import alexthw.starbunclemania.starbuncle.trash.StarbyVoidBehavior;
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

public class StarBin extends AbstractCosmeticItem {

    public StarBin(Properties pProperties, String s) {
        super(pProperties,s);
    }

    @Override
    public void changeBehavior(ItemStack stack, Player player, IDecoratable deco) {
        if (deco instanceof Starbuncle starby && !player.isShiftKeyDown()) {
            starby.setBehavior(new StarbyVoidBehavior(starby, new CompoundTag()));
            PortUtil.sendMessage(player, Component.translatable("ars_nouveau.starbuncle.trash_behavior_set"));
        }
    }

    static final Vec3 Translation = new Vec3(0, 0.55, 0.15);
    static final Vec3 Scaling = new Vec3(1, 1, 1);

    @Override
    public Vec3 getTranslations() {
        return Translation;
    }

    @Override
    public Vec3 getScaling() {
        return Scaling;
    }

    @Override
    public String getBone(LivingEntity entity) {
        return "tail";
    }
    /**
     * @param entity check if is compatible with the cosmetic item
     */
    @Override
    public boolean canWear(LivingEntity entity) {
        return entity instanceof Starbuncle || entity instanceof FamiliarStarbuncle;
    }

}
