package alexthw.starbunclemania.common.item;

import alexthw.starbunclemania.common.StarbyMountEntity;
import com.hollingsworth.arsnouveau.api.item.ICosmeticItem;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.items.ModItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class StarbySaddle extends ModItem implements ICosmeticItem {

    public StarbySaddle(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack pStack, Player pPlayer, LivingEntity entity, InteractionHand pUsedHand) {
        if (entity instanceof Starbuncle starbuncle && !(entity instanceof StarbyMountEntity)){
            starbuncle.setCosmeticItem(pStack.split(1));
            StarbyMountEntity mount = new StarbyMountEntity(pPlayer.level, starbuncle.data);
            mount.setPos(starbuncle.getX(), starbuncle.getY(), starbuncle.getZ());
            pPlayer.level.addFreshEntity(mount);
            starbuncle.discard();

            return InteractionResult.SUCCESS;
        }
        return super.interactLivingEntity(pStack, pPlayer, entity, pUsedHand);
    }


    @Override
    public Vec3 getTranslations() {
        return new Vec3(0,0,0);
    }

    @Override
    public Vec3 getScaling() {
        return new Vec3(0,0,0);
    }
}
