package alexthw.starbunclemania.registry;

import alexthw.starbunclemania.common.block.wixie_stations.CrucibleWixieCauldron;
import alexthw.starbunclemania.common.block.wixie_stations.CrucibleWixieCauldronTile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class EidolonCompat {


    public static DeferredHolder<Block, Block> CRUCIBLE_WIXIE_CAULDRON;

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<CrucibleWixieCauldronTile>> CRUCIBLE_WIXIE_CAULDRON_TILE;

    public static void register() {

        CRUCIBLE_WIXIE_CAULDRON = ModRegistry.BLOCKS.register("crucible_wixie_cauldron", CrucibleWixieCauldron::new);

        CRUCIBLE_WIXIE_CAULDRON_TILE = ModRegistry.BLOCK_ENTITIES.register("crucible_wixie_cauldron_tile", () -> BlockEntityType.Builder.of(CrucibleWixieCauldronTile::new, CRUCIBLE_WIXIE_CAULDRON.get()).build(null));

    }


    public static void checkWixieBlock(BlockState blockState, Level world, BlockPos pos, ServerPlayer player, CallbackInfoReturnable<InteractionResult> cir) {
//        if (blockState.getBlock() instanceof CrucibleBlock) {
//            world.setBlockAndUpdate(pos, CRUCIBLE_WIXIE_CAULDRON.get().defaultBlockState());
//            ModRegistry.WIXIE_1.trigger(player);
//            cir.setReturnValue(InteractionResult.SUCCESS);
//        }
    }


    public static void onRegisterRenders(EntityRenderersEvent.RegisterRenderers event) {
//        event.registerBlockEntityRenderer(CRUCIBLE_WIXIE_CAULDRON_TILE.get(), WixieCrucibleRenderer::new);
    }

}
