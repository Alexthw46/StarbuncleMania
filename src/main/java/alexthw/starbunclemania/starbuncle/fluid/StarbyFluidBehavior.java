package alexthw.starbunclemania.starbuncle.fluid;

import alexthw.starbunclemania.Configs;
import alexthw.starbunclemania.StarbuncleMania;
import alexthw.starbunclemania.common.block.fluids.LiquidJarTile;
import alexthw.starbunclemania.common.item.FluidScroll;
import alexthw.starbunclemania.registry.ModRegistry;
import alexthw.starbunclemania.starbuncle.StarHelper;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.StarbyListBehavior;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.BLOCK;


public class StarbyFluidBehavior extends StarbyListBehavior {

    public static final ResourceLocation TRANSPORT_ID = ResourceLocation.fromNamespaceAndPath(StarbuncleMania.MODID, "starby_fluid_transport");

    private @NotNull FluidStack fluidStack = FluidStack.EMPTY;

    public ItemStack fluidScroll;

    public StarbyFluidBehavior(Starbuncle entity, CompoundTag tag) {
        super(entity, tag);
        if (tag.contains("FluidName"))
            fluidStack = FluidStack.parseOptional(entity.level().registryAccess(), tag);

        if (tag.contains("fluidScroll"))
            this.fluidScroll = ItemStack.parseOptional(entity.level().registryAccess(), tag.getCompound("fluidScroll"));
        goals.add(new WrappedGoal(3, new FluidStoreGoal(entity, this)));
        goals.add(new WrappedGoal(3, new FluidExtractGoal(entity, this)));
    }

    @Override
    public boolean canGoToBed() {
        return isBedPowered() || (getTankToExtract() == null && (getFluidStack().isEmpty() || getTankForStorage() == null));
    }

    public @NotNull FluidStack getFluidStack() {
        return fluidStack;
    }

    public void setFluidStack(FluidStack fluid) {
        fluidStack = fluid;
        syncTag();
    }

    @Override
    public void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable Direction side, @Nullable LivingEntity storedEntity, Player playerEntity) {
        super.onFinishedConnectionFirst(storedPos, side, storedEntity, playerEntity);
        if (storedPos != null) {
            if (level.getCapability(BLOCK, storedPos, side) != null) {
                addToPos(storedPos);
                syncTag();
                PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.starbuncle.fluid_to"));
            }
        }
    }

    @Override
    public void onFinishedConnectionLast(@Nullable BlockPos storedPos, @Nullable Direction side, @Nullable LivingEntity storedEntity, Player playerEntity) {
        super.onFinishedConnectionLast(storedPos, side, storedEntity, playerEntity);
        if (storedPos != null) {
            if (level.getCapability(BLOCK, storedPos, side) != null) {
                addFromPos(storedPos);
                syncTag();
                PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.starbuncle.fluid_from"));
            }
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof FluidScroll && !stack.isComponentsPatchEmpty()) {
            fluidScroll = stack;
            PortUtil.sendMessage(player, Component.translatable("ars_nouveau.filter_set"));
            syncTag();
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void getTooltip(Consumer<Component> tooltip) {
        super.getTooltip(tooltip);
        tooltip.accept(Component.translatable("ars_nouveau.starbuncle.storing_fluid", TO_LIST.size()));
        tooltip.accept(Component.translatable("ars_nouveau.starbuncle.taking_fluid", FROM_LIST.size()));
        if (!fluidStack.isEmpty()) {
            LiquidJarTile.displayFluidTooltip(tooltip, getFluidStack());
        }
        if (fluidScroll != null && !fluidScroll.isEmpty()) {
            tooltip.accept(Component.translatable("ars_nouveau.filtering_with", fluidScroll.getHoverName().getString()));
        }
    }

    public int getRatio() {
        return Configs.STARBUCKET_RATIO.get();
    }

    @Override
    public ResourceLocation getRegistryName() {
        return TRANSPORT_ID;
    }

    public BlockPos getTankForStorage(FluidStack fluidInTank) {
        if (!fluidInTank.isEmpty()) {
            for (BlockPos pos : TO_LIST) {
                if (level.isLoaded(pos) && canStore(pos, fluidInTank)) {
                    return pos;
                }
            }
        }
        return null;
    }

    public BlockPos getTankForStorage() {
        return getTankForStorage(getFluidStack());
    }

    public BlockPos getTankToExtract() {
        for (BlockPos pos : FROM_LIST) {
            if (level.isLoaded(pos) && canExtract(pos)) {
                return pos;
            }
        }

        return null;
    }

    /**
     * Yeah, I don't like writing the little isPresent-resolve-isPresent-get everytime
     *
     * @return null if the blockEntity in the position doesn't have Fluid capability, the IFluidHandler otherwise
     */
    public static @Nullable IFluidHandler getHandlerFromCap(BlockPos pos, Level level, Direction sideOrdinal) {
        if (sideOrdinal == null) sideOrdinal = Direction.UP;
        sideOrdinal = StarHelper.checkItemFramesForSide(pos, level, sideOrdinal);
        return level.getCapability(BLOCK, pos, sideOrdinal);
    }

    public IFluidHandler getHandlerFromCap(BlockPos pos, Direction side) {
        return getHandlerFromCap(pos, level, side);
    }

    public boolean canStore(BlockPos pos, @NotNull FluidStack fluidStack) {
        IFluidHandler fluid = getHandlerFromCap(pos, TO_DIRECTION_MAP.get(pos.hashCode()));
        if (fluid != null) {
            for (int i = 0; i < fluid.getTanks(); i++) {
                if (fluid.isFluidValid(i, fluidStack) && fluid.fill(fluidStack, IFluidHandler.FluidAction.SIMULATE) >= Configs.STARBUCKET_THRESHOLD.get()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean canExtract(BlockPos pos) {
        IFluidHandler fluid = getHandlerFromCap(pos, FROM_DIRECTION_MAP.get(pos.hashCode()));
        if (fluid != null) {
            for (int i = 0; i < fluid.getTanks(); i++) {
                if (!fluid.getFluidInTank(i).isEmpty()) {
                    if (FluidScroll.checkForFilters(pos, fluidScroll, fluid.getFluidInTank(i), starbuncle.level()))
                        return false;
                    if (getTankForStorage(fluid.getFluidInTank(i)) != null)
                        return true;
                }
            }
        }
        return false;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        if (!getFluidStack().isEmpty()) {
            getFluidStack().save(starbuncle.registryAccess(), tag);
        }
        if (fluidScroll != null) {
            tag.put("itemScroll", fluidScroll.save(starbuncle.registryAccess()));
        }
        return super.toTag(tag);
    }

    @Override
    public ItemStack getStackForRender() {
        ItemStack instance = ModRegistry.FLUID_JAR.get().asItem().getDefaultInstance();
        if (!getFluidStack().isEmpty()) {
            instance.getCapability(Capabilities.FluidHandler.ITEM).fill(getFluidStack(), IFluidHandler.FluidAction.EXECUTE);
        }
//        tag.putBoolean("Starbuncle", true);
        return instance;
    }

}
