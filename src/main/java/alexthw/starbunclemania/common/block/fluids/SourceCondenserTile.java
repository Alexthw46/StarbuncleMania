package alexthw.starbunclemania.common.block.fluids;

import alexthw.starbunclemania.Configs;
import alexthw.starbunclemania.registry.ModRegistry;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import static net.minecraftforge.common.capabilities.ForgeCapabilities.FLUID_HANDLER;

public class SourceCondenserTile extends AbstractTankTile implements GeoBlockEntity, ITickable {

    public SourceCondenserTile(BlockPos pos, BlockState state) {
        super(ModRegistry.SOURCE_CONDENSER_TILE.get(), pos, state);
        tank.setValidator((stack) -> stack.getFluid().getFluidType() == ModRegistry.SOURCE_FLUID_TYPE.get());
    }

    public static final FluidStack tester = new FluidStack(ModRegistry.SOURCE_FLUID.get(), 1000);

    public float getFluidPercentage() {
        return (float) super.getFluidAmount() / capacity;
    }

    @Override
    public void tick() {
        if (level != null && !level.isClientSide() && level.getGameTime() % 40 == 0) {
            if (this.tank.fill(tester, IFluidHandler.FluidAction.SIMULATE) == 1000) {
                if (SourceUtil.takeSourceWithParticles(getBlockPos(), level, 6, Configs.SOURCE_TO_FLUID.get()) != null) {
                    this.tank.fill(tester, IFluidHandler.FluidAction.EXECUTE);
                }
            }
            if (!this.tank.isEmpty() && this.tank.getFluidAmount() >= 1000) {
                BlockEntity be = level.getBlockEntity(this.getBlockPos().below());
                if (be != null && be.getCapability(ForgeCapabilities.FLUID_HANDLER, Direction.UP).isPresent()) {
                    IFluidHandler handler = be.getCapability(FLUID_HANDLER, Direction.UP).resolve().isPresent() ? be.getCapability(FLUID_HANDLER, Direction.UP).resolve().get() : null;
                    if (handler != null && handler.fill(tester, IFluidHandler.FluidAction.SIMULATE) > 100) {
                        int drain = handler.fill(tester, IFluidHandler.FluidAction.EXECUTE);
                        this.tank.drain(drain, IFluidHandler.FluidAction.EXECUTE);
                    }
                }
            }
            updateBlock();
        }
    }

    AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "rotate_controller", 0, e -> e.setAndContinue(RawAnimation.begin().thenLoop("floating"))));
        data.add(new AnimationController<>(this, "float_controller", 0, e -> e.setAndContinue(RawAnimation.begin().thenLoop("rotation"))));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }
}