package alexthw.starbunclemania.starbuncle.energy;

import alexthw.starbunclemania.Configs;
import alexthw.starbunclemania.starbuncle.StarHelper;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.StarbyListBehavior;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class StarbyEnergyBehavior extends StarbyListBehavior {

    public static final ResourceLocation TRANSPORT_ID = ResourceLocation.fromNamespaceAndPath(ArsNouveau.MODID, "starby_energy_transport");

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    private int energy = 0;

    public StarbyEnergyBehavior(Starbuncle entity, CompoundTag tag) {
        super(entity, tag);
        if (tag.contains("battery")) {
            energy = tag.getInt("battery");
        }
        goals.add(new WrappedGoal(3, new EnergyExtractGoal(entity, this)));
        goals.add(new WrappedGoal(3, new EnergyStoreGoal(entity, this)));
    }

    @Override
    public boolean canGoToBed() {
        return isBedPowered() || (getBatteryForTake() == null || energy > Configs.STARBATTERY_THRESHOLD.get()) && (energy == 0 || getBatteryForStore() == null);
    }

    public static @Nullable IEnergyStorage getHandlerFromCap(BlockPos pos, Level level, Direction side) {
        if (side == null) side = Direction.UP;
        side = StarHelper.checkItemFramesForSide(pos, level, side);
        return level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, side);
    }

    public IEnergyStorage getHandlerFromCap(BlockPos pos, Direction side) {
        return getHandlerFromCap(pos, level, side);
    }

    public @Nullable BlockPos getBatteryForTake() {
        for (BlockPos pos : FROM_LIST) {
            if (level.isLoaded(pos) && isPositionValidTake(pos)) {
                return pos;
            }
        }
        return null;
    }

    public @Nullable BlockPos getBatteryForStore() {
        for (BlockPos pos : TO_LIST) {
            if (level.isLoaded(pos) && isPositionValidStore(pos)) {
                return pos;
            }
        }
        return null;
    }

    public boolean isPositionValidTake(BlockPos p) {
        if (p == null) return false;
        IEnergyStorage battery = getHandlerFromCap(p, FROM_DIRECTION_MAP.get(p.hashCode()));
        if (battery != null) {
            return battery.canExtract() && battery.getEnergyStored() >= 0;
        }
        return false;
    }


    public boolean isPositionValidStore(BlockPos p) {
        if (p == null) return false;
        IEnergyStorage battery = getHandlerFromCap(p, TO_DIRECTION_MAP.get(p.hashCode()));
        if (battery != null) {
            return battery.canReceive() && battery.receiveEnergy(getRatio(), true) >= Configs.STARBATTERY_THRESHOLD.get();
        } else return false;
    }


    @Override
    public void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable Direction side, @Nullable LivingEntity storedEntity, Player playerEntity) {
        super.onFinishedConnectionFirst(storedPos, side, storedEntity, playerEntity);
        if (storedPos != null) {
            BlockEntity be = level.getBlockEntity(storedPos);
            if (level.getCapability(Capabilities.EnergyStorage.BLOCK, storedPos, side) != null || getHandlerFromCap(storedPos, side) != null) {
                addToPos(storedPos);
                syncTag();
                PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.starbuncle.energy_to"));
            }
        }
    }

    @Override
    public void onFinishedConnectionLast(@Nullable BlockPos storedPos, @Nullable Direction side, @Nullable LivingEntity storedEntity, Player playerEntity) {
        super.onFinishedConnectionLast(storedPos, side, storedEntity, playerEntity);
        if (storedPos != null) {
            if (starbuncle.level().getCapability(Capabilities.EnergyStorage.BLOCK, storedPos, side) != null || getHandlerFromCap(storedPos, side) != null) {
                addFromPos(storedPos);
                syncTag();
                PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.starbuncle.energy_from"));
            }
        }
    }

    @Override
    public void getTooltip(Consumer<Component> tooltip) {
        super.getTooltip(tooltip);
        tooltip.accept(Component.translatable("ars_nouveau.starbuncle.storing_energy", TO_LIST.size()));
        tooltip.accept(Component.translatable("ars_nouveau.starbuncle.taking_energy", FROM_LIST.size()));
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.putInt("battery", energy);
        return super.toTag(tag);
    }

    public int getRatio() {
        return Configs.STARBATTERY_RATIO.get();
    }

    @Override
    public ResourceLocation getRegistryName() {
        return TRANSPORT_ID;
    }

    @Override
    public ItemStack getStackForRender() {
        return ItemStack.EMPTY;
    }

}
