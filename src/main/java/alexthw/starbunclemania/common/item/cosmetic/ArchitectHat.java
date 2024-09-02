package alexthw.starbunclemania.common.item.cosmetic;

import alexthw.starbunclemania.starbuncle.placer.StarbyPlacerBehavior;
import com.hollingsworth.arsnouveau.api.entity.IDecoratable;
import com.hollingsworth.arsnouveau.api.item.ICosmeticItem;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
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
import org.jetbrains.annotations.NotNull;

public class ArchitectHat extends Item implements ICosmeticItem {

    public ArchitectHat(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack pStack, @NotNull Player pPlayer, @NotNull LivingEntity pInteractionTarget, @NotNull InteractionHand pUsedHand) {

        if (pInteractionTarget instanceof IDecoratable deco && canWear(pInteractionTarget)) {
            deco.setCosmeticItem(pStack.split(1));
            if (deco instanceof Starbuncle starbuncle) {
                starbuncle.setBehavior(new StarbyPlacerBehavior(starbuncle, new CompoundTag()));
                PortUtil.sendMessage(pPlayer, Component.translatable("ars_nouveau.starbuncle.placer_behavior_set"));
            }
        }

        return InteractionResult.PASS;
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