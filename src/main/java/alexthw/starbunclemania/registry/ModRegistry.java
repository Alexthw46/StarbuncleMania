package alexthw.starbunclemania.registry;

import alexthw.starbunclemania.StarbuncleMania;
import alexthw.starbunclemania.client.FluidSourceLinkRenderer;
import alexthw.starbunclemania.client.SourceCondenserRenderer;
import alexthw.starbunclemania.common.item.cosmetic.*;
import alexthw.starbunclemania.common.StarbyMountEntity;
import alexthw.starbunclemania.common.block.*;
import alexthw.starbunclemania.common.item.*;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.items.RendererBlockItem;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static alexthw.starbunclemania.StarbuncleMania.TAB;

@SuppressWarnings({"Convert2MethodRef", "ConstantConditions"})
public class ModRegistry {

    private static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, StarbuncleMania.MODID);
    private static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, StarbuncleMania.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, StarbuncleMania.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, StarbuncleMania.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, StarbuncleMania.MODID);

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, StarbuncleMania.MODID);

    // Maybe switch source conversion to recipes, currently in configs
    //public static final DeferredRegister<RecipeType<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, StarbuncleMania.MODID);
    //public static final DeferredRegister<RecipeSerializer<?>> R_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, StarbuncleMania.MODID);

    public static final TagKey<Fluid> POTION = FluidTags.create(new ResourceLocation("forge", "potion"));

    public static void registerRegistries(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        FLUID_TYPES.register(bus);
        FLUIDS.register(bus);
        BLOCK_ENTITIES.register(bus);
        ENTITIES.register(bus);
        bus.addListener(ModRegistry::registerEntityAttributes);
        if (ModList.get().isLoaded("mekanism")){
            MekanismCompat.register(bus);
        }
    }

    public static void registerEntityAttributes(final EntityAttributeCreationEvent event) {
        event.put(STARBY_MOUNT.get(), Starbuncle.attributes().build());
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

    public static final RegistryObject<EntityType<StarbyMountEntity>> STARBY_MOUNT;

    static {

        STARHAT = ITEMS.register("star_hat", () -> new ExampleCosmetic(basicItemProperties()));
        PROFHAT = ITEMS.register("wyrm_degree", () -> new ProfHat(basicItemProperties()));
        STARBATTERY = ITEMS.register("star_battery", () -> new StarbAABattery(basicItemProperties()));
        STARBUCKET = ITEMS.register("star_bucket", () -> new StarBucket(basicItemProperties()));
        STARBALLON = ITEMS.register("star_balloon", () -> new StarBalloon(basicItemProperties()));
        STARTRASH = ITEMS.register("star_bin", () -> new StarBin(basicItemProperties()));
        STARSWORD = ITEMS.register("star_sword", () -> new StarSword(new Item.Properties()));
        STARWAND = ITEMS.register("star_wand", () -> new StarWand(new Item.Properties()));

        DIRECTION_SCROLL = ITEMS.register("direction_scroll", () -> new DirectionScroll(basicItemProperties()));
        FLUID_SCROLL_A = ITEMS.register("fluid_scroll_allow", () -> new FluidScroll(basicItemProperties()));
        FLUID_SCROLL_D = ITEMS.register("fluid_scroll_deny", () -> new FluidScroll(basicItemProperties()){
            @Override
            public boolean isDenied(ItemStack fluidScroll, FluidStack fluidInTank) {
                FluidData filter = new FluidData(fluidScroll);
                return filter.containsStack(fluidInTank);
            }
        });

        STARSADDLE = ITEMS.register("star_saddle", () -> new StarbySaddle(basicItemProperties()));

        FLUID_JAR = BLOCKS.register("fluid_jar", () -> new LiquidJarBlock());
        ITEMS.register("fluid_jar", () -> new FluidJarItem(FLUID_JAR.get(), basicItemProperties()));
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

        STARBY_MOUNT = addEntity("starby_mount", 2, 2, true, false, (entityCarbuncleEntityType, world) -> new StarbyMountEntity(world), MobCategory.CREATURE);

    }

    public static final RegistryObject<FluidType> SOURCE_FLUID_TYPE = FLUID_TYPES.register("source_fluid", SourceFluid::new);

    public static final RegistryObject<FlowingFluid> SOURCE_FLUID = FLUIDS.register("source_fluid", () ->
            new ForgeFlowingFluid.Source(fluidProperties()));
    public static final RegistryObject<Fluid> SOURCE_FLUID_FLOWING = FLUIDS.register("source_fluid_flowing", () ->
            new ForgeFlowingFluid.Flowing(fluidProperties()));
    public static final RegistryObject<LiquidBlock> SOURCE_FLUID_BLOCK = BLOCKS.register("source_fluid_block", () ->
            new LiquidBlock(SOURCE_FLUID, BlockBehaviour.Properties.of(Material.WATER).noCollission().strength(100.0F).noLootTable()));
    public static final RegistryObject<Item> SOURCE_FLUID_BUCKET = ITEMS.register("source_fluid_bucket", () ->
            new BucketItem(SOURCE_FLUID, basicItemProperties().craftRemainder(Items.BUCKET).stacksTo(1)));

    static Item.Properties basicItemProperties() {
        return new Item.Properties().tab(TAB);
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

