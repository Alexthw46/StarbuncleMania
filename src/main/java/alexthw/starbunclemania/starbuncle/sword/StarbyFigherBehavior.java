package alexthw.starbunclemania.starbuncle.sword;

import alexthw.starbunclemania.StarbuncleMania;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.StarbyBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class StarbyFigherBehavior extends StarbyBehavior {
    public StarbyFigherBehavior(Starbuncle entity, CompoundTag tag) {
        super(entity, tag);
    }
    @Override
    protected ResourceLocation getRegistryName() {
        return TRANSPORT_ID;
    }
    public static final ResourceLocation TRANSPORT_ID = new ResourceLocation(StarbuncleMania.MODID, "starby_fighter");
}
