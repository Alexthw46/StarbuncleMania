package alexthw.starbunclemania.registry;

import alexthw.starbunclemania.StarbuncleMania;
import alexthw.starbunclemania.client.FluidSourceLinkRenderer;
import alexthw.starbunclemania.client.SourceCondenserRenderer;
import alexthw.starbunclemania.common.StarbyMountEntity;
import alexthw.starbunclemania.common.block.fluids.*;
import alexthw.starbunclemania.common.block.wixie_stations.*;
import alexthw.starbunclemania.common.item.DirectionScroll;
import alexthw.starbunclemania.common.item.FluidJarItem;
import alexthw.starbunclemania.common.item.FluidScroll;
import alexthw.starbunclemania.common.item.cosmetic.*;
import alexthw.starbunclemania.recipe.FluidSourcelinkRecipe;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.items.RendererBlockItem;
import com.hollingsworth.arsnouveau.setup.registry.CreativeTabRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static alexthw.starbunclemania.StarbuncleMania.prefix;

@SuppressWarnings({"Convert2MethodRef", "ConstantConditions", "SpellCheckingInspection"})
public class ModRegistry {

    private static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, StarbuncleMania.MODID);
    private static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, StarbuncleMania.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, StarbuncleMania.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, StarbuncleMania.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, StarbuncleMania.MODID);

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, StarbuncleMania.MODID);

    // Maybe switch source conversion to recipes, currently in configs
    public static final DeferredRegister<RecipeType<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, StarbuncleMania.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> R_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, StarbuncleMania.MODID);

    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, StarbuncleMania.MODID);
    public static final TagKey<Fluid> POTION = FluidTags.create(new ResourceLocation("forge", "potion"));

    public static void registerRegistries(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        FLUID_TYPES.register(bus);
        FLUIDS.register(bus);
        BLOCK_ENTITIES.register(bus);
        ENTITIES.register(bus);
        RECIPES.register(bus);
        R_SERIALIZERS.register(bus);
        TABS.register(bus);
        bus.addListener(ModRegistry::registerEntityAttributes);
        bus.addListener(ModRegistry::editEntityAttributes);
        if (ModList.get().isLoaded("mekanism")) {
            MekanismCompat.register(bus);
        }
        if (ModList.get().isLoaded("farmersdelight")) {
            FarmerDelightCompat.register();
        }
    }

    public static void registerEntityAttributes(final EntityAttributeCreationEvent event) {
        event.put(STARBY_MOUNT.get(), Starbuncle.attributes().add(Attributes.MAX_HEALTH, 20).build());
    }

    public static void editEntityAttributes(final EntityAttributeModificationEvent event) {
        event.add(ModEntities.STARBUNCLE_TYPE.get(), Attributes.ATTACK_DAMAGE, 2);
        event.add(ModEntities.STARBUNCLE_TYPE.get(), Attributes.FOLLOW_RANGE, 5);
    }

    public static final RegistryObject<RecipeType<FluidSourcelinkRecipe>> FLUID_SOURCELINK_RT;
    public static final RegistryObject<RecipeSerializer<FluidSourcelinkRecipe>> FLUID_SOURCELINK_RS;

    static {
        FLUID_SOURCELINK_RT = RECIPES.register("fluid_sourcelink", () -> RecipeType.simple(prefix("fluid_sourcelink")));
        FLUID_SOURCELINK_RS = R_SERIALIZERS.register("fluid_sourcelink", FluidSourcelinkRecipe.Serializer::new);
    }

    public static final RegistryObject<Item> DIRECTION_SCROLL;
    public static final RegistryObject<Item> FLUID_SCROLL_A;
    public static final RegistryObject<Item> FLUID_SCROLL_D;

    public static final RegistryObject<Item> STARHAT;
    public static final RegistryObject<Item> PROFHAT;
    public static final RegistryObject<Item> STARBATTERY;
    public static final RegistryObject<Item> STARBUCKET;
    public static final RegistryObject<Item> STARBALLON;
    public static final RegistryObject<Item> STARTRASH;
    public static final RegistryObject<Item> STARSWORD;
    public static final RegistryObject<Item> STARWAND;
    public static final RegistryObject<Item> STARSADDLE;


    public static final RegistryObject<Block> FLUID_JAR;
    public static final RegistryObject<BlockEntityType<LiquidJarTile>> FLUID_JAR_TILE;
    public static final RegistryObject<Block> SOURCE_CONDENSER;
    public static final RegistryObject<BlockEntityType<SourceCondenserTile>> SOURCE_CONDENSER_TILE;
    public static final RegistryObject<Block> FLUID_SOURCELINK;
    public static final RegistryObject<BlockEntityType<FluidSourcelinkTile>> FLUID_SOURCELINK_TILE;

    public static final RegistryObject<Block> SMELTING_WIXIE_CAULDRON;
    public static final RegistryObject<BlockEntityType<SmeltingWixieCauldronTile>> SMELTING_WIXIE_CAULDRON_TILE;
    public static final RegistryObject<Block> STONEWORK_WIXIE_CAULDRON;
    public static final RegistryObject<BlockEntityType<StonecutterWixieCauldronTile>> STONECUTTER_WIXIE_CAULDRON_TILE;

    public static final RegistryObject<Block> FLUID_MIX_WIXIE_CAULDRON;
    public static final RegistryObject<BlockEntityType<FluidMixWixieCauldronTile>> FLUID_MIX_WIXIE_CAULDRON_TILE;

    public static final RegistryObject<EntityType<StarbyMountEntity>> STARBY_MOUNT;

    static {
        STARHAT = ITEMS.register("star_hat", () -> new ExampleCosmetic(basicItemProperties()));
        PROFHAT = ITEMS.register("wyrm_degree", () -> new ProfHat(basicItemProperties()));
        STARBATTERY = ITEMS.register("star_battery", () -> new StarbAABattery(basicItemProperties()));
        STARBUCKET = ITEMS.register("star_bucket", () -> new StarBucket(basicItemProperties()));
        STARBALLON = ITEMS.register("star_balloon", () -> new StarBalloon(basicItemProperties()));
        STARTRASH = ITEMS.register("star_bin", () -> new StarBin(basicItemProperties()));
        STARSWORD = ITEMS.register("star_sword", () -> new StarSword(basicItemProperties()));
        STARWAND = ITEMS.register("star_wand", () -> new StarWand(new Item.Properties()));

        DIRECTION_SCROLL = ITEMS.register("direction_scroll", () -> new DirectionScroll(basicItemProperties()));
        FLUID_SCROLL_A = ITEMS.register("fluid_scroll_allow", () -> new FluidScroll(basicItemProperties()));
        FLUID_SCROLL_D = ITEMS.register("fluid_scroll_deny", () -> new FluidScroll(basicItemProperties()) {
            @Override
            public boolean isDenied(ItemStack fluidScroll, FluidStack fluidInTank) {
                FluidData filter = new FluidData(fluidScroll);
                return filter.containsStack(fluidInTank);
            }
        });

        STARSADDLE = ITEMS.register("star_saddle", () -> new StarbySaddle(basicItemProperties()));

        FLUID_JAR = BLOCKS.register("fluid_jar", () -> new LiquidJarBlock());
        ITEMS.register("fluid_jar", () -> new FluidJarItem(FLUID_JAR.get(), basicItemProperties().stacksTo(1)));
        FLUID_JAR_TILE = BLOCK_ENTITIES.register("fluid_jar_tile", () -> BlockEntityType.Builder.of(LiquidJarTile::new, FLUID_JAR.get()).build(null));

        SOURCE_CONDENSER = BLOCKS.register("source_condenser", () -> new SourceCondenserBlock());
        SOURCE_CONDENSER_TILE = BLOCK_ENTITIES.register("source_condenser_tile", () -> BlockEntityType.Builder.of(SourceCondenserTile::new, SOURCE_CONDENSER.get()).build(null));
        ITEMS.register("source_condenser", () -> new RendererBlockItem(SOURCE_CONDENSER.get(), basicItemProperties()) {
            @Override
            public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                return SourceCondenserRenderer::getISTER;
            }
        });

        FLUID_SOURCELINK = BLOCKS.register("fluid_sourcelink", () -> new FluidSourcelinkBlock());
        FLUID_SOURCELINK_TILE = BLOCK_ENTITIES.register("fluid_sourcelink_tile", () -> BlockEntityType.Builder.of(FluidSourcelinkTile::new, FLUID_SOURCELINK.get()).build(null));
        ITEMS.register("fluid_sourcelink", () -> new RendererBlockItem(FLUID_SOURCELINK.get(), basicItemProperties()) {
            @Override
            public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                return FluidSourceLinkRenderer::getISTER;
            }
        });

        STARBY_MOUNT = addEntity("starby_mount", 2, 2, true, false, (entityCarbuncleEntityType, world) -> new StarbyMountEntity(entityCarbuncleEntityType, world), MobCategory.CREATURE);

        SMELTING_WIXIE_CAULDRON = BLOCKS.register("smelting_wixie_cauldron", SmeltingWixieCauldron::new);
        SMELTING_WIXIE_CAULDRON_TILE = BLOCK_ENTITIES.register("smelting_wixie_cauldron_tile", () -> BlockEntityType.Builder.of(SmeltingWixieCauldronTile::new, SMELTING_WIXIE_CAULDRON.get()).build(null));

        STONEWORK_WIXIE_CAULDRON = BLOCKS.register("stonecutting_wixie_cauldron", StonecutterWixieCauldron::new);
        STONECUTTER_WIXIE_CAULDRON_TILE = BLOCK_ENTITIES.register("stonecutting_wixie_cauldron_tile", () -> BlockEntityType.Builder.of(StonecutterWixieCauldronTile::new, STONEWORK_WIXIE_CAULDRON.get()).build(null));

        FLUID_MIX_WIXIE_CAULDRON = BLOCKS.register("fluid_mix_wixie_cauldron", FluidMixWixieCauldron::new);
        FLUID_MIX_WIXIE_CAULDRON_TILE = BLOCK_ENTITIES.register("fluid_mix_wixie_cauldron_tile", () -> BlockEntityType.Builder.of(FluidMixWixieCauldronTile::new, FLUID_MIX_WIXIE_CAULDRON.get()).build(null));
    }

    public static final RegistryObject<CreativeModeTab> SBM_TAB = TABS.register("general", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.starbunclemania"))
            .icon(ItemsRegistry.STARBUNCLE_CHARM.get()::getDefaultInstance)
            .displayItems((params, output) -> {
                for (var entry : ITEMS.getEntries()) {
                    output.accept(entry.get().getDefaultInstance());
                }
            }).withTabsBefore(CreativeTabRegistry.BLOCKS.getKey().location())
            .build());

    public static final RegistryObject<FluidType> SOURCE_FLUID_TYPE = FLUID_TYPES.register("source_fluid", SourceFluid::new);

    public static final RegistryObject<FlowingFluid> SOURCE_FLUID = FLUIDS.register("source_fluid", () ->
            new ForgeFlowingFluid.Source(fluidProperties()));
    public static final RegistryObject<Fluid> SOURCE_FLUID_FLOWING = FLUIDS.register("source_fluid_flowing", () ->
            new ForgeFlowingFluid.Flowing(fluidProperties()));
    public static final RegistryObject<LiquidBlock> SOURCE_FLUID_BLOCK = BLOCKS.register("source_fluid_block", () ->
            new LiquidBlock(SOURCE_FLUID, BlockBehaviour.Properties.copy(Blocks.WATER).noCollission().strength(100.0F).noLootTable()));
    public static final RegistryObject<Item> SOURCE_FLUID_BUCKET = ITEMS.register("source_fluid_bucket", () ->
            new BucketItem(SOURCE_FLUID, basicItemProperties().craftRemainder(Items.BUCKET).stacksTo(1)));

    static Item.Properties basicItemProperties() {
        return new Item.Properties();
    }

    private static ForgeFlowingFluid.Properties fluidProperties() {
        return new ForgeFlowingFluid.Properties(SOURCE_FLUID_TYPE, SOURCE_FLUID, SOURCE_FLUID_FLOWING)
                .block(SOURCE_FLUID_BLOCK)
                .bucket(SOURCE_FLUID_BUCKET);
    }


    @SuppressWarnings("SameParameterValue")
    static <T extends Entity> RegistryObject<EntityType<T>> addEntity(String name, float width, float height, boolean fire, boolean noSave, EntityType.EntityFactory<T> factory, MobCategory kind) {
        return ENTITIES.register(name, () -> {
            EntityType.Builder<T> builder = EntityType.Builder.of(factory, kind)
                    .setTrackingRange(32)
                    .sized(width, height);
            if (noSave) {
                builder.noSave();
            }
            if (fire) {
                builder.fireImmune();
            }
            return builder.build(StarbuncleMania.MODID + ":" + name);
        });
    }
}

