package alexthw.starbunclemania.starbuncle.sword;

import alexthw.starbunclemania.StarbuncleMania;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.StarbyBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class StarbyFigherBehavior extends StarbyBehavior {

    public UUID master;
    private LivingEntity owner;

    public StarbyFigherBehavior(Starbuncle starbuncle, CompoundTag tag) {
        super(starbuncle, tag);
        if (tag.contains("master")){
           master = tag.getUUID("master");
        }
        if (master != null){
            owner = level.getPlayerByUUID(master);
        }
        goals.add(new WrappedGoal(2, new HurtByTargetGoal(starbuncle)));
        goals.add(new WrappedGoal(1, new TargetGoal(starbuncle, false){
            /**
             * Returns whether the EntityAIBase should begin execution.
             */
            public boolean canUse() {
                return StarbyFigherBehavior.this.owner != null && StarbyFigherBehavior.this.owner.getLastHurtMob() != null;
            }

            /**
             * Execute a one shot task or start executing a continuous task
             */
            public void start() {
                StarbyFigherBehavior.this.starbuncle.setTarget(StarbyFigherBehavior.this.owner.getLastHurtMob());
                super.start();
            }
        }));
        goals.add(new WrappedGoal(3, new NearestAttackableTargetGoal<>(starbuncle, Mob.class, 10, false, true,
                (LivingEntity entity) ->
                        (entity instanceof Mob mob && mob.getTarget() != null && mob.getTarget().equals(this.owner))
                        || (entity != null && entity.getKillCredit() != null && entity.getKillCredit().equals(this.owner)))
        ));
    }

    @Override
    public void onFinishedConnectionLast(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
        super.onFinishedConnectionLast(storedPos, storedEntity, playerEntity);
        starbuncle.setTarget(storedEntity);

    }

    @Override
    public void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
        super.onFinishedConnectionFirst(storedPos, storedEntity, playerEntity);
        starbuncle.setTarget(storedEntity);
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
    public static final ResourceLocation TRANSPORT_ID = new ResourceLocation(StarbuncleMania.MODID, "starby_fighter");

}
