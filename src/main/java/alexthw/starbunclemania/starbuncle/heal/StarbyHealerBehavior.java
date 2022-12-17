package alexthw.starbunclemania.starbuncle.heal;

import alexthw.starbunclemania.StarbuncleMania;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.StarbyBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.UUID;

public class StarbyHealerBehavior extends StarbyBehavior {

    public UUID master;
    private LivingEntity owner;

    public StarbyHealerBehavior(Starbuncle entity, CompoundTag tag) {
        super(entity, tag);
        if (tag.contains("master")){
            master = tag.getUUID("master");
        }
        if (master != null){
            owner = level.getPlayerByUUID(master);
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        if (master != null){
            tag.putUUID("master",master);
        }
        return super.toTag(tag);
    }

    @Override
    protected ResourceLocation getRegistryName() {
        return TRANSPORT_ID;
    }
    public static final ResourceLocation TRANSPORT_ID = new ResourceLocation(StarbuncleMania.MODID, "starby_healer");
}
