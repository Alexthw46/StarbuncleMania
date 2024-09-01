package alexthw.starbunclemania.common.data;

import com.hollingsworth.arsnouveau.api.item.NBTComponent;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.mojang.serialization.Codec;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class FluidScrollData implements NBTComponent<FluidScrollData>, TooltipProvider {

    public static final Codec<FluidScrollData> CODEC = FluidStack.CODEC.listOf().xmap(FluidScrollData::new, (i) -> i.items);
    public static final StreamCodec<RegistryFriendlyByteBuf, FluidScrollData> STREAM_CODEC = StreamCodec.composite(FluidStack.STREAM_CODEC.apply(
            ByteBufCodecs.collection(NonNullList::createWithCapacity)
    ), (i) -> i.items, FluidScrollData::new);

    private final List<FluidStack> items;

    public FluidScrollData(List<FluidStack> items) {
        this.items = List.copyOf(items);
    }

    public FluidScrollData() {
        this(List.of());
    }


    public boolean containsStack(FluidStack stack) {
        return contains(items, stack);
    }

    public static boolean contains(List<FluidStack> list, FluidStack stack) {
        return list.stream().anyMatch(s -> FluidStack.isSameFluid(s, stack));
    }

    public Iterable<FluidStack> getItems() {
        return items;
    }

    @Override
    public Codec<FluidScrollData> getCodec() {
        return CODEC;
    }

    public FluidScrollData.Mutable mutable() {
        return new FluidScrollData.Mutable(this);
    }

    @Override
    public void addToTooltip(Item.@NotNull TooltipContext pContext, @NotNull Consumer<Component> pTooltipAdder, @NotNull TooltipFlag pTooltipFlag) {
        for (FluidStack s : items) {
            pTooltipAdder.accept(s.getHoverName());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FluidScrollData that = (FluidScrollData) o;
        for (FluidStack stack : items) {
            if (!that.containsStack(stack)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getItems());
    }

    public static class Mutable {
        private final List<FluidStack> list;

        public Mutable(FluidScrollData data) {
            this.list = data.items.stream().map(FluidStack::copy).collect(Collectors.toCollection(ArrayList::new));
        }

        public boolean add(FluidStack stack) {
            return list.add(stack.copy());
        }

        public boolean remove(FluidStack stack) {
            return list.remove(stack.copy());
        }

        public List<FluidStack> getItems() {
            return list;
        }

        public FluidScrollData toImmutable() {
            return new FluidScrollData(list);
        }

        public boolean writeWithFeedback(Player player, FluidStack stackToWrite) {
            if (stackToWrite.isEmpty())
                return false;
            if (FluidScrollData.contains(list, stackToWrite)) {
                PortUtil.sendMessage(player, Component.translatable("ars_nouveau.scribe.item_removed"));
                return remove(stackToWrite);
            }
            if (add(stackToWrite)) {
                PortUtil.sendMessage(player, Component.translatable("ars_nouveau.scribe.item_added"));
                return true;
            }
            return false;
        }
    }
}

