package alexthw.starbunclemania.wixie;

import com.hollingsworth.arsnouveau.api.recipe.CraftingManager;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import java.util.ArrayList;
import java.util.List;

public class FluidCraftManager extends CraftingManager {

    public List<FluidStack> neededFluids = new ArrayList<>();

    public FluidCraftManager(ItemStack outputStack, List<ItemStack> neededItems, List<FluidStack> neededFluids) {
        super(outputStack, neededItems);
        this.neededFluids = neededFluids;
    }


    public FluidStack getNextFluid() {
        return !this.neededFluids.isEmpty() ? this.neededFluids.getFirst() : FluidStack.EMPTY;
    }

    public FluidStack getNeededFluid() {
        return FluidStack.EMPTY;
    }

    public boolean canBeCompleted() {
        return this.neededItems.isEmpty() && this.neededFluids.isEmpty();
    }

    public boolean giveFluid(FluidStack fluidStack) {
        if (canBeCompleted())
            return false;

        FluidStack stackToRemove = FluidStack.EMPTY;
        for (FluidStack stack : neededFluids) {
            if (FluidStack.isSameFluidSameComponents(stack, fluidStack)) {
                stackToRemove = stack;
                break;
            }
        }
        return neededFluids.remove(stackToRemove);
    }
}
