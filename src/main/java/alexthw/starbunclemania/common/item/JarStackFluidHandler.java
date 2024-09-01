package alexthw.starbunclemania.common.item;

import alexthw.starbunclemania.registry.ModRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
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
        super(ModRegistry.FLUID_CONTENT,container, capacity);
    }
}
