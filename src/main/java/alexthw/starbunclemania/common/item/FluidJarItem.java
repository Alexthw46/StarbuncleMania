package alexthw.starbunclemania.common.item;

import alexthw.starbunclemania.client.JarRenderer;
import alexthw.starbunclemania.common.block.LiquidJarTile;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.ars_nouveau.geckolib3.core.IAnimatable;
import software.bernie.ars_nouveau.geckolib3.core.manager.AnimationData;
import software.bernie.ars_nouveau.geckolib3.core.manager.AnimationFactory;

import java.util.List;
import java.util.function.Consumer;

public class FluidJarItem extends BlockItem implements IAnimatable {

    public FluidJarItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IClientItemExtensions() {
            final JarRenderer.ISTER renderer = new JarRenderer.ISTER();
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        });
    }

    public static FluidStack getFluidFromTag(ItemStack stack){
        if(!stack.hasTag())
            return FluidStack.EMPTY;
        CompoundTag blockTag = stack.getOrCreateTag().getCompound("BlockEntityTag");
        if (!blockTag.isEmpty()){
            return FluidStack.loadFluidStackFromNBT(blockTag);
        }
        return FluidStack.EMPTY;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
        if(pStack.hasTag()){
            CompoundTag blockTag = pStack.getOrCreateTag().getCompound("BlockEntityTag");
            if (!blockTag.isEmpty()) {
                FluidStack fluid = FluidStack.loadFluidStackFromNBT(blockTag);
                LiquidJarTile.displayFluidTooltip(pTooltip, fluid);
            }
        }
    }

    final AnimationFactory factory = new AnimationFactory(this);

    @Override
    public void registerControllers(AnimationData animationData) {
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

}
