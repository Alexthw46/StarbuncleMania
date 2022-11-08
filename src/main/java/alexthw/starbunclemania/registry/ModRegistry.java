package alexthw.starbunclemania.registry;

import alexthw.starbunclemania.StarbuncleMania;
import alexthw.starbunclemania.common.*;
import alexthw.starbunclemania.common.block.LiquidJarBlock;
import alexthw.starbunclemania.common.block.LiquidJarTile;
import alexthw.starbunclemania.common.item.*;
import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("Convert2MethodRef")
public class ModRegistry {

    private static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, StarbuncleMania.MODID);
    private static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, StarbuncleMania.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, StarbuncleMania.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, StarbuncleMania.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, StarbuncleMania.MODID);


    public static void registerRegistries(IEventBus bus){
        BLOCKS.register(bus);
        ITEMS.register(bus);
        FLUID_TYPES.register(bus);
        FLUIDS.register(bus);
        BLOCK_ENTITIES.register(bus);
    }

    public static final RegistryObject<Item> EXAMPLE;
    public static final RegistryObject<Item> PROFHAT;
    public static final RegistryObject<Item> STARBATTERY;
    public static final RegistryObject<Item> STARBUCKET;
    public static final RegistryObject<Item> DIRECTION_SCROLL;


    public static final RegistryObject<Block> FLUID_JAR;
    public static final RegistryObject<BlockEntityType<LiquidJarTile>> FLUID_JAR_TILE;

    static {

        EXAMPLE = ITEMS.register("star_hat", () -> new ExampleCosmetic(new Item.Properties().tab(ArsNouveau.itemGroup)));
        PROFHAT = ITEMS.register("prof_hat", () -> new ProfHat(new Item.Properties().tab(ArsNouveau.itemGroup)));
        STARBATTERY = ITEMS.register("star_battery", () -> new StarbAABattery(new Item.Properties().tab(ArsNouveau.itemGroup)));
        STARBUCKET = ITEMS.register("star_bucket", () -> new StarBucket(new Item.Properties().tab(ArsNouveau.itemGroup)));
        DIRECTION_SCROLL = ITEMS.register("direction_scroll", () -> new DirectionScroll());

        FLUID_JAR = BLOCKS.register("fluid_jar", () -> new LiquidJarBlock());
        ITEMS.register("fluid_jar", () -> new FluidJarItem(FLUID_JAR.get(), new Item.Properties().tab(ArsNouveau.itemGroup)));
        FLUID_JAR_TILE = BLOCK_ENTITIES.register("fluid_jar_tile", () -> BlockEntityType.Builder.of(LiquidJarTile::new, FLUID_JAR.get()).build(null));

    }

    public static final RegistryObject<FluidType> SOURCE_FLUID_TYPE = FLUID_TYPES.register("source_fluid", SourceFluid::new);

    public static final RegistryObject<FlowingFluid> SOURCE_FLUID = FLUIDS.register("source_fluid", () ->
            new ForgeFlowingFluid.Source(fluidProperties()));
    public static final RegistryObject<Fluid> SOURCE_FLUID_FLOWING = FLUIDS.register("source_fluid_flowing", () ->
            new ForgeFlowingFluid.Flowing(fluidProperties()));
    public static final RegistryObject<LiquidBlock> SOURCE_FLUID_BLOCK = BLOCKS.register("source_fluid_block", () ->
            new LiquidBlock(SOURCE_FLUID, BlockBehaviour.Properties.of(Material.WATER).noCollission().strength(100.0F).noLootTable()));
    public static final RegistryObject<Item> SOURCE_FLUID_BUCKET = ITEMS.register("source_fluid_bucket", () ->
            new BucketItem(SOURCE_FLUID, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(ArsNouveau.itemGroup)));

    private static ForgeFlowingFluid.Properties fluidProperties()
    {
        return new ForgeFlowingFluid.Properties(SOURCE_FLUID_TYPE, SOURCE_FLUID, SOURCE_FLUID_FLOWING)
                .block(SOURCE_FLUID_BLOCK)
                .bucket(SOURCE_FLUID_BUCKET);
    }
}
