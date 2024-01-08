package alexthw.starbunclemania.common.block.fluids;

import alexthw.starbunclemania.Configs;
import alexthw.starbunclemania.registry.ModRegistry;
import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.potion.PotionData;
import com.hollingsworth.arsnouveau.common.block.tile.SourcelinkTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.minecraftforge.common.capabilities.ForgeCapabilities.FLUID_HANDLER;

public class FluidSourcelinkTile extends SourcelinkTile implements ITooltipProvider {

    public FluidSourcelinkTile(BlockPos pos, BlockState state) {
        super(ModRegistry.FLUID_SOURCELINK_TILE.get(), pos, state);
    }

    public static int capacity = 6000;
    private final LazyOptional<IFluidHandler> holder = LazyOptional.of(() -> this.tank);

    protected final FluidTank tank = new FluidTank(capacity) {
        protected void onContentsChanged() {
            FluidSourcelinkTile.this.updateBlock();
            FluidSourcelinkTile.this.setChanged();
        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
            return FluidSourcelinkTile.this.getSourceFromFluid(stack) > 0;
        }
    };

    boolean tester(IFluidHandler tank) {
        for (int i = 0; i < tank.getTanks(); i++) {
            FluidStack fluid = tank.getFluidInTank(i);
            if (getSourceFromFluid(fluid) > 0) {
                return this.tank.isEmpty() || this.tank.getFluid().isFluidEqual(fluid);
            }
        }
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (level != null) {
            if (this.tank.getSpace() > 0 && level.getGameTime() % 20 == 0) {
                BlockEntity be = level.getBlockEntity(this.getBlockPos().below());
                if (be != null && be.getCapability(ForgeCapabilities.FLUID_HANDLER, Direction.UP).isPresent()) {
                    IFluidHandler handler = be.getCapability(FLUID_HANDLER, Direction.DOWN).resolve().isPresent() ? be.getCapability(FLUID_HANDLER, Direction.DOWN).resolve().get() : null;
                    if (handler != null && tester(handler)) {
                        this.tank.fill(handler.drain(Math.min(2000, tank.getSpace()), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                    }
                }
            }
            if (!level.isClientSide() && level.getGameTime() % 20 == 0 && this.canAcceptSource()) {
                double sourceFromFluid = getSourceFromFluid(this.getFluid());
                if (sourceFromFluid > 0 && this.canAcceptSource((int) (sourceFromFluid * 500))) {
                    int maxDrain = Math.min(Mth.ceil((getMaxSource() - getSource()) / sourceFromFluid), 2000);
                    int drain = this.tank.drain(maxDrain, IFluidHandler.FluidAction.EXECUTE).getAmount();
                    this.addSource((int) (drain * sourceFromFluid));
                }
            }
        }
    }

    static Map<ResourceLocation, Double> cache = new HashMap<>();

    double getSourceFromFluid(FluidStack tank) {
        if (!tank.isEmpty() && level != null) {
            ResourceLocation fluid = ForgeRegistries.FLUIDS.getKey(tank.getFluid());
            if (fluid == null) return 0;
            if (cache.containsKey(fluid)) {
                return cache.get(fluid);
            } else if (Configs.FLUID_TO_SOURCE_MAP.containsKey(fluid)) {
                double value = Configs.FLUID_TO_SOURCE_MAP.get(fluid);
                cache.put(fluid, value);
                return value;
            } else if (tank.hasTag() && tank.getFluid().is(ModRegistry.POTION)) {
                PotionData potion = PotionData.fromTag(tank.getTag());
                double mana = 75;
                Set<MobEffect> effectTypes = new HashSet<>();
                for (MobEffectInstance e : potion.fullEffects()) {
                    mana += (e.getDuration() / 50.);
                    mana += e.getAmplifier() * 250;
                    mana += 150;
                    effectTypes.add(e.getEffect());
                }
                if (effectTypes.size() > 1)
                    mana *= (1.5 * (effectTypes.size() - 1));
                return mana / 250; //250 mb equals a potion
            } else {
                var recipes = level.getRecipeManager().getAllRecipesFor(ModRegistry.FLUID_SOURCELINK_RT.get());
                var find = recipes.stream().filter(r -> r.fluidType.equals(fluid)).findFirst().orElse(null);
                double value = 0;
                if (find != null) {
                    value = find.conversion_ratio;
                }
                cache.put(fluid, value);
                return value;
            }
        }
        return 0;
    }

    @NotNull
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction facing) {
        return capability == ForgeCapabilities.FLUID_HANDLER ? this.holder.cast() : super.getCapability(capability, facing);
    }

    public boolean interact(Player player, InteractionHand hand) {
        return FluidUtil.interactWithFluidHandler(player, hand, this.tank);
    }

    public FluidStack getFluid() {
        return this.tank.getFluid();
    }

    @Override
    public int getMaxSource() {
        return 5000;
    }

    @Override
    public int getTransferRate() {
        return 2000;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        if (level != null)
            level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 8);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (!tank.isEmpty()) {
            tank.writeToNBT(tag);
        }
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        tank.readFromNBT(pTag);
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        LiquidJarTile.displayFluidTooltip(tooltip, this.getFluid());
        tooltip.add(Component.translatable("ars_nouveau.source_jar.fullness", (getSource() * 100) / this.getMaxSource()));
    }

}
