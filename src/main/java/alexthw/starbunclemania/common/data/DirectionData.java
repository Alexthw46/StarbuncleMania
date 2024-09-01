package alexthw.starbunclemania.common.data;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public record DirectionData(Direction direction) implements TooltipProvider {
    public static Codec<DirectionData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Direction.CODEC.fieldOf("direction").forGetter(DirectionData::direction)).apply(instance, DirectionData::new));

    public static StreamCodec<RegistryFriendlyByteBuf, DirectionData> STREAM_CODEC = StreamCodec.composite(Direction.STREAM_CODEC, DirectionData::direction, DirectionData::new);

    @Override
    public void addToTooltip(Item.@NotNull TooltipContext pContext, @NotNull Consumer<Component> pTooltipAdder, @NotNull TooltipFlag pTooltipFlag) {
        pTooltipAdder.accept(Component.literal(direction.getName()).setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GREEN)));
    }
}