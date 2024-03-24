package alexthw.starbunclemania.mixin;

import alexthw.starbunclemania.registry.EidolonCompat;
import alexthw.starbunclemania.registry.FarmerDelightCompat;
import alexthw.starbunclemania.registry.ModRegistry;
import com.hollingsworth.arsnouveau.common.items.summon_charms.WixieCharm;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.StonecutterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WixieCharm.class)
public class WixieCharmMixin {


    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true, remap = false)
    public void useOnBlock(UseOnContext context, @NotNull Level world, BlockPos pos, CallbackInfoReturnable<InteractionResult> cir) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.getBlock() instanceof FurnaceBlock) {
            world.setBlockAndUpdate(pos, ModRegistry.SMELTING_WIXIE_CAULDRON.get().defaultBlockState().setValue(FurnaceBlock.FACING, blockState.getValue(FurnaceBlock.FACING)));
            cir.setReturnValue(InteractionResult.SUCCESS);
        } else if (blockState.getBlock() instanceof StonecutterBlock) {
            world.setBlockAndUpdate(pos, ModRegistry.STONEWORK_WIXIE_CAULDRON.get().defaultBlockState().setValue(StonecutterBlock.FACING, blockState.getValue(StonecutterBlock.FACING)));
            cir.setReturnValue(InteractionResult.SUCCESS);
        } else {
            if (ModList.get().isLoaded("farmersdelight")) {
                FarmerDelightCompat.checkWixieBlock(blockState, world, pos, cir);
            }
            if (ModList.get().isLoaded("eidolon")) {
                EidolonCompat.checkWixieBlock(blockState, world, pos, cir);
            }
        }
    }

}
