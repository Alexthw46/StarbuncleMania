package alexthw.starbunclemania.common.item.cosmetic;

import alexthw.starbunclemania.starbuncle.item.AdvancedItemTransportBehavior;
import com.hollingsworth.arsnouveau.api.entity.IDecoratable;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarBookwyrm;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarStarbuncle;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class ProfHat extends AbstractCosmeticItem {

    public ProfHat(Properties pProperties, String s) {
        super(pProperties, s);
    }

    @Override
    public void changeBehavior(ItemStack stack, Player player, IDecoratable deco) {
        if (deco instanceof Starbuncle starbuncle) {
            starbuncle.setBehavior(new AdvancedItemTransportBehavior(starbuncle, new CompoundTag()));
            PortUtil.sendMessage(player, Component.translatable("ars_nouveau.starbuncle.sided_item_behavior_set"));
        }
    }

    static final Vec3 wyrmScale = new Vec3(1.75, 1.75, 1.75);
    static final Vec3 wyrmTransl = new Vec3(0, 0.65, 0);

    static final Vec3 starbScale = new Vec3(1.25, 1.25, 1.25);
    static final Vec3 starbTransl = new Vec3(0, 0.43, -0.05);

    @Override
    public Vec3 getTranslations(LivingEntity entity) {
        return switch (entity) {
            case FamiliarBookwyrm ignored -> wyrmTransl;
            case null, default -> starbTransl;
        };
    }

    @Override
    public Vec3 getScaling(LivingEntity entity) {
        return switch (entity) {
            case FamiliarBookwyrm ignored -> wyrmScale;
            case null, default -> starbScale;
        };
    }

    @Override
    public boolean canWear(LivingEntity entity) {
        return entity instanceof FamiliarBookwyrm || entity instanceof FamiliarStarbuncle || entity instanceof Starbuncle;
    }

    @Override
    public ItemDisplayContext getTransformType() {
        return ItemDisplayContext.HEAD;
    }

}
