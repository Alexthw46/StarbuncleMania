package alexthw.starbunclemania.registry;

import alexthw.starbunclemania.StarbuncleMania;
import mekanism.api.chemical.gas.Gas;
import mekanism.common.base.IChemicalConstant;
import mekanism.common.registration.impl.GasDeferredRegister;
import mekanism.common.registration.impl.GasRegistryObject;
import net.minecraftforge.eventbus.api.IEventBus;

public class MekanismCompat {

    public static final GasDeferredRegister GASES = new GasDeferredRegister(StarbuncleMania.MODID);
    public static final GasRegistryObject<Gas> SOURCE_GAS = GASES.register(new IChemicalConstant() {
        @Override
        public String getName() {
            return "source_gas";
        }

        @Override
        public int getColor() {
            return 0xDF9B13FB;
        }

        @Override
        public float getTemperature() {
            return 300;
        }

        @Override
        public float getDensity() {
            return 0;
        }

        @Override
        public int getLightLevel() {
            return 0;
        }
    });


    public static void register(IEventBus bus) {
        GASES.register(bus);
    }

}
