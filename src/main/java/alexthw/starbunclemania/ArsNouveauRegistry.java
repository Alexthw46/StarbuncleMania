package alexthw.starbunclemania;

import alexthw.starbunclemania.registry.ModRegistry;
import alexthw.starbunclemania.starbuncle.energy.StarbyEnergyBehavior;
import alexthw.starbunclemania.starbuncle.fluid.StarbyFluidBehavior;
import alexthw.starbunclemania.starbuncle.gas.StarbyGasBehavior;
import alexthw.starbunclemania.starbuncle.heal.StarbyHealerBehavior;
import alexthw.starbunclemania.starbuncle.item.AdvancedItemTransportBehavior;
import alexthw.starbunclemania.starbuncle.sword.StarbyFighterBehavior;
import alexthw.starbunclemania.starbuncle.trash.StarbyVoidBehavior;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.entity.BehaviorRegistry;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.light.LightManager;
import net.minecraft.world.level.LightLayer;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;
import java.util.List;

public class ArsNouveauRegistry {

    public static final List<AbstractSpellPart> registeredSpells = new ArrayList<>(); //this will come handy for datagen

    public static void register(){
        BehaviorRegistry.register(AdvancedItemTransportBehavior.TRANSPORT_ID, (entity, tag) -> new AdvancedItemTransportBehavior((Starbuncle) entity, tag));
        BehaviorRegistry.register(StarbyEnergyBehavior.TRANSPORT_ID, (entity, tag) -> new StarbyEnergyBehavior((Starbuncle) entity, tag));
        BehaviorRegistry.register(StarbyFluidBehavior.TRANSPORT_ID, (entity, tag) -> new StarbyFluidBehavior((Starbuncle) entity, tag));
        if (ModList.get().isLoaded("mekanism"))
            BehaviorRegistry.register(StarbyGasBehavior.TRANSPORT_ID, (entity, tag) -> new StarbyGasBehavior((Starbuncle) entity, tag));
        BehaviorRegistry.register(StarbyVoidBehavior.TRANSPORT_ID, (entity, tag) -> new StarbyVoidBehavior((Starbuncle) entity, tag));
        BehaviorRegistry.register(StarbyFighterBehavior.TRANSPORT_ID, (entity, tag) -> new StarbyFighterBehavior((Starbuncle) entity, tag));
        BehaviorRegistry.register(StarbyHealerBehavior.TRANSPORT_ID, (entity, tag) -> new StarbyHealerBehavior((Starbuncle) entity, tag));
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
