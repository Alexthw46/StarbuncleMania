package alexthw.starbunclemania;

import alexthw.starbunclemania.common.block.fluids.LiquidJarTile;
import alexthw.starbunclemania.registry.ModRegistry;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

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

        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModRegistry.FLUID_JAR_TILE.get(), (be, side) -> be.tank
        );

        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModRegistry.FLUID_SOURCELINK_TILE.get(), (be, side) -> be.tank
        );

        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModRegistry.SOURCE_CONDENSER_TILE.get(), (be, side) -> be.tank
        );

        event.registerItem(Capabilities.FluidHandler.ITEM, (s, c) -> new FluidHandlerItemStack(ModRegistry.FLUID_CONTENT,s, LiquidJarTile.capacity),
                ModRegistry.FLUID_JAR.get().asItem());

    }
}
