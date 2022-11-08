package alexthw.starbunclemania.starbuncle.fluid;

import alexthw.starbunclemania.StarbuncleMania;
import alexthw.starbunclemania.registry.ModRegistry;
import alexthw.starbunclemania.common.item.DirectionScroll;
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
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;

import static net.minecraftforge.common.capabilities.ForgeCapabilities.FLUID_HANDLER;


public class StarbyFluidBehavior extends StarbyListBehavior {

    public static final ResourceLocation TRANSPORT_ID = new ResourceLocation(StarbuncleMania.MODID, "starby_fluid_transport");

    private @Nonnull FluidStack fluidStack = FluidStack.EMPTY;
    public int side = -1;

    public StarbyFluidBehavior(Starbuncle entity, CompoundTag tag) {
        super(entity, tag);
        if (tag.contains("Direction")) side = tag.getInt("Direction");
        if (tag.contains("FluidName")) fluidStack = FluidStack.loadFluidStackFromNBT(tag);
        goals.add(new WrappedGoal(3, new FluidStoreGoal(entity, this)));
        goals.add(new WrappedGoal(3, new FluidExtractGoal(entity, this)));
    }

    public @Nonnull FluidStack getFluidStack() {
        return fluidStack;
    }

    public void setFluidStack(FluidStack fluid) {
        fluidStack = fluid;
        syncTag();
    }

    @Override
    public void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
        super.onFinishedConnectionFirst(storedPos, storedEntity, playerEntity);
        if (storedPos != null) {
            BlockEntity be = level.getBlockEntity(storedPos);
            if (be != null && be.getCapability(FLUID_HANDLER).isPresent()) {
                addToPos(storedPos);
                syncTag();
                PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.starbuncle.fluid_to"));
            }
        }
    }

    @Override
    public void onFinishedConnectionLast(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
        super.onFinishedConnectionLast(storedPos, storedEntity, playerEntity);
        if (storedPos != null) {
            BlockEntity be = level.getBlockEntity(storedPos);
            if (be != null && be.getCapability(FLUID_HANDLER).isPresent()) {
                addFromPos(storedPos);
                syncTag();
                PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.starbuncle.fluid_from"));
            }
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof DirectionScroll && stack.hasTag()){
            side = stack.getOrCreateTag().getInt("side");
            PortUtil.sendMessage(player, Component.translatable("ars_nouveau.filter_set"));
            syncTag();
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        super.getTooltip(tooltip);
        tooltip.add(Component.translatable("ars_nouveau.starbuncle.storing_fluid", TO_LIST.size()));
        tooltip.add(Component.translatable("ars_nouveau.starbuncle.taking_fluid", FROM_LIST.size()));
        if (side >= 0){
            tooltip.add(Component.literal("Preferred Side : " + Direction.values()[side].getName()));
        }
    }

    public int getRatio() {
        return 1000;
    }

    @Override
    protected ResourceLocation getRegistryName() {
        return TRANSPORT_ID;
    }

    public BlockPos getTankForStorage(FluidStack fluidInTank) {
        if (!fluidInTank.isEmpty()) {
            for (BlockPos pos : TO_LIST) {
                if (canStore(pos, fluidInTank)) {
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
        return getTankToExtract(getFluidStack());
    }

    public BlockPos getTankToExtract(FluidStack fluid) {
        for (BlockPos pos : FROM_LIST) {
            if (canExtract(pos)) {
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
    public static @Nullable IFluidHandler getHandlerFromCap(BlockPos pos, Level level, int sideOrdinal) {
        BlockEntity be = level.getBlockEntity(pos);
        for (ItemFrame i : level.getEntitiesOfClass(ItemFrame.class, new AABB(pos).inflate(1))) {
            // Check if these frames are attached to the tile
            BlockEntity adjTile = level.getBlockEntity(i.blockPosition().relative(i.getDirection().getOpposite()));
            if (adjTile == null || !adjTile.equals(be) || i.getItem().isEmpty()) continue;

            ItemStack stackInFrame = i.getItem();
            if (stackInFrame.getItem() instanceof DirectionScroll && stackInFrame.hasTag()){
                sideOrdinal = stackInFrame.getOrCreateTag().getInt("side");
                break;
            }
        }
        Direction side = sideOrdinal < 0 ? Direction.UP : Direction.values()[sideOrdinal];
        return be != null && be.getCapability(FLUID_HANDLER, side).isPresent() && be.getCapability(FLUID_HANDLER, side).resolve().isPresent() ? be.getCapability(FLUID_HANDLER, side).resolve().get() : null;
    }

    public IFluidHandler getHandlerFromCap(BlockPos pos) {
        return getHandlerFromCap(pos, level, side);
    }

    public boolean canStore(BlockPos pos, @Nonnull FluidStack fluidStack) {
        IFluidHandler fluid = getHandlerFromCap(pos);
        if (fluid != null) {
            for (int i = 0; i < fluid.getTanks(); i++) {
                if (fluid.isFluidValid(i, fluidStack) && fluid.fill(fluidStack, IFluidHandler.FluidAction.SIMULATE) > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean canExtract(BlockPos pos) {
        IFluidHandler fluid = getHandlerFromCap(pos);
        if (fluid != null) {
            for (int i = 0; i < fluid.getTanks(); i++) {
                if (!fluid.getFluidInTank(i).isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        if (!getFluidStack().isEmpty()) {
            getFluidStack().writeToNBT(tag);
        }
        if (side >= 0) tag.putInt("Direction", side);
        return super.toTag(tag);
    }

    @Override
    public ItemStack getStackForRender() {
        ItemStack instance = ModRegistry.FLUID_JAR.get().asItem().getDefaultInstance();
        CompoundTag tag = instance.getOrCreateTag();
        if (!getFluidStack().isEmpty()){
            tag.put("BlockEntityTag", getFluidStack().writeToNBT(new CompoundTag()));
        }
        tag.putBoolean("Starbuncle", true);
        return instance;
    }

}
