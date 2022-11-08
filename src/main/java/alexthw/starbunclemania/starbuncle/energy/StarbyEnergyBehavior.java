package alexthw.starbunclemania.starbuncle.energy;

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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.minecraftforge.common.capabilities.ForgeCapabilities.ENERGY;

public class StarbyEnergyBehavior extends StarbyListBehavior {

    public static final ResourceLocation TRANSPORT_ID = new ResourceLocation(ArsNouveau.MODID, "starby_energy_transport");

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    private int energy = 0;

    public StarbyEnergyBehavior(Starbuncle entity, CompoundTag tag) {
        super(entity, tag);
        if (tag.contains("battery")){
            energy = tag.getInt("battery");
        }
        goals.add(new WrappedGoal(3, new EnergyExtractGoal(entity, this)));
        goals.add(new WrappedGoal(3, new EnergyStoreGoal(entity, this)));
    }

    public static @Nullable IEnergyStorage getHandlerFromCap(BlockPos pos, Level level) {
        BlockEntity be = level.getBlockEntity(pos);
        Direction side = Direction.NORTH;
        return be != null && be.getCapability(ENERGY, side).isPresent() && be.getCapability(ENERGY, side).resolve().isPresent() ? be.getCapability(ENERGY, side).resolve().get() : null;
    }

    public IEnergyStorage getHandlerFromCap(BlockPos pos) {
        return getHandlerFromCap(pos, level);
    }

    public @Nullable BlockPos getBatteryForTake() {
        for (BlockPos pos : FROM_LIST) {
            if (isPositionValidTake(pos)) {
                return pos;
            }
        }
        return null;
    }

    public @Nullable BlockPos getBatteryForStore() {
        for (BlockPos pos : TO_LIST) {
            if (isPositionValidStore(pos)) {
                return pos;
            }
        }
        return null;
    }

    public boolean isPositionValidTake(BlockPos p) {
        if (p == null) return false;
        IEnergyStorage battery = getHandlerFromCap(p);
        if (battery != null) {
            return battery.canExtract() && battery.getEnergyStored() >= 0;
        }
        return false;
    }


    public boolean isPositionValidStore(BlockPos p) {
        if (p == null) return false;
        IEnergyStorage battery = getHandlerFromCap(p);
        if (battery != null) {
            int stored = battery.getEnergyStored();
            int max = battery.getMaxEnergyStored();
            return battery.canReceive() && stored <= max;
        } else return false;
    }


    @Override
    public void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
        super.onFinishedConnectionFirst(storedPos, storedEntity, playerEntity);
        if (storedPos != null) {
            BlockEntity be = level.getBlockEntity(storedPos);
            if (be != null && be.getCapability(ENERGY).isPresent()) {
                addToPos(storedPos);
                syncTag();
                PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.starbuncle.energy_to"));
            }
        }
    }

    @Override
    public void onFinishedConnectionLast(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
        super.onFinishedConnectionLast(storedPos, storedEntity, playerEntity);
        if (storedPos != null) {
            BlockEntity be = level.getBlockEntity(storedPos);
            if (be != null && be.getCapability(ENERGY).isPresent()) {
            addFromPos(storedPos);
            syncTag();
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.starbuncle.energy_from"));
            }
        }
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        super.getTooltip(tooltip);
        tooltip.add(Component.translatable("ars_nouveau.starbuncle.storing_energy", TO_LIST.size()));
        tooltip.add(Component.translatable("ars_nouveau.starbuncle.taking_energy", FROM_LIST.size()));
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.putInt("battery", energy);
        return super.toTag(tag);
    }

    public int getRatio(){
        return 100000;
    }

    @Override
    protected ResourceLocation getRegistryName() {
        return TRANSPORT_ID;
    }

    @Override
    public ItemStack getStackForRender() {
        return starbuncle.getCosmeticItem();
    }
}
