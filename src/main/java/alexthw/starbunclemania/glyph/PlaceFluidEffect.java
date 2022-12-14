package alexthw.starbunclemania.glyph;

import alexthw.starbunclemania.mixin.InventoryManagerAccessor;
import alexthw.starbunclemania.starbuncle.fluid.StarbyFluidBehavior;
import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.item.inv.*;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.items.curios.ShapersFocus;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
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
import java.util.function.Predicate;

import static alexthw.starbunclemania.StarbuncleMania.prefix;

public class PlaceFluidEffect extends AbstractEffect {

    public static final PlaceFluidEffect INSTANCE = new PlaceFluidEffect();

    public PlaceFluidEffect() {
        super(prefix("glyph_place_fluid"), "Place Fluid");
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, rayTraceResult.getBlockPos(), rayTraceResult, spellStats);
        FakePlayer fakePlayer = ANFakePlayer.getPlayer((ServerLevel) world);

        List<IFluidHandler> tanks = getTanks(world, shooter, spellContext);
        if (tanks.isEmpty()) return;

        for (BlockPos pos1 : posList) {
            if (!BlockUtil.destroyRespectsClaim(getPlayer(shooter, (ServerLevel) world), world, pos1))
                continue;
            pos1 = spellStats.getBuffCount(AugmentSensitive.INSTANCE) > 0 ? pos1 : pos1.relative(rayTraceResult.getDirection());
            if (!MinecraftForge.EVENT_BUS.post(new BlockEvent.EntityPlaceEvent(BlockSnapshot.create(world.dimension(), world, pos1), world.getBlockState(pos1), fakePlayer))) {
                this.place(pos1, world, shooter, tanks, spellContext, resolver, new BlockHitResult(new Vec3(pos1.getX(), pos1.getY(), pos1.getZ()), rayTraceResult.getDirection(), pos1, false));
            }
        }
    }

    private void place(BlockPos pPos, Level world, LivingEntity shooter, List<IFluidHandler> tanks, SpellContext spellContext, SpellResolver resolver, BlockHitResult resolveResult) {
        BlockState state = world.getBlockState(pPos);
        boolean isReplaceable = state.getMaterial().isReplaceable();
        for (IFluidHandler tank : tanks) {
            if (tank.getFluidInTank(0).isEmpty()) continue;
            //a bucket is 1000 millibuckets
            FluidStack tester = new FluidStack(tank.getFluidInTank(0), 1000);
            if (tester.getFluid() instanceof FlowingFluid ff && tank.drain(tester, IFluidHandler.FluidAction.SIMULATE).getAmount() == 1000 && (state.isAir() || isReplaceable || state.getBlock() instanceof LiquidBlockContainer)) {
                if (state.getFluidState().isSource() && state.getFluidState().getFluidType() == ff.getFluidType()) break;
                //adapted code from BucketItem
                //nether water effect
                if (world.dimensionType().ultraWarm() && tester.getFluid().is(FluidTags.WATER)) {
                    int i = pPos.getX();
                    int j = pPos.getY();
                    int k = pPos.getZ();
                    world.playSound(null, pPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);
                    for (int l = 0; l < 8; ++l) {
                        world.addParticle(ParticleTypes.LARGE_SMOKE, (double) i + Math.random(), (double) j + Math.random(), (double) k + Math.random(), 0.0D, 0.0D, 0.0D);
                    }
                } else if (state.getBlock() instanceof LiquidBlockContainer container && container.canPlaceLiquid(world, pPos, state, ff)) {
                    container.placeLiquid(world, pPos, state, ff.defaultFluidState());
                } else {
                    if (!world.isClientSide && state.getMaterial().isReplaceable() && !state.getMaterial().isLiquid()) {
                        world.destroyBlock(pPos, true);
                    }

                    if (!world.setBlock(pPos, ff.defaultFluidState().createLegacyBlock(), 11) && !state.getFluidState().isSource()) {
                        continue;
                    }
                }
                tank.drain(tester, IFluidHandler.FluidAction.EXECUTE);
                ShapersFocus.tryPropagateBlockSpell(resolveResult, world, shooter, spellContext, resolver);
                break;
            }
        }
    }

    public List<IFluidHandler> getTanks(Level world, @Nonnull LivingEntity shooter, SpellContext spellContext) {
        List<IFluidHandler> handlers = new ArrayList<>();

        //ensure it's not a real player
        if (shooter instanceof FakePlayer) {
            for (Direction side : Direction.values()) {
                BlockPos pos = shooter.getOnPos().above().relative(side);
                BlockEntity be = world.getBlockEntity(pos);
                if (be != null && be.getCapability(ForgeCapabilities.FLUID_HANDLER).isPresent()) {
                    IFluidHandler handler = StarbyFluidBehavior.getHandlerFromCap(pos, world, side.ordinal());
                    if (handler != null && !handler.getFluidInTank(0).isEmpty() && handler.getFluidInTank(0).getAmount() >= 1000) {
                        handlers.add(handler);
                    }
                }
            }
        } else {
            getTankItems(world, shooter, spellContext, handlers);
        }
        return handlers;
    }

    public static void getTankItems(Level world, @Nonnull LivingEntity shooter, SpellContext spellContext, List<IFluidHandler> handlers) {
        if (shooter instanceof Player) {
            InventoryManager manager = spellContext.getCaster().getInvManager();
            Predicate<ItemStack> predicate = (i) -> !i.isEmpty() && !(i.getItem() instanceof BucketItem) && i.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent();
            FilterableItemHandler highestHandler = ((InventoryManagerAccessor) manager).callHighestPrefInventory(manager.getInventory(), predicate, InteractType.EXTRACT);
            if (highestHandler != null){
                for (SlotReference slot : findItems(highestHandler, predicate, InteractType.EXTRACT)) {
                    ExtractedStack extractItem = ExtractedStack.from(slot,1);
                    if (!extractItem.isEmpty()) {
                        handlers.add(extractItem.stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve().get());
                        extractItem.returnOrDrop(world, shooter.getOnPos());
                    }
                }
            }
        }
    }

    public static List<SlotReference> findItems(FilterableItemHandler itemHandler, Predicate<ItemStack> stackPredicate, InteractType type){
        List<SlotReference> slots = new ArrayList<>();
        for(int slot = 0; slot < Inventory.getSelectionSize(); slot++){
            ItemStack stackInSlot = itemHandler.getHandler().getStackInSlot(slot);
            if(!stackInSlot.isEmpty() && stackPredicate.test(stackInSlot) && itemHandler.canInteractFor(stackInSlot, type)){
                slots.add(new SlotReference(itemHandler.getHandler(), slot));
            }
        }
        return slots;
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
        return augmentSetOf(AugmentSensitive.INSTANCE, AugmentAOE.INSTANCE, AugmentPierce.INSTANCE);
    }
}
