package alexthw.starbunclemania.glyph;

import alexthw.starbunclemania.starbuncle.fluid.StarbyFluidBehavior;
import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.items.curios.ShapersFocus;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static alexthw.starbunclemania.StarbuncleMania.prefix;

public class PickupFluidEffect extends AbstractEffect {

    public static final PickupFluidEffect INSTANCE = new PickupFluidEffect();

    public PickupFluidEffect() {
        super(prefix("glyph_pickup_fluid"), "Pickup Fluid");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (rayTraceResult.getEntity() instanceof Cow cow && !cow.isBaby()){
            List<IFluidHandler> tanks = getTanks(world, shooter, spellContext);
            for (IFluidHandler tank : tanks) {
                //a bucket is 1000 millibuckets
                FluidStack tester = new FluidStack(ForgeMod.MILK.get(), 1000);
                if (tank.fill(tester, IFluidHandler.FluidAction.SIMULATE) == 1000) {
                    tank.fill(tester, IFluidHandler.FluidAction.EXECUTE);
                    break;
                }
            }
        }else {
            onResolveBlock(new BlockHitResult(rayTraceResult.getLocation(), Direction.UP, rayTraceResult.getEntity().getOnPos(),true), world, shooter, spellStats, spellContext, resolver);
        }
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        BlockPos adjustedPos = rayTraceResult.getBlockPos();
        if (!world.getFluidState(adjustedPos).isSource()){
            adjustedPos = adjustedPos.relative(rayTraceResult.getDirection());
        }
        List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, adjustedPos, rayTraceResult, spellStats);
        FakePlayer fakePlayer = ANFakePlayer.getPlayer((ServerLevel) world);

        List<IFluidHandler> tanks = getTanks(world, shooter, spellContext);
        if (tanks.isEmpty()) return;

        for (BlockPos pos1 : posList) {
            BlockState state = world.getBlockState(pos1);
            if (state.getBlock() instanceof BucketPickup bp && !MinecraftForge.EVENT_BUS.post(new BlockEvent.EntityPlaceEvent(BlockSnapshot.create(world.dimension(), world, pos1), world.getBlockState(pos1), fakePlayer))) {
                this.pickup(pos1, world, shooter, tanks, bp, resolver, spellContext, new BlockHitResult(new Vec3(pos1.getX(), pos1.getY(), pos1.getZ()), rayTraceResult.getDirection(), pos1, false));
            }
        }
    }

    private void pickup(BlockPos pPos, Level world, LivingEntity shooter, List<IFluidHandler> tanks, BucketPickup bp, SpellResolver resolver, SpellContext spellContext, BlockHitResult resolveResult) {
        FluidState fluidState = world.getFluidState(pPos);
        for (IFluidHandler tank : tanks) {
            //a bucket is 1000 millibuckets
            FluidStack tester = new FluidStack(fluidState.getType(), 1000);
            if (tank.fill(tester, IFluidHandler.FluidAction.SIMULATE) == 1000 && fluidState.isSource()) {
                bp.pickupBlock(world, pPos, world.getBlockState(pPos));
                tank.fill(tester, IFluidHandler.FluidAction.EXECUTE);
                ShapersFocus.tryPropagateBlockSpell(resolveResult, world, shooter, spellContext, resolver);
                break;
            }
        }
    }

    public List<IFluidHandler> getTanks(Level world, @Nonnull LivingEntity shooter, SpellContext spellContext) {
        List<IFluidHandler> handlers = new ArrayList<>();

        //ensure it's a turret or similar
        if (shooter instanceof FakePlayer) {
            for (Direction side : Direction.values()) {
                BlockPos pos = shooter.getOnPos().above().relative(side);
                BlockEntity be = world.getBlockEntity(pos);
                if (be != null && be.getCapability(ForgeCapabilities.FLUID_HANDLER).isPresent()) {
                    IFluidHandler handler = StarbyFluidBehavior.getHandlerFromCap(pos, world, side.ordinal());
                    if (handler != null && (handler.getFluidInTank(0).isEmpty() || handler.getFluidInTank(0).getAmount() <= handler.getTankCapacity(0) - 1000)) {
                        handlers.add(handler);
                    }
                }
            }
        } else PlaceFluidEffect.getTankItems(world, shooter, spellContext, handlers);
        return handlers;
    }

    @Override
    public int getDefaultManaCost() {
        return 20;
    }

    @Override
    public SpellTier getTier() {
        return SpellTier.TWO;
    }

    @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAOE.INSTANCE, AugmentPierce.INSTANCE);
    }

}
