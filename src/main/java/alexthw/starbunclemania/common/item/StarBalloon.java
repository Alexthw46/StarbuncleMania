package alexthw.starbunclemania.common.item;

import alexthw.starbunclemania.client.BalloonRenderer;
import alexthw.starbunclemania.client.JarRenderer;
import alexthw.starbunclemania.starbuncle.gas.StarbyGasBehavior;
import com.hollingsworth.arsnouveau.api.entity.IDecoratable;
import com.hollingsworth.arsnouveau.api.item.ICosmeticItem;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarStarbuncle;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.fml.ModList;
import software.bernie.ars_nouveau.geckolib3.core.IAnimatable;
import software.bernie.ars_nouveau.geckolib3.core.PlayState;
import software.bernie.ars_nouveau.geckolib3.core.builder.AnimationBuilder;
import software.bernie.ars_nouveau.geckolib3.core.controller.AnimationController;
import software.bernie.ars_nouveau.geckolib3.core.manager.AnimationData;
import software.bernie.ars_nouveau.geckolib3.core.manager.AnimationFactory;

import java.util.function.Consumer;

public class StarBalloon extends Item implements ICosmeticItem, IAnimatable {

    public StarBalloon(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack pStack, Player pPlayer, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {
        if (pInteractionTarget instanceof IDecoratable starbuncle && canWear(pInteractionTarget)) {
            starbuncle.setCosmeticItem(pStack.split(1));
            if (pInteractionTarget instanceof Starbuncle starby && ModList.get().isLoaded("mekanism")) {
                starby.setBehavior(new StarbyGasBehavior(starby, new CompoundTag()));
            }
            return InteractionResult.SUCCESS;
        }

        return super.interactLivingEntity(pStack, pPlayer, pInteractionTarget, pUsedHand);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IClientItemExtensions() {
            final BalloonRenderer renderer = new BalloonRenderer();
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        });
    }

    Vec3 Translation = new Vec3(+0.0, -0.22, -0.15);
    Vec3 Scaling = new Vec3(1, 1, 1);

    @Override
    public Vec3 getTranslations() {
        return Translation;
    }

    @Override
    public Vec3 getScaling() {
        return Scaling;
    }

    @Override
    public ItemTransforms.TransformType getTransformType() {
        return ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND;
    }

    /**
     * @param entity check if is compatible with the cosmetic item
     */
    @Override
    public boolean canWear(LivingEntity entity) {
        return entity instanceof Starbuncle || entity instanceof FamiliarStarbuncle;
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>(this, "idle_controller", 1.0F, tAnimationEvent -> {
            tAnimationEvent.getController().setAnimation((new AnimationBuilder()).addAnimation("float"));
            return PlayState.CONTINUE;
        }));
    }

    public final AnimationFactory factory = new AnimationFactory(this);

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}
