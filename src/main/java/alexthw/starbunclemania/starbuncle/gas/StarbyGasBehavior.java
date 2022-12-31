package alexthw.starbunclemania.starbuncle.gas;

import alexthw.starbunclemania.Configs;
import alexthw.starbunclemania.StarbuncleMania;
import alexthw.starbunclemania.common.item.DirectionScroll;
import alexthw.starbunclemania.common.item.cosmetic.StarBalloon;
import alexthw.starbunclemania.starbuncle.StarHelper;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.StarbyListBehavior;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import mekanism.api.Action;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import org.jetbrains.annotations.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StarbyGasBehavior extends StarbyListBehavior {

    public static final Capability<IGasHandler> GAS_HANDLER = CapabilityManager.get(new CapabilityToken<>() {});

    private GasStack gasStack = GasStack.EMPTY;
    public int side = -1;

    public StarbyGasBehavior(Starbuncle entity, CompoundTag tag) {
        super(entity, tag);
        if (tag.contains("Direction")) side = tag.getInt("Direction");
        if (tag.contains("gasName")) gasStack = GasStack.readFromNBT(tag);
        goals.add(new WrappedGoal(3, new GasStoreGoal(entity, this)));
        goals.add(new WrappedGoal(3, new GasExtractGoal(entity, this)));
    }

    @Override
    public boolean canGoToBed() {
        return getTankToExtract() == null && (getGasStack().isEmpty() || getTankForStorage() == null);
    }

    public @NotNull GasStack getGasStack() {
        return gasStack;
    }

    public void setGasStack(GasStack gas) {
        gasStack = gas;
        syncTag();
    }

    @Override
    public void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
        super.onFinishedConnectionFirst(storedPos, storedEntity, playerEntity);
        if (storedPos != null) {
            BlockEntity be = level.getBlockEntity(storedPos);
            if (be != null && be.getCapability(GAS_HANDLER).isPresent()) {
                addToPos(storedPos);
                syncTag();
                PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.starbuncle.gas_to"));
            }
        }
    }

    @Override
    public void onFinishedConnectionLast(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
        super.onFinishedConnectionLast(storedPos, storedEntity, playerEntity);
        if (storedPos != null) {
            BlockEntity be = level.getBlockEntity(storedPos);
            if (be != null && be.getCapability(GAS_HANDLER).isPresent()) {
                addFromPos(storedPos);
                syncTag();
                PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.starbuncle.gas_from"));
            }
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof DirectionScroll && stack.hasTag()) {
            side = stack.getOrCreateTag().getInt("side");
            PortUtil.sendMessage(player, Component.translatable("ars_nouveau.filter_set"));
            syncTag();
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        super.getTooltip(tooltip);
        tooltip.add(Component.translatable("ars_nouveau.starbuncle.storing_gas", TO_LIST.size()));
        tooltip.add(Component.translatable("ars_nouveau.starbuncle.taking_gas", FROM_LIST.size()));
        if (!gasStack.isEmpty()) {
            tooltip.add(Component.literal(getGasStack().getAmount() + " ").append(Component.translatable(getGasStack().getTranslationKey())));
        }
        if (side >= 0) {
            tooltip.add(Component.literal("Preferred Side : " + Direction.values()[side].name()));
        }
    }

    public int getRatio() {
        return Configs.STARBALLOON_RATIO.get();
    }

    @Override
    protected ResourceLocation getRegistryName() {
        return TRANSPORT_ID;
    }

    public static final ResourceLocation TRANSPORT_ID = new ResourceLocation(StarbuncleMania.MODID, "starby_gas_transport");


    public BlockPos getTankForStorage(GasStack gasInTank) {
        if (!gasInTank.isEmpty()) {
            for (BlockPos pos : TO_LIST) {
                if (canStore(pos, gasInTank)) {
                    return pos;
                }
            }
        }
        return null;
    }

    public BlockPos getTankForStorage() {
        return getTankForStorage(getGasStack());
    }

    public BlockPos getTankToExtract() {
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
     * @return null if the blockEntity in the position doesn't have Gas capability, the IGasHandler otherwise
     */
    public static @Nullable IGasHandler getHandlerFromCap(BlockPos pos, Level level, int sideOrdinal) {
        BlockEntity be = level.getBlockEntity(pos);
        sideOrdinal = StarHelper.checkItemFramesForSide(pos, level, sideOrdinal, be);
        Direction side = sideOrdinal < 0 ? Direction.UP : Direction.values()[sideOrdinal];
        return be != null && be.getCapability(GAS_HANDLER, side).isPresent() && be.getCapability(GAS_HANDLER, side).resolve().isPresent() ? be.getCapability(GAS_HANDLER, side).resolve().get() : null;
    }

    public IGasHandler getHandlerFromCap(BlockPos pos) {
        return getHandlerFromCap(pos, level, side);
    }

    public boolean canStore(BlockPos pos, @NotNull GasStack gasStack) {
        IGasHandler gas = getHandlerFromCap(pos);
        if (gas != null) {
            for (int i = 0; i < gas.getTanks(); i++) {
                if (gas.isValid(i, gasStack) && gas.insertChemical(gasStack, Action.SIMULATE).getAmount() >= Configs.STARBALLOON_THRESHOLD.get()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean canExtract(BlockPos pos) {
        IGasHandler gas = getHandlerFromCap(pos);
        if (gas != null) {
            for (int i = 0; i < gas.getTanks(); i++) {
                if (!gas.getChemicalInTank(i).isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        if (!getGasStack().isEmpty()) {
            getGasStack().write(tag);
        }
        if (side >= 0) tag.putInt("Direction", side);
        return super.toTag(tag);
    }

    @Override
    public ItemStack getStackForRender() {
        if (starbuncle.getCosmeticItem().getItem() instanceof StarBalloon)
            starbuncle.getCosmeticItem().getOrCreateTag().putInt("color", gasStack.getChemicalColorRepresentation());
        return ItemStack.EMPTY;
    }
}
