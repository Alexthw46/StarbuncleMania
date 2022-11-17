package alexthw.starbunclemania;

import alexthw.starbunclemania.registry.ModRegistry;
import com.hollingsworth.arsnouveau.setup.ConfigUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = StarbuncleMania.MODID)
public class Configs {
    public static final Common COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Server SERVER;
    public static final ForgeConfigSpec SERVER_SPEC;
    public static Map<ResourceLocation, Integer> FLUID_TO_SOURCE_MAP = new HashMap<>();
    private static ForgeConfigSpec.ConfigValue<List<? extends String>> FLUID_TO_SOURCE_CONFIG;

    static {

        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();

        final Pair<Server, ForgeConfigSpec> specClientPair = new ForgeConfigSpec.Builder().configure(Server::new);
        SERVER_SPEC = specClientPair.getRight();
        SERVER = specClientPair.getLeft();

    }

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading configEvent) {
        if (configEvent.getConfig().getSpec() == SERVER_SPEC) {
            resetLiquidSource();
        }
    }

    @SubscribeEvent
    public static void onReload(final ModConfigEvent.Reloading configEvent) {
        if (configEvent.getConfig().getSpec() == SERVER_SPEC) {
            resetLiquidSource();
        }
    }

    private static void resetLiquidSource() {
        FLUID_TO_SOURCE_MAP = new HashMap<>();
        // Copy values from FLUID_TO_SOURCE_CONFIG to FLUID_TO_SOURCE_MAP
        for (Map.Entry<String, Integer> entry : ConfigUtil.parseMapConfig(FLUID_TO_SOURCE_CONFIG).entrySet()) {
            FLUID_TO_SOURCE_MAP.put(new ResourceLocation(entry.getKey()), entry.getValue());
        }
    }

    public static Map<String, Integer> getDefaultLiquidSource() {
        Map<String, Integer> map = new HashMap<>();
        map.put(ModRegistry.SOURCE_FLUID_TYPE.getId().toString(), 900);
        map.put(ForgeMod.LAVA_TYPE.getId().toString(), 500);
        return map;
    }

    public static class Common {

        public Common(ForgeConfigSpec.Builder builder) {

        }
    }

    public static class Server {

        public Server(ForgeConfigSpec.Builder builder) {
            FLUID_TO_SOURCE_CONFIG = builder.comment("Value of bucket of fluid converted in source by the sourcelink", "Example entry: minecraft:lava=500")
                    .defineList("fluid_to_source", ConfigUtil.writeConfig(getDefaultLiquidSource()), ConfigUtil::validateMap);

        }
    }
}
