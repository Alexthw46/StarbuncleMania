package alexthw.starbunclemania.common.item.cosmetic;

import alexthw.starbunclemania.client.BalloonRenderer;
import alexthw.starbunclemania.starbuncle.gas.StarbyGasBehavior;
import com.hollingsworth.arsnouveau.api.entity.IDecoratable;
import com.hollingsworth.arsnouveau.api.item.ICosmeticItem;
import com.hollingsworth.arsnouveau.common.crafting.recipes.IDyeable;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarStarbuncle;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;

import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class StarBalloon extends Item implements ICosmeticItem, GeoItem, IDyeable {

    public StarBalloon(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack pStack, @NotNull Player pPlayer, @NotNull LivingEntity pInteractionTarget, @NotNull InteractionHand pUsedHand) {
        if (pInteractionTarget instanceof IDecoratable starbuncle && canWear(pInteractionTarget)) {
            starbuncle.setCosmeticItem(pStack.split(1));
            if (pInteractionTarget instanceof Starbuncle starby && !pPlayer.isShiftKeyDown() && ModList.get().isLoaded("mekanism")) {
                starby.setBehavior(new StarbyGasBehavior(starby, new CompoundTag()));
                PortUtil.sendMessage(pPlayer, Component.translatable("ars_nouveau.starbuncle.gas_behavior_set"));
            }
            return InteractionResult.SUCCESS;
        }

        return super.interactLivingEntity(pStack, pPlayer, pInteractionTarget, pUsedHand);
    }

    @Override
    public void onDye(ItemStack stack, DyeColor dyeColor) {
        stack.getOrCreateTag().putInt("color", dyeColor.getFireworkColor());
    }

    @Override
    public void initializeClient(@NotNull Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IClientItemExtensions() {
            final BalloonRenderer renderer = new BalloonRenderer();
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        });
    }

    static final Vec3 Translation = new Vec3(-0.1, 0, -0.05);
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
    public ItemDisplayContext getTransformType() {
        return ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;
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

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar animationData) {
        animationData.add(new AnimationController<>(this, "idle_controller", 1, tAnimationEvent ->
            tAnimationEvent.setAndContinue(RawAnimation.begin().thenLoop("float"))));
    }

    public final AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

}
