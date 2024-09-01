package alexthw.starbunclemania.common.block.fluids;

import alexthw.starbunclemania.Configs;
import alexthw.starbunclemania.recipe.FluidSourcelinkRecipe;
import alexthw.starbunclemania.registry.ModRegistry;
import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.common.block.tile.SourcelinkTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import java.util.*;


public class FluidSourcelinkTile extends SourcelinkTile implements ITooltipProvider {

    public FluidSourcelinkTile(BlockPos pos, BlockState state) {
        super(ModRegistry.FLUID_SOURCELINK_TILE.get(), pos, state);
    }

    public static final int capacity = 6000;

    public final FluidTank tank = new FluidTank(capacity) {
        protected void onContentsChanged() {
            FluidSourcelinkTile.this.updateBlock();
            FluidSourcelinkTile.this.setChanged();
        }

        @Override
        public boolean isFluidValid(@NotNull FluidStack stack) {
            return FluidSourcelinkTile.this.getSourceFromFluid(stack) > 0;
        }
    };

    boolean tester(IFluidHandler tank) {
        for (int i = 0; i < tank.getTanks(); i++) {
            FluidStack fluid = tank.getFluidInTank(i);
            if (getSourceFromFluid(fluid) > 0) {
                return this.tank.isEmpty() || FluidStack.isSameFluidSameComponents(this.tank.getFluid(), fluid);
            }
        }
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (level != null) {
            if (this.tank.getSpace() > 0 && level.getGameTime() % 20 == 0) {
                IFluidHandler fluidHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, getBlockPos().below(), Direction.UP);
                if (fluidHandler != null) {
                    if (tester(fluidHandler)) {
                        this.tank.fill(fluidHandler.drain(Math.min(2000, tank.getSpace()), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
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

    static final Map<ResourceLocation, Double> cache = new HashMap<>();

    double getSourceFromFluid(FluidStack tank) {
        if (!tank.isEmpty() && level != null) {
            ResourceLocation fluid = BuiltInRegistries.FLUID.getKey(tank.getFluid());
            if (cache.containsKey(fluid)) {
                return cache.get(fluid);
            } else if (Configs.FLUID_TO_SOURCE_MAP.containsKey(fluid)) {
                double value = Configs.FLUID_TO_SOURCE_MAP.get(fluid);
                cache.put(fluid, value);
                return value;
            } else if (tank.has(DataComponents.POTION_CONTENTS)) {
                PotionContents contents = tank.get(DataComponents.POTION_CONTENTS);
                double mana = 75;
                Set<Holder<MobEffect>> effectTypes = new HashSet<>();
                assert contents != null;
                for (MobEffectInstance e : contents.getAllEffects()) {
                    mana += (e.getDuration() / 50.);
                    mana += e.getAmplifier() * 250;
                    mana += 150;
                    effectTypes.add(e.getEffect());
                }
                if (effectTypes.size() > 1)
                    mana *= (1.5 * (effectTypes.size() - 1));
                return mana / 250; //250 mb equals a potion
            } else {
                List<RecipeHolder<FluidSourcelinkRecipe>> recipes = level.getRecipeManager().getAllRecipesFor(ModRegistry.FLUID_SOURCELINK_RT.get());
                FluidSourcelinkRecipe find = recipes.stream().map(RecipeHolder::value).filter(r -> r.fluidType().equals(fluid)).findFirst().orElse(null);
                double value = 0;
                if (find != null) {
                    value = find.conversion_ratio();
                }
                cache.put(fluid, value);
                return value;
            }
        }
        return 0;
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
    public void handleUpdateTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider pRegistries) {
        super.handleUpdateTag(tag, pRegistries);
        if (level != null)
            level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 8);
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);
        if (!tank.isEmpty()) {
            tank.writeToNBT(pRegistries, tag);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(tag, pRegistries);
        tank.readFromNBT(pRegistries, tag);
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        LiquidJarTile.displayFluidTooltip(tooltip, this.getFluid());
        tooltip.add(Component.translatable("ars_nouveau.source_jar.fullness", (getSource() * 100) / this.getMaxSource()));
    }

}
