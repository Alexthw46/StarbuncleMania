package alexthw.starbunclemania.starbuncle.gas;

import alexthw.starbunclemania.Configs;
import alexthw.starbunclemania.StarbuncleMania;
import alexthw.starbunclemania.common.item.cosmetic.StarBalloon;
import alexthw.starbunclemania.starbuncle.StarHelper;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.StarbyListBehavior;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import mekanism.api.Action;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class StarbyGasBehavior extends StarbyListBehavior {

    private ChemicalStack gasStack = ChemicalStack.EMPTY;

    public StarbyGasBehavior(Starbuncle entity, CompoundTag tag) {
        super(entity, tag);
        if (tag.contains("gasName")) gasStack = ChemicalStack.parseOptional(level.registryAccess(), tag);
        goals.add(new WrappedGoal(3, new GasStoreGoal(entity, this)));
        goals.add(new WrappedGoal(3, new GasExtractGoal(entity, this)));
    }

    @Override
    public boolean canGoToBed() {
        return isBedPowered() || (getTankToExtract() == null && (getGasStack().isEmpty() || getTankForStorage() == null));
    }

    public @NotNull ChemicalStack getGasStack() {
        return gasStack;
    }

    public void setGasStack(ChemicalStack gas) {
        gasStack = gas;
        syncTag();
    }

    @Override
    public void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable Direction face, @Nullable LivingEntity storedEntity, Player playerEntity) {
        super.onFinishedConnectionFirst(storedPos, face, storedEntity, playerEntity);
        if (storedPos != null) {

            if (starbuncle.level().getCapability(Capabilities.CHEMICAL.block(), storedPos, face) != null) {
                addToPos(storedPos);
                syncTag();
                PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.starbuncle.gas_to"));
            }
        }
    }

    @Override
    public void onFinishedConnectionLast(@Nullable BlockPos storedPos, @Nullable Direction face, @Nullable LivingEntity storedEntity, Player playerEntity) {
        super.onFinishedConnectionLast(storedPos, face, storedEntity, playerEntity);
        if (storedPos != null) {
            if (starbuncle.level().getCapability(Capabilities.CHEMICAL.block(), storedPos, face) != null) {
                addFromPos(storedPos);
                syncTag();
                PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.starbuncle.gas_from"));
            }
        }
    }

    @Override
    public void getTooltip(Consumer<Component> tooltip) {
        super.getTooltip(tooltip);
        tooltip.accept(Component.translatable("ars_nouveau.starbuncle.storing_gas", TO_LIST.size()));
        tooltip.accept(Component.translatable("ars_nouveau.starbuncle.taking_gas", FROM_LIST.size()));
        if (!gasStack.isEmpty()) {
            tooltip.accept(Component.literal(getGasStack().getAmount() + " ").append(Component.translatable(getGasStack().getTranslationKey())));
        }
    }

    public int getRatio() {
        return Configs.STARBALLOON_RATIO.get();
    }

    @Override
    public ResourceLocation getRegistryName() {
        return TRANSPORT_ID;
    }

    public static final ResourceLocation TRANSPORT_ID = ResourceLocation.fromNamespaceAndPath(StarbuncleMania.MODID, "starby_gas_transport");


    public BlockPos getTankForStorage(ChemicalStack gasInTank) {
        if (!gasInTank.isEmpty()) {
            for (BlockPos pos : TO_LIST) {
                if (level.isLoaded(pos) && canStore(pos, gasInTank)) {
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
            if (level.isLoaded(pos) && canExtract(pos)) {
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
    public static @Nullable IChemicalHandler getHandlerFromCap(BlockPos pos, Level level, Direction side) {
        if (side == null) side = Direction.NORTH;
        side = StarHelper.checkItemFramesForSide(pos, level, side);
        return level.getCapability(Capabilities.CHEMICAL.block(), pos, side);
    }

    public IChemicalHandler getHandlerFromCap(BlockPos pos, Direction side) {
        return getHandlerFromCap(pos, level, side);
    }

    public boolean canStore(BlockPos pos, @NotNull ChemicalStack gasStack) {
        IChemicalHandler gas = getHandlerFromCap(pos, TO_DIRECTION_MAP.get(pos.hashCode()));
        if (gas != null) {
            for (int i = 0; i < gas.getChemicalTanks(); i++) {
                if (gas.isValid(i, gasStack) && gas.insertChemical(gasStack, Action.SIMULATE).getAmount() <= Configs.STARBALLOON_THRESHOLD.get()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean canExtract(BlockPos pos) {
        IChemicalHandler gas = getHandlerFromCap(pos, FROM_DIRECTION_MAP.get(pos.hashCode()));
        if (gas != null) {
            for (int i = 0; i < gas.getChemicalTanks(); i++) {
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
            getGasStack().save(level.registryAccess(), tag);
        }
        return super.toTag(tag);
    }

    @Override
    public ItemStack getStackForRender() {
        if (starbuncle.getCosmeticItem().getItem() instanceof StarBalloon)
            starbuncle.getCosmeticItem().set(DataComponents.DYED_COLOR, new DyedItemColor(gasStack.getChemicalColorRepresentation(), false));
        return ItemStack.EMPTY;
    }
}
