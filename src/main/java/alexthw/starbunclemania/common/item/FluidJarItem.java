package alexthw.starbunclemania.common.item;

import alexthw.starbunclemania.client.JarRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;

import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class FluidJarItem extends BlockItem implements GeoItem {

    public FluidJarItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    public void initializeClient(@NotNull Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IClientItemExtensions() {
            final JarRenderer.ISTER renderer = new JarRenderer.ISTER();
            @Override
            public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        });
    }

//    public static FluidStack getFluidFromTag(ItemStack stack){
//        if(!stack.hasTag())
//            return FluidStack.EMPTY;
//        CompoundTag blockTag = stack.getOrCreateTag().getCompound("BlockEntityTag");
//        if (!blockTag.isEmpty()){
//            return FluidStack.loadFluidStackFromNBT(blockTag);
//        }
//        return FluidStack.EMPTY;
//    }

    final AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar animationData) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

}
