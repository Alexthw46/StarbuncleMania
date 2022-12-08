package alexthw.starbunclemania.common.item.cosmetic;

import alexthw.starbunclemania.starbuncle.item.AdvancedItemTransportBehavior;
import com.hollingsworth.arsnouveau.api.entity.IDecoratable;
import com.hollingsworth.arsnouveau.api.item.ICosmeticItem;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarBookwyrm;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarStarbuncle;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
            if (deco instanceof Starbuncle starbuncle){
                starbuncle.setBehavior(new AdvancedItemTransportBehavior(starbuncle, new CompoundTag()));
                PortUtil.sendMessage(pPlayer, Component.translatable("ars_nouveau.starbuncle.sided_item_behavior_set"));
            }
        }

        return InteractionResult.PASS;
    }

    static final Vec3 wyrmScale = new Vec3(1.75,1.75,1.75);
    static final Vec3 wyrmTransl = new Vec3(0,0.675,0);

    static final Vec3 starbScale = new Vec3(1.25,1.25,1.25);
    static final Vec3 starbTransl = new Vec3(1.25,1.25,1.25);

    @Override
    public Vec3 getTranslations(LivingEntity entity) {
        if (entity instanceof FamiliarBookwyrm)
            return wyrmTransl;
        return starbTransl;
    }

    @Override
    public Vec3 getScaling(LivingEntity entity) {
        if (entity instanceof FamiliarBookwyrm)
            return wyrmScale;
        else return starbScale;
    }

    @Override
    public Vec3 getScaling() {
        return getScaling(null);
    }

    @Override
    public Vec3 getTranslations() {
        return getTranslations(null);
    }

    @Override
    public boolean canWear(LivingEntity entity) {
        return entity instanceof FamiliarBookwyrm || entity instanceof FamiliarStarbuncle || entity instanceof Starbuncle;
    }

    @Override
    public ItemTransforms.TransformType getTransformType() {
        return ItemTransforms.TransformType.HEAD;
    }
}
