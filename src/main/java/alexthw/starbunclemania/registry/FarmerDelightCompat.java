package alexthw.starbunclemania.registry;

import alexthw.starbunclemania.common.block.wixie_stations.CuttingWixieCauldron;
import alexthw.starbunclemania.common.block.wixie_stations.CuttingWixieCauldronTile;
import alexthw.starbunclemania.common.block.wixie_stations.FarmerPotWixieCauldron;
import alexthw.starbunclemania.common.block.wixie_stations.FarmerPotWixieCauldronTile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.block.CookingPotBlock;
import vectorwing.farmersdelight.common.block.CuttingBoardBlock;

public class FarmerDelightCompat {


    public static DeferredHolder<Block,Block> CUTTING_WIXIE_CAULDRON, COOKING_POT_WIXIE_CAULDRON;

    public static DeferredHolder<BlockEntityType<?>,BlockEntityType<CuttingWixieCauldronTile>> CUTTING_WIXIE_CAULDRON_TILE;
    public static DeferredHolder<BlockEntityType<?>,BlockEntityType<FarmerPotWixieCauldronTile>> COOKING_POT_WIXIE_CAULDRON_TILE;


    public static void register() {

        COOKING_POT_WIXIE_CAULDRON = ModRegistry.BLOCKS.register("cooking_pot_wixie_cauldron", FarmerPotWixieCauldron::new);
        CUTTING_WIXIE_CAULDRON = ModRegistry.BLOCKS.register("cutting_wixie_cauldron", CuttingWixieCauldron::new);

        COOKING_POT_WIXIE_CAULDRON_TILE = ModRegistry.BLOCK_ENTITIES.register("cooking_pot_wixie_cauldron_tile", () -> BlockEntityType.Builder.of(FarmerPotWixieCauldronTile::new, COOKING_POT_WIXIE_CAULDRON.get()).build(null));
        CUTTING_WIXIE_CAULDRON_TILE = ModRegistry.BLOCK_ENTITIES.register("cutting_wixie_cauldron_tile", () -> BlockEntityType.Builder.of(CuttingWixieCauldronTile::new, CUTTING_WIXIE_CAULDRON.get()).build(null));

    }

    public static void checkWixieBlock(BlockState blockState, Level world, BlockPos pos, ServerPlayer player, CallbackInfoReturnable<InteractionResult> cir) {
        if (blockState.getBlock() instanceof CuttingBoardBlock) {
            world.setBlockAndUpdate(pos, CUTTING_WIXIE_CAULDRON.get().defaultBlockState().setValue(CuttingBoardBlock.FACING, blockState.getValue(CuttingBoardBlock.FACING)));
            ModRegistry.WIXIE_2.get().trigger(player);
            cir.setReturnValue(InteractionResult.SUCCESS);
        } else if (blockState.getBlock() instanceof CookingPotBlock) {
            world.setBlockAndUpdate(pos, COOKING_POT_WIXIE_CAULDRON.get().defaultBlockState().setValue(CookingPotBlock.FACING, blockState.getValue(CookingPotBlock.FACING)).setValue(CookingPotBlock.SUPPORT, blockState.getValue(CookingPotBlock.SUPPORT)));
            ModRegistry.WIXIE_1.get().trigger(player);
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}
