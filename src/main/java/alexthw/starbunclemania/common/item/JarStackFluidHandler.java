package alexthw.starbunclemania.common.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Redirects Fluid caps to the infos in the block entity tag
 */
public class JarStackFluidHandler extends FluidHandlerItemStack {
    /**
     * @param container The container itemStack, data is stored on it directly as NBT.
     * @param capacity  The maximum capacity of this fluid tank.
     */
    public JarStackFluidHandler(@NotNull ItemStack container, int capacity) {
        super(container, capacity);
    }

    @Override
    public @NotNull FluidStack getFluid() {
        return FluidJarItem.getFluidFromTag(this.container);
    }

    @Override
    protected void setFluid(FluidStack fluid) {
        CompoundTag fluidTag = new CompoundTag();

        fluid.writeToNBT(fluidTag);
        container.getOrCreateTag().put("BlockEntityTag", fluidTag);
    }

    @Override
    protected void setContainerToEmpty() {
        container.removeTagKey("BlockEntityTag");
    }
}
