package alexthw.starbunclemania;

import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import static alexthw.starbunclemania.StarbuncleMania.prefix;

@Mod.EventBusSubscriber(modid = StarbuncleMania.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventHandler {



    @SubscribeEvent
    public static void registerCaps(AttachCapabilitiesEvent<BlockEntity> event) {
        if (event.getObject() instanceof MobJarTile tile){
            FluidTank tank = new FluidTank(16000) {
                protected void onContentsChanged() {
                    tile.updateBlock();
                    tile.setChanged();
                }
            };
            LazyOptional<IFluidHandler> holder = LazyOptional.of(() -> tank);

            ICapabilityProvider provider = new ICapabilityProvider() {
                @Override
                public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction direction) {
                    if (cap == ForgeCapabilities.FLUID_HANDLER) {
                        return holder.cast();
                    }
                    return LazyOptional.empty();
                }
            };

            event.addCapability(prefix("extra_fluid_handler"), provider);
        }

    }
}
