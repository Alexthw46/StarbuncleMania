package alexthw.starbunclemania;

import alexthw.starbunclemania.common.block.fluids.AbstractTankTile;
import alexthw.starbunclemania.common.item.JarStackFluidHandler;
import alexthw.starbunclemania.registry.ModRegistry;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = StarbuncleMania.MODID, bus = EventBusSubscriber.Bus.MOD)
public class EventHandler {


    @SubscribeEvent
    public static void registerCaps(RegisterCapabilitiesEvent event) {

        event.registerBlock(Capabilities.FluidHandler.BLOCK, (level, pos, state, be, context) -> new FluidTank(1600) {
            @Override
            protected void onContentsChanged() {
                if (level.getBlockEntity(pos) instanceof MobJarTile tile) {
                    tile.setChanged();
                    tile.updateBlock();
                }
            }
        }, BlockRegistry.MOB_JAR.get());

        event.registerBlock(Capabilities.FluidHandler.BLOCK, (level, pos, state, be, context) -> new FluidTank(1600) {
            @Override
            protected void onContentsChanged() {
                if (level.getBlockEntity(pos) instanceof AbstractTankTile tile) {
                    tile.setChanged();
                    tile.updateBlock();
                }
            }

            @Override
            public boolean isFluidValid(@NotNull FluidStack stack) {
                if (level.getBlockEntity(pos) instanceof AbstractTankTile tile) {
                    return tile.isFluidValid(stack);
                }
                return super.isFluidValid(stack);
            }
        }, ModRegistry.FLUID_JAR.get(), ModRegistry.FLUID_SOURCELINK.get(), ModRegistry.SOURCE_CONDENSER.get());


        event.registerItem(Capabilities.FluidHandler.ITEM, (s, c) -> new JarStackFluidHandler(s, 1600),
                ModRegistry.FLUID_JAR.get().asItem());

    }
}
