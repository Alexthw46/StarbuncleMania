package alexthw.starbunclemania;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = StarbuncleMania.MODID)
public class Configs {
    public static final Common COMMON;
    public static final ModConfigSpec COMMON_SPEC;
    public static final Server SERVER;
    public static final ModConfigSpec SERVER_SPEC;
    public static Map<ResourceLocation, Double> FLUID_TO_SOURCE_MAP = new HashMap<>();
    private static ModConfigSpec.ConfigValue<List<? extends String>> FLUID_TO_SOURCE_CONFIG;
    public static ModConfigSpec.IntValue SOURCE_TO_FLUID;

    public static ModConfigSpec.IntValue STARBUCKET_RATIO;
    public static ModConfigSpec.IntValue STARBUCKET_THRESHOLD;
    public static ModConfigSpec.IntValue STARBALLOON_RATIO;
    public static ModConfigSpec.IntValue STARBALLOON_THRESHOLD;
    public static ModConfigSpec.IntValue STARBATTERY_RATIO;
    public static ModConfigSpec.IntValue STARBATTERY_THRESHOLD;


    public static ModConfigSpec.LongValue GAS_SOURCE_BURN_VALUE;


    static {

        final Pair<Common, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Common::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();

        final Pair<Server, ModConfigSpec> specClientPair = new ModConfigSpec.Builder().configure(Server::new);
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
        for (Map.Entry<String, Double> entry : parseMapConfig(FLUID_TO_SOURCE_CONFIG).entrySet()) {
            FLUID_TO_SOURCE_MAP.put(ResourceLocation.parse(entry.getKey()), entry.getValue());
        }
    }

    public static Map<String, Double> getDefaultLiquidSource() {
        return new HashMap<>();
    }

    public static class Common {

        public Common(ModConfigSpec.Builder builder) {

        }
    }

    public static class Server {

        public Server(ModConfigSpec.Builder builder) {
            builder.push("General Configs");
            FLUID_TO_SOURCE_CONFIG = builder.comment("Value of milli-bucket of fluid converted in source by the sourcelink", "Example entry: \"minecraft:lava=1.6\"")
                    .defineList("fluid_to_source", writeConfig(getDefaultLiquidSource()), Configs::validateMap);
            SOURCE_TO_FLUID = builder.comment("Source cost to make a bucket of liquid source.").defineInRange("source_condense_cost", 1000, 1, Integer.MAX_VALUE);

            STARBUCKET_RATIO = builder.comment("Transfer rate of the fluid starbuncles").defineInRange("starbucket_ratio", 1000, 1, Integer.MAX_VALUE);
            STARBATTERY_RATIO = builder.comment("Transfer rate of the energy starbuncles").defineInRange("starbattery_ratio", 100000, 1, Integer.MAX_VALUE);
            STARBALLOON_RATIO = builder.comment("Transfer rate of the gas starbuncles").defineInRange("starballoon_ratio", 1000, 1, Integer.MAX_VALUE);
            STARBUCKET_THRESHOLD = builder.comment("Threshold of the fluid starbuncles, lower this if you need them to check and fill more often.").defineInRange("starbucket_threshold", 250, 1, Integer.MAX_VALUE);
            STARBATTERY_THRESHOLD = builder.comment("Threshold rate of the energy starbuncles, lower this if you need them to check and fill more often.").defineInRange("starbattery_threshold", 1000, 1, Integer.MAX_VALUE);
            STARBALLOON_THRESHOLD = builder.comment("Threshold rate of the gas starbuncles, lower this if you need them to check and fill more often.").defineInRange("starballoon_threshold", 250, 1, Integer.MAX_VALUE);

            builder.pop();

            builder.push("Mekanism Compat");
            GAS_SOURCE_BURN_VALUE = builder.comment("How much energy is produced per mB of Gaseous Source").defineInRange("source_gas_energy_density",200L, 0L, Long.MAX_VALUE);
            builder.pop();
        }
    }

    public static final Pattern STRING_FLOAT_MAP = Pattern.compile("([^/=]+)=(\\d\\.\\d+)");

    public static List<String> writeConfig(Map<String, Double> map) {
        return map.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue().toString())
                .collect(Collectors.toList());
    }

    public static boolean validateMap(Object rawConfig) {
        if (rawConfig instanceof CharSequence raw) {
            return STRING_FLOAT_MAP.matcher(raw).matches();
        }
        return false;
    }

    public static Map<String, Double> parseMapConfig(ModConfigSpec.ConfigValue<List<? extends String>> configValue) {
        return configValue.get().stream()
                .map(STRING_FLOAT_MAP::matcher)
                .filter(Matcher::matches)
                .collect(Collectors.toMap(
                        m -> m.group(1),
                        m -> Double.valueOf(m.group(2))
                ));
    }
}
