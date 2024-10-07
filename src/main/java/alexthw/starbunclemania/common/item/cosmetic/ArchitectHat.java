package alexthw.starbunclemania.common.item.cosmetic;

import alexthw.starbunclemania.starbuncle.AuthorizedBehavior;
import alexthw.starbunclemania.starbuncle.placer.StarbyPlacerBehavior;
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

public class ArchitectHat extends AbstractCosmeticItem {

    public ArchitectHat(Properties properties, String s) {
        super(properties, s);
    }

    @Override
    public void changeBehavior(ItemStack stack, Player player, IDecoratable deco) {
        if (deco instanceof Starbuncle starbuncle) {
            starbuncle.setBehavior(new StarbyPlacerBehavior(starbuncle, new CompoundTag()));
            if (starbuncle.dynamicBehavior instanceof AuthorizedBehavior auth) {
                auth.setOwnerUUID(player.getUUID());
            }
            PortUtil.sendMessage(player, Component.translatable("ars_nouveau.starbuncle.placer_behavior_set"));
        }
    }

    @Override
    public boolean canWear(LivingEntity entity) {
        return entity instanceof Starbuncle || entity instanceof FamiliarStarbuncle;
    }

    @Override
    public Vec3 getTranslations() {
        return new Vec3(0, 0.25, -0.125);
    }

    @Override
    public Vec3 getScaling() {
        return new Vec3(1.0, 1.0, 1.0);
    }

}