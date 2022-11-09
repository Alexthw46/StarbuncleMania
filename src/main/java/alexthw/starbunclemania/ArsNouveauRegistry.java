package alexthw.starbunclemania;

import alexthw.starbunclemania.registry.ModRegistry;
import alexthw.starbunclemania.starbuncle.energy.StarbyEnergyBehavior;
import alexthw.starbunclemania.starbuncle.fluid.StarbyFluidBehavior;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.entity.BehaviorRegistry;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.light.LightManager;
import net.minecraft.world.level.LightLayer;

import java.util.ArrayList;
import java.util.List;

public class ArsNouveauRegistry {

    public static final List<AbstractSpellPart> registeredSpells = new ArrayList<>(); //this will come handy for datagen

    public static void register(){
        BehaviorRegistry.register(StarbyEnergyBehavior.TRANSPORT_ID, (entity, tag) -> new StarbyEnergyBehavior((Starbuncle) entity, tag));
        BehaviorRegistry.register(StarbyFluidBehavior.TRANSPORT_ID, (entity, tag) -> new StarbyFluidBehavior((Starbuncle) entity, tag));
    }

    public static void postInit(){
        LightManager.register(ModRegistry.STARBY_MOUNT.get(), (p -> {
            if (p.level.getBrightness(LightLayer.BLOCK, p.blockPosition()) < 6) {
                return 10;
            }
            return 0;
        }));
    }

    public static void register(AbstractSpellPart spellPart){
        ArsNouveauAPI.getInstance().registerSpell(spellPart);
        registeredSpells.add(spellPart);
    }

}
