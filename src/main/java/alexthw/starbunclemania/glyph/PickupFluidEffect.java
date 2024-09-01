package alexthw.starbunclemania.glyph;

import alexthw.starbunclemania.starbuncle.fluid.StarbyFluidBehavior;
import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.TileCaster;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import com.hollingsworth.arsnouveau.common.block.tile.RuneTile;
import com.hollingsworth.arsnouveau.common.items.curios.ShapersFocus;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

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
        if (rayTraceResult.getEntity() instanceof Cow cow && !cow.isBaby() && !cow.hasEffect(MobEffects.HERO_OF_THE_VILLAGE)) {
            var tanks = getTanks(world, spellContext);
            pickupCow(tanks, cow);
            for (IFluidHandler tank : tanks)
                if (tank instanceof WrappedExtractedItemHandler wrap)
                    wrap.extractedStack.returnOrDrop(world, shooter.getOnPos());
        } else {
            onResolveBlock(new BlockHitResult(rayTraceResult.getLocation(), Direction.UP, rayTraceResult.getEntity().getOnPos(), true), world, shooter, spellStats, spellContext, resolver);
        }
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        BlockPos adjustedPos = rayTraceResult.getBlockPos();
        if (!(world.getFluidState(adjustedPos).isSource() || world.getBlockState(adjustedPos).getBlock() instanceof AbstractCauldronBlock || world.getBlockEntity(adjustedPos) instanceof MobJarTile)) {
            adjustedPos = adjustedPos.relative(rayTraceResult.getDirection());
        }
        List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, adjustedPos, rayTraceResult, spellStats);
        FakePlayer fakePlayer = ANFakePlayer.getPlayer((ServerLevel) world);

        List<IFluidHandler> tanks = getTanks(world, spellContext);
        if (tanks.isEmpty()) return;

        for (BlockPos pos1 : posList) {
            BlockState state = world.getBlockState(pos1);
            if (!NeoForge.EVENT_BUS.post(new BlockEvent.EntityPlaceEvent(BlockSnapshot.create(world.dimension(), world, pos1), world.getBlockState(pos1), fakePlayer)).isCanceled()) {
                if (state.getBlock() instanceof BucketPickup bp) {
                    this.pickup(pos1, (ServerLevel) world, shooter, tanks, bp, resolver, spellContext, new BlockHitResult(new Vec3(pos1.getX(), pos1.getY(), pos1.getZ()), rayTraceResult.getDirection(), pos1, false));
                } else if (!state.hasBlockEntity() && (state.getBlock() == Blocks.WATER_CAULDRON || state.getBlock() == Blocks.LAVA_CAULDRON)) {
                    this.pickupCauldron(pos1, world, shooter, tanks, resolver, spellContext, new BlockHitResult(new Vec3(pos1.getX(), pos1.getY(), pos1.getZ()), rayTraceResult.getDirection(), pos1, false));
                }
            }
        }
        for (var tank : tanks) {
            if (tank instanceof WrappedExtractedItemHandler wrap) {
                wrap.extractedStack.returnOrDrop(world, shooter.getOnPos());
            }
        }

    }

    private void pickupCauldron(BlockPos pPos, Level world, LivingEntity shooter, List<IFluidHandler> tanks, SpellResolver resolver, SpellContext spellContext, BlockHitResult resolveResult) {
        Fluid fluid = world.getBlockState(pPos).getBlock() == Blocks.WATER_CAULDRON ? Fluids.WATER : Fluids.LAVA;
        for (IFluidHandler tank : tanks) {
            //a bucket is 1000 millibuckets
            FluidStack tester = new FluidStack(fluid, 1000);
            if (tank.fill(tester, IFluidHandler.FluidAction.SIMULATE) == 1000) {
                world.setBlockAndUpdate(pPos, Blocks.CAULDRON.defaultBlockState());
                tank.fill(tester, IFluidHandler.FluidAction.EXECUTE);
                if (tank instanceof WrappedExtractedItemHandler wrap) wrap.updateContainer();
                ShapersFocus.tryPropagateBlockSpell(resolveResult, world, shooter, spellContext, resolver);
                break;
            }
        }
    }

    private void pickupCow(List<IFluidHandler> tanks, Cow cow) {
        for (IFluidHandler tank : tanks) {
            //a bucket is 1000 millibuckets
            FluidStack tester = new FluidStack(NeoForgeMod.MILK.get(), 1000);
            if (tank.fill(tester, IFluidHandler.FluidAction.SIMULATE) == 1000) {
                tank.fill(tester, IFluidHandler.FluidAction.EXECUTE);
                cow.addEffect(new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, 20, 1, false, false, false));
                break;
            }
        }
    }

    private void pickup(BlockPos pPos, ServerLevel world, LivingEntity shooter, List<IFluidHandler> tanks, BucketPickup bp, SpellResolver resolver, SpellContext spellContext, BlockHitResult resolveResult) {
        FluidState fluidState = world.getFluidState(pPos);
        for (IFluidHandler tank : tanks) {
            //a bucket is 1000 millibuckets
            FluidStack tester = new FluidStack(fluidState.getType(), 1000);
            if (tank.fill(tester, IFluidHandler.FluidAction.SIMULATE) == 1000 && fluidState.isSource()) {
                bp.pickupBlock(getPlayer(shooter, world), world, pPos, world.getBlockState(pPos));
                tank.fill(tester, IFluidHandler.FluidAction.EXECUTE);
                if (tank instanceof WrappedExtractedItemHandler wrap) wrap.updateContainer();
                ShapersFocus.tryPropagateBlockSpell(resolveResult, world, shooter, spellContext, resolver);
                break;
            }
        }
    }

    public List<IFluidHandler> getTanks(Level world, SpellContext spellContext) {
        List<IFluidHandler> handlers = new ArrayList<>();

        //check nearby inventories if it's a turret or similar
        if (spellContext.getCaster() instanceof TileCaster tile && !(tile.getTile() instanceof RuneTile rune && rune.isSensitive)) {
            BlockPos tilePos = tile.getTile().getBlockPos();
            for (Direction side : Direction.values()) {
                BlockPos pos = tilePos.relative(side);
                if (world.getCapability(Capabilities.FluidHandler.BLOCK, pos, side) != null) {
                    IFluidHandler handler = StarbyFluidBehavior.getHandlerFromCap(pos, world, side);
                    if (handler != null && (handler.getFluidInTank(0).isEmpty() || handler.getFluidInTank(0).getAmount() <= handler.getTankCapacity(0) - 1000)) {
                        handlers.add(handler);
                    }
                }
            }
        }
        PlaceFluidEffect.getTankItems(spellContext, handlers);
        return handlers;
    }

    @Override
    public int getDefaultManaCost() {
        return 80;
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }

    @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION, SpellSchools.ELEMENTAL_WATER);
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAOE.INSTANCE, AugmentPierce.INSTANCE);
    }

}
