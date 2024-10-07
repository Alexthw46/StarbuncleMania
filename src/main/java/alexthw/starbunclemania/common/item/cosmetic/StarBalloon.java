package alexthw.starbunclemania.common.item.cosmetic;

import alexthw.starbunclemania.client.BalloonRenderer;
import alexthw.starbunclemania.starbuncle.gas.StarbyGasBehavior;
import com.hollingsworth.arsnouveau.api.entity.IDecoratable;
import com.hollingsworth.arsnouveau.common.crafting.recipes.IDyeable;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarBookwyrm;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarDrygmy;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarEntity;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarWixie;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class StarBalloon extends AbstractCosmeticItem implements GeoItem, IDyeable {

    public StarBalloon(Properties pProperties, String s) {
        super(pProperties, s);
    }

    @Override
    public void onDye(ItemStack stack, DyeColor dyeColor) {
        stack.set(DataComponents.DYED_COLOR, new DyedItemColor(dyeColor.getTextureDiffuseColor(),false));
    }

    @Override
    public void changeBehavior(ItemStack stack, Player player, IDecoratable deco) {
        if (deco instanceof Starbuncle starby && ModList.get().isLoaded("mekanism")) {
            starby.setBehavior(new StarbyGasBehavior(starby, new CompoundTag()));
            PortUtil.sendMessage(player, Component.translatable("ars_nouveau.starbuncle.gas_behavior_set"));
        }
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            final BalloonRenderer renderer = new BalloonRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                return renderer;
            }
        });
    }

    static final Vec3 Translation = new Vec3(-0.1, 0, -0.05);
    static final Vec3 Scaling = new Vec3(1, 1, 1);

    @Override
    public Vec3 getTranslations(LivingEntity entity) {
        return switch (entity) {
            case FamiliarWixie ignored -> Vec3.ZERO;
            case FamiliarDrygmy ignored -> Vec3.ZERO;
            case FamiliarBookwyrm ignored -> Vec3.ZERO;
            default -> Translation;
        };
    }

    @Override
    public Vec3 getScaling(LivingEntity entity) {
        return switch (entity) {
            case FamiliarWixie ignored -> defaultScaling;
            case FamiliarDrygmy ignored -> defaultScaling;
            case FamiliarBookwyrm ignored -> defaultScaling;
            case null, default -> Scaling;
        };
    }

    @Override
    public ItemDisplayContext getTransformType() {
        return ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;
    }

    @Override
    public String getBone(LivingEntity entity) {
        return "body";
    }

    /**
     * @param entity check if is compatible with the cosmetic item
     */
    @Override
    public boolean canWear(LivingEntity entity) {
        return entity instanceof Starbuncle || entity instanceof FamiliarEntity;
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
