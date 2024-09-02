package alexthw.starbunclemania;

import alexthw.starbunclemania.glyph.PickupFluidEffect;
import alexthw.starbunclemania.glyph.PlaceFluidEffect;
import alexthw.starbunclemania.registry.ModRegistry;
import alexthw.starbunclemania.starbuncle.energy.StarbyEnergyBehavior;
import alexthw.starbunclemania.starbuncle.fluid.StarbyFluidBehavior;
import alexthw.starbunclemania.starbuncle.gas.StarbyGasBehavior;
import alexthw.starbunclemania.starbuncle.heal.StarbyHealerBehavior;
import alexthw.starbunclemania.starbuncle.item.AdvancedItemTransportBehavior;
import alexthw.starbunclemania.starbuncle.miner.StarbyMinerBehavior;
import alexthw.starbunclemania.starbuncle.placer.StarbyPlacerBehavior;
import alexthw.starbunclemania.starbuncle.sword.StarbyFighterBehavior;
import alexthw.starbunclemania.starbuncle.trash.StarbyVoidBehavior;
import com.hollingsworth.arsnouveau.api.mob_jar.JarBehavior;
import com.hollingsworth.arsnouveau.api.registry.BehaviorRegistry;
import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.registry.JarBehaviorRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.light.LightManager;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.LightLayer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;
import java.util.List;

public class ArsNouveauRegistry {

    public static final List<AbstractSpellPart> registeredSpells = new ArrayList<>(); //this will come handy for datagen

    public static void register() {
        register(PlaceFluidEffect.INSTANCE);
        register(PickupFluidEffect.INSTANCE);

        BehaviorRegistry.register(AdvancedItemTransportBehavior.TRANSPORT_ID, (entity, tag) -> new AdvancedItemTransportBehavior((Starbuncle) entity, tag));
        BehaviorRegistry.register(StarbyEnergyBehavior.TRANSPORT_ID, (entity, tag) -> new StarbyEnergyBehavior((Starbuncle) entity, tag));
        BehaviorRegistry.register(StarbyFluidBehavior.TRANSPORT_ID, (entity, tag) -> new StarbyFluidBehavior((Starbuncle) entity, tag));
        if (ModList.get().isLoaded("mekanism"))
            BehaviorRegistry.register(StarbyGasBehavior.TRANSPORT_ID, (entity, tag) -> new StarbyGasBehavior((Starbuncle) entity, tag));
        BehaviorRegistry.register(StarbyVoidBehavior.TRANSPORT_ID, (entity, tag) -> new StarbyVoidBehavior((Starbuncle) entity, tag));
        BehaviorRegistry.register(StarbyFighterBehavior.TRANSPORT_ID, (entity, tag) -> new StarbyFighterBehavior((Starbuncle) entity, tag));
        BehaviorRegistry.register(StarbyHealerBehavior.TRANSPORT_ID, (entity, tag) -> new StarbyHealerBehavior((Starbuncle) entity, tag));
        BehaviorRegistry.register(StarbyMinerBehavior.MINER_ID, (entity, tag) -> new StarbyMinerBehavior((Starbuncle) entity, tag));
        BehaviorRegistry.register(StarbyPlacerBehavior.MINER_ID, (entity, tag) -> new StarbyPlacerBehavior((Starbuncle) entity, tag));
    }

    public static void postInit() {
        LightManager.register(ModRegistry.STARBY_MOUNT.get(), (p -> {
            if (p.level().getBrightness(LightLayer.BLOCK, p.blockPosition()) < 6) {
                return 10;
            }
            return 0;
        }));
        registerJarBehaviors();
    }

    public static void register(AbstractSpellPart spellPart) {
        GlyphRegistry.registerSpell(spellPart);
        registeredSpells.add(spellPart);
    }

    public static void registerJarBehaviors() {
        JarBehaviorRegistry.register(EntityType.COW, new JarBehavior<>() {
            @Override
            public void tick(MobJarTile tile) {
                super.tick(tile);
                var level = tile.getLevel();
                if (level instanceof ServerLevel && level.getGameTime() % 20 == 0) {
                    IFluidHandler cap = level.getCapability(Capabilities.FluidHandler.BLOCK, tile.getBlockPos(), (Direction) null);
                    if (cap != null) {
                        var fluid = cap.getFluidInTank(0);
                        if (fluid.isEmpty() || (fluid.getFluid().isSame(NeoForgeMod.MILK.get()) && fluid.getAmount() < cap.getTankCapacity(0))) {
                            cap.fill(new FluidStack(NeoForgeMod.MILK.get(), 1000), IFluidHandler.FluidAction.EXECUTE);
                        }
                    }
                }
            }
        });
        JarBehaviorRegistry.register(EntityType.MOOSHROOM, new JarBehavior<>() {
            @Override
            public void tick(MobJarTile tile) {
                super.tick(tile);
                var level = tile.getLevel();
                if (level instanceof ServerLevel && level.getGameTime() % 20 == 0) {
                    IFluidHandler cap = level.getCapability(Capabilities.FluidHandler.BLOCK, tile.getBlockPos(), (Direction) null);
                    if (cap != null) {
                        var fluid = cap.getFluidInTank(0);
                        if (fluid.isEmpty() || (fluid.getFluid().isSame(NeoForgeMod.MILK.get()) && fluid.getAmount() < cap.getTankCapacity(0))) {
                            cap.fill(new FluidStack(NeoForgeMod.MILK.get(), 1000), IFluidHandler.FluidAction.EXECUTE);
                        }
                    }
                }
            }
        });
    }

}