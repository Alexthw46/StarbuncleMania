package alexthw.starbunclemania.common.item.cosmetic;

import alexthw.starbunclemania.starbuncle.energy.StarbyEnergyBehavior;
import com.hollingsworth.arsnouveau.api.entity.IDecoratable;
import com.hollingsworth.arsnouveau.api.item.ICosmeticItem;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarStarbuncle;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class StarbAABattery extends Item implements ICosmeticItem {

    public StarbAABattery(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack pStack, Player pPlayer, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {
        if (pInteractionTarget instanceof IDecoratable starbuncle && canWear(pInteractionTarget)) {
            starbuncle.setCosmeticItem(pStack.split(1));
            if ( pInteractionTarget instanceof Starbuncle starby && !pPlayer.isShiftKeyDown()){
                starby.setBehavior(new StarbyEnergyBehavior(starby, new CompoundTag()));
                PortUtil.sendMessage(pPlayer, Component.translatable("ars_nouveau.starbuncle.energy_behavior_set"));
            }
            return InteractionResult.SUCCESS;
        }

        return super.interactLivingEntity(pStack, pPlayer, pInteractionTarget, pUsedHand);    }

    static final Vec3 translation = new Vec3(0, 0.25, 0.025);
    static final Vec3 scaling = new Vec3(1, 1, 1);

    @Override
    public Vec3 getTranslations() {
        return translation;
    }

    @Override
    public Vec3 getScaling() {
        return scaling;
    }

    @Override
    public String getBone() {
        return "body";
    }

    /**
     * @param entity check if is compatible with the cosmetic item
     */
    @Override
    public boolean canWear(LivingEntity entity) {
        return entity instanceof Starbuncle || entity instanceof FamiliarStarbuncle;
    }
}
