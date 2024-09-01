package alexthw.starbunclemania.registry;

import alexthw.starbunclemania.StarbuncleMania;
import alexthw.starbunclemania.client.FluidSourceLinkRenderer;
import alexthw.starbunclemania.client.SourceCondenserRenderer;
import alexthw.starbunclemania.common.StarbyMountEntity;
import alexthw.starbunclemania.common.block.fluids.*;
import alexthw.starbunclemania.common.block.wixie_stations.SmeltingWixieCauldron;
import alexthw.starbunclemania.common.block.wixie_stations.SmeltingWixieCauldronTile;
import alexthw.starbunclemania.common.block.wixie_stations.StonecutterWixieCauldron;
import alexthw.starbunclemania.common.block.wixie_stations.StonecutterWixieCauldronTile;
import alexthw.starbunclemania.common.data.DirectionData;
import alexthw.starbunclemania.common.data.FluidScrollData;
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
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

import static alexthw.starbunclemania.StarbuncleMania.prefix;

@SuppressWarnings({"Convert2MethodRef", "ConstantConditions", "SpellCheckingInspection"})
public class ModRegistry {

    private static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, StarbuncleMania.MODID);
    private static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, StarbuncleMania.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.createItems(StarbuncleMania.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.createBlocks(StarbuncleMania.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, StarbuncleMania.MODID);
    public static final DeferredRegister<DataComponentType<?>> D_COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, StarbuncleMania.MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, StarbuncleMania.MODID);
    public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS = DeferredRegister.create(BuiltInRegistries.TRIGGER_TYPES, StarbuncleMania.MODID);

    // Maybe switch source conversion to recipes, currently in configs
    public static final DeferredRegister<RecipeType<?>> RECIPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, StarbuncleMania.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> R_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, StarbuncleMania.MODID);

    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, StarbuncleMania.MODID);
    public static final TagKey<Fluid> POTION = FluidTags.create(ResourceLocation.fromNamespaceAndPath("c", "potion"));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<DirectionData>> DIRECTION = D_COMPONENTS.register("direction", () -> DataComponentType.<DirectionData>builder().persistent(DirectionData.CODEC).networkSynchronized(DirectionData.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FluidScrollData>> FLUID_SCROLL = D_COMPONENTS.register("fluid_scroll", () -> DataComponentType.<FluidScrollData>builder().persistent(FluidScrollData.CODEC).networkSynchronized(FluidScrollData.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SimpleFluidContent>> FLUID_CONTENT = D_COMPONENTS.register("fluid_content", () -> DataComponentType.<SimpleFluidContent>builder().persistent(SimpleFluidContent.CODEC).networkSynchronized(SimpleFluidContent.STREAM_CODEC).build());

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
        D_COMPONENTS.register(bus);
        TRIGGERS.register(bus);
        bus.addListener(ModRegistry::registerEntityAttributes);
        bus.addListener(ModRegistry::editEntityAttributes);
        if (ModList.get().isLoaded("mekanism")) {
            MekanismCompat.register(bus);
        }
        if (ModList.get().isLoaded("farmersdelight")) {
            FarmerDelightCompat.register();
        }
        if (ModList.get().isLoaded("eidolon")) {
            EidolonCompat.register();
        }
    }

    public static void registerEntityAttributes(final EntityAttributeCreationEvent event) {
        event.put(STARBY_MOUNT.get(), Starbuncle.attributes().add(Attributes.MAX_HEALTH, 20).build());
    }

    public static void editEntityAttributes(final EntityAttributeModificationEvent event) {
        event.add(ModEntities.STARBUNCLE_TYPE.get(), Attributes.ATTACK_DAMAGE, 2);
        event.add(ModEntities.STARBUNCLE_TYPE.get(), Attributes.FOLLOW_RANGE, 5);
    }

    public static final DeferredHolder<RecipeType<?>, RecipeType<FluidSourcelinkRecipe>> FLUID_SOURCELINK_RT;
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FluidSourcelinkRecipe>> FLUID_SOURCELINK_RS;

    static {
        FLUID_SOURCELINK_RT = RECIPES.register("fluid_sourcelink", () -> RecipeType.simple(prefix("fluid_sourcelink")));
        FLUID_SOURCELINK_RS = R_SERIALIZERS.register("fluid_sourcelink", FluidSourcelinkRecipe.Serializer::new);
    }

    public static final DeferredHolder<Item, Item> DIRECTION_SCROLL;
    public static final DeferredHolder<Item, Item> FLUID_SCROLL_A;
    public static final DeferredHolder<Item, Item> FLUID_SCROLL_D;

    public static final DeferredHolder<Item, Item> STARHAT;
    public static final DeferredHolder<Item, Item> PROFHAT;
    public static final DeferredHolder<Item, Item> STARBATTERY;
    public static final DeferredHolder<Item, Item> STARBUCKET;
    public static final DeferredHolder<Item, Item> STARBALLON;
    public static final DeferredHolder<Item, Item> STARTRASH;
    public static final DeferredHolder<Item, Item> STARSWORD;
    public static final DeferredHolder<Item, Item> STARWAND;
    public static final DeferredHolder<Item, Item> STARSADDLE;


    public static final DeferredHolder<Block, Block> FLUID_JAR;
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LiquidJarTile>> FLUID_JAR_TILE;
    public static final DeferredHolder<Block, Block> SOURCE_CONDENSER;
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SourceCondenserTile>> SOURCE_CONDENSER_TILE;
    public static final DeferredHolder<Block, Block> FLUID_SOURCELINK;
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FluidSourcelinkTile>> FLUID_SOURCELINK_TILE;

    public static final DeferredHolder<Block, Block> SMELTING_WIXIE_CAULDRON;
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SmeltingWixieCauldronTile>> SMELTING_WIXIE_CAULDRON_TILE;
    public static final DeferredHolder<Block, Block> STONEWORK_WIXIE_CAULDRON;
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<StonecutterWixieCauldronTile>> STONECUTTER_WIXIE_CAULDRON_TILE;

    public static final DeferredHolder<EntityType<?>, EntityType<StarbyMountEntity>> STARBY_MOUNT;

    public static final DeferredHolder<CriterionTrigger<?>, PlayerTrigger> WIXIE_1 = TRIGGERS.register("wixie_cook", () -> new PlayerTrigger());
    public static final DeferredHolder<CriterionTrigger<?>, PlayerTrigger> WIXIE_2 = TRIGGERS.register("wixie_stone", () -> new PlayerTrigger());

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
                FluidScrollData filter = fluidScroll.get(FLUID_SCROLL);
                if (filter == null) {
                    return false;
                }
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

    }

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SBM_TAB = TABS.register("general", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.starbunclemania"))
            .icon(ItemsRegistry.STARBUNCLE_CHARM.get()::getDefaultInstance)
            .displayItems((params, output) -> {
                for (var entry : ITEMS.getEntries()) {
                    output.accept(entry.get().getDefaultInstance());
                }
            }).withTabsBefore(CreativeTabRegistry.BLOCKS.getKey().location())
            .build());

    public static final DeferredHolder<FluidType, FluidType> SOURCE_FLUID_TYPE = FLUID_TYPES.register("source_fluid", SourceFluid::new);

    public static final DeferredHolder<Fluid, Fluid> SOURCE_FLUID = FLUIDS.register("source_fluid", () ->
            new BaseFlowingFluid.Source(fluidProperties()));
    public static final DeferredHolder<Fluid, FlowingFluid> SOURCE_FLUID_FLOWING = FLUIDS.register("source_fluid_flowing", () ->
            new BaseFlowingFluid.Flowing(fluidProperties()));
    public static final DeferredHolder<Block, LiquidBlock> SOURCE_FLUID_BLOCK = BLOCKS.register("source_fluid_block", () ->
            new LiquidBlock(SOURCE_FLUID_FLOWING.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noCollission().strength(100.0F).noLootTable()));
    public static final DeferredHolder<Item, Item> SOURCE_FLUID_BUCKET = ITEMS.register("source_fluid_bucket", () ->
            new BucketItem(SOURCE_FLUID.get(), basicItemProperties().craftRemainder(Items.BUCKET).stacksTo(1)));

    static Item.Properties basicItemProperties() {
        return new Item.Properties();
    }

    private static BaseFlowingFluid.Properties fluidProperties() {
        return new BaseFlowingFluid.Properties(SOURCE_FLUID_TYPE, SOURCE_FLUID, SOURCE_FLUID_FLOWING)
                .block(SOURCE_FLUID_BLOCK)
                .bucket(SOURCE_FLUID_BUCKET);
    }


    @SuppressWarnings("SameParameterValue")
    static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> addEntity(String name, float width, float height, boolean fire, boolean noSave, EntityType.EntityFactory<T> factory, MobCategory kind) {
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

