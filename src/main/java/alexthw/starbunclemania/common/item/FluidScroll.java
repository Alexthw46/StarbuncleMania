package alexthw.starbunclemania.common.item;

import alexthw.starbunclemania.starbuncle.StarHelper;
import com.hollingsworth.arsnouveau.api.item.IScribeable;
import com.hollingsworth.arsnouveau.api.nbt.ItemstackData;
import com.hollingsworth.arsnouveau.common.items.ModItem;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FluidScroll extends ModItem implements IScribeable{

    public FluidScroll(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        if (pUsedHand == InteractionHand.MAIN_HAND && !pLevel.isClientSide) {
            ItemStack thisStack = pPlayer.getItemInHand(pUsedHand);
            ItemStack otherStack = pPlayer.getItemInHand(InteractionHand.OFF_HAND);
            if (!otherStack.isEmpty()) {
                onScribe(pLevel, pPlayer.blockPosition(), pPlayer, InteractionHand.OFF_HAND, thisStack);
                return InteractionResultHolder.success(thisStack);
            }
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    public static boolean checkForFilters(@Nullable BlockPos pos, ItemStack scroll, FluidStack fluid, Level level){
        boolean scrollCheck = scroll != null && scroll.getItem() instanceof FluidScroll filter && filter.isDenied(scroll, fluid);
        if (pos != null){
            return StarHelper.checkItemFramesForFluid(pos, level, scrollCheck, fluid);
        }else return scrollCheck;
    }

    public boolean isDenied(ItemStack fluidScroll, FluidStack fluidInTank) {
        FluidData filter = new FluidData(fluidScroll);
        return !filter.containsStack(fluidInTank);
    }

    @Override
    public boolean onScribe(Level world, BlockPos pos, Player player, InteractionHand handIn, ItemStack thisStack) {
        ItemStack container = player.getItemInHand(handIn);
        FluidScroll.FluidData scrollData = new FluidScroll.FluidData(thisStack);
        if (container.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent()){
            IFluidHandler tank = container.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
            return scrollData.writeWithFeedback(player, tank.getFluidInTank(0));
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip2, TooltipFlag flagIn) {
        if(!stack.hasTag())
            return;
        FluidData scrollData = new FluidData(stack);
        for (FluidStack s : scrollData.fluids) {
            tooltip2.add(s.getDisplayName());
        }
    }

    public static class FluidData extends ItemstackData{

        List<FluidStack> fluids = new ArrayList<>();

        public List<FluidStack> getFluids() {
            return fluids;
        }

        public FluidData(ItemStack stack) {
            super(stack);
            CompoundTag tag = getItemTag(stack);
            if (tag == null || tag.isEmpty())
                return;
            for (String s : tag.getAllKeys()) {
                if (s.contains("fluid_")) {
                    fluids.add(FluidStack.loadFluidStackFromNBT(tag.getCompound(s)));
                }
            }
        }

        public boolean writeWithFeedback(Player player, FluidStack stackToWrite) {
            if (stackToWrite.isEmpty())
                return false;
            if (containsStack(stackToWrite)) {
                PortUtil.sendMessage(player, Component.translatable("ars_nouveau.scribe.item_removed"));
                return remove(stackToWrite);
            }
            if(add(stackToWrite)) {
                PortUtil.sendMessage(player, Component.translatable("ars_nouveau.scribe.item_added"));
                return true;
            }
            return false;
        }

        private boolean remove(FluidStack stackToWrite) {
            boolean added = fluids.removeIf(stackToWrite::isFluidEqual);
            writeItem();
            return added;
        }

        private boolean add(FluidStack stackToWrite) {
            boolean added = fluids.add(stackToWrite.copy());
            writeItem();
            return added;
        }

        public boolean containsStack(FluidStack stackToWrite) {
            return fluids.stream().anyMatch(stackToWrite::isFluidEqual);
        }


        @Override
        public String getTagString() {
            return "sb_fluidScrollData";
        }
        public String getKey(FluidStack stack) {
            return "fluid_" + ForgeRegistries.FLUIDS.getKey(stack.getFluid()).toString();
        }
        @Override
        public void writeToNBT(CompoundTag tag) {
            for (FluidStack s : fluids) {
                CompoundTag fluidTag = new CompoundTag();
                s.writeToNBT(fluidTag);
                tag.put(getKey(s), fluidTag);
            }
        }
    }

}
