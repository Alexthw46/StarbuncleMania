package alexthw.starbunclemania.mixin;

import com.hollingsworth.arsnouveau.api.item.inv.FilterableItemHandler;
import com.hollingsworth.arsnouveau.api.item.inv.InteractType;
import com.hollingsworth.arsnouveau.api.item.inv.InventoryManager;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import java.util.function.Predicate;

@Mixin(InventoryManager.class)
public interface InventoryManagerAccessor {

    @Invoker(remap = false)
    FilterableItemHandler callHighestPrefInventory(List<FilterableItemHandler> inventories, Predicate<ItemStack> predicate, InteractType type);
}
