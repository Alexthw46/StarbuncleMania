package alexthw.starbunclemania.common.item.cosmetic;

import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.familiar.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;

public class ExampleCosmetic extends AbstractCosmeticItem {

    public ExampleCosmetic(Properties properties, String s) {
        super(properties, s);
    }

    @Override
    public boolean canWear(LivingEntity entity) {
        return entity instanceof Starbuncle || entity instanceof FamiliarStarbuncle || entity instanceof FamiliarBookwyrm || entity instanceof FamiliarDrygmy || entity instanceof FamiliarWhirlisprig || entity instanceof FamiliarWixie;
    }

    @Override
    public ItemDisplayContext getTransformType() {
        return ItemDisplayContext.HEAD;
    }

    @Override
    public Vec3 getTranslations() {
        return getTranslations(null);
    }

    @Override
    public Vec3 getScaling() {
        return getScaling(null);
    }

    @Override
    public Vec3 getTranslations(LivingEntity entity) {
        return switch (entity) {
            case FamiliarBookwyrm ignored -> wyrmTransl;
            case FamiliarWhirlisprig ignored -> whirlTransl;
            case FamiliarDrygmy ignored -> drygmyTransl;
            case FamiliarWixie ignored -> new Vec3(0, 0.825, 0.2);
            case null, default -> starbTransl;
        };
    }

    @Override
    public Vec3 getScaling(LivingEntity entity) {
        return switch (entity) {
            case FamiliarBookwyrm ignored -> wyrmScale;
            case FamiliarWhirlisprig ignored -> whirlScale;
            case FamiliarDrygmy ignored -> drygmyScale;
            case FamiliarWixie ignored -> defaultScaling.scale(1.35);
            case null, default -> starbScale;
        };
    }

    static final Vec3 starbScale = new Vec3(1, 1, 1);
    static final Vec3 starbTransl = new Vec3(0, 0.43, 0);

    static final Vec3 whirlScale = new Vec3(1.25, 1, 1.25);
    static final Vec3 whirlTransl = new Vec3(0, .65, 0.2);

    static final Vec3 drygmyScale = new Vec3(1.5, 1.5, 1.5);
    static final Vec3 drygmyTransl = new Vec3(0, .75, 0.3);

    static final Vec3 wyrmScale = new Vec3(1., 1., 1.);
    static final Vec3 wyrmTransl = new Vec3(0, 0.675, 0);

    @Override
    public String getBone(LivingEntity entity) {
        if (entity instanceof FamiliarWixie) return "hat";
        return super.getBone(entity);
    }
}
