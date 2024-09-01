package alexthw.starbunclemania.starbuncle.sword;

import alexthw.starbunclemania.StarbuncleMania;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.WealdWalker;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.StarbyBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class StarbyFighterBehavior extends StarbyBehavior {

    public UUID master;
    private LivingEntity owner;

    public StarbyFighterBehavior(Starbuncle starbuncle, CompoundTag tag) {
        super(starbuncle, tag);
        if (tag.contains("master")){
           master = tag.getUUID("master");
        }
        if (master != null){
            owner = level.getPlayerByUUID(master);
        }
        goals.add(new WrappedGoal(1, new MeleeAttackGoal(this.starbuncle, 2.0, true)));
        goals.add(new WrappedGoal(4, new RandomStrollGoal(this.starbuncle, 1.0)));
        starbuncle.targetSelector.removeAllGoals( (g) -> true);
        starbuncle.targetSelector.addGoal(3, new HurtByTargetGoal(starbuncle, Player.class).setAlertOthers(WealdWalker.class, Starbuncle.class));
        starbuncle.targetSelector.addGoal(1, new TargetGoal(starbuncle, false){
            /**
             * Returns whether the EntityAIBase should begin execution.
             */
            public boolean canUse() {
                return StarbyFighterBehavior.this.owner != null && StarbyFighterBehavior.this.owner.getLastHurtMob() != null;
            }

            /**
             * Execute a one shot task or start executing a continuous task
             */
            public void start() {
                StarbyFighterBehavior.this.starbuncle.setTarget(StarbyFighterBehavior.this.owner.getLastHurtMob());
                super.start();
            }
        });
        starbuncle.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(starbuncle, Mob.class, 10, false, false,
                (LivingEntity entity) ->
                        entity instanceof Mob mob && mob.getTarget() != null && mob.getTarget().equals(this.owner)
                        || entity != null && entity.getKillCredit() != null && entity.getKillCredit().equals(this.owner))
        );
    }

    @Override
    public void onFinishedConnectionLast(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
        super.onFinishedConnectionLast(storedPos, storedEntity, playerEntity);
        if (storedEntity != starbuncle) {
            starbuncle.setTarget(storedEntity);
            syncTag();
        }
    }

    @Override
    public void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
        super.onFinishedConnectionFirst(storedPos, storedEntity, playerEntity);
        if (storedEntity != starbuncle) {
            starbuncle.setTarget(storedEntity);
            syncTag();
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
    public ResourceLocation getRegistryName() {
        return TRANSPORT_ID;
    }
    public static final ResourceLocation TRANSPORT_ID = ResourceLocation.fromNamespaceAndPath(StarbuncleMania.MODID, "starby_fighter");

    @Override
    public ItemStack getStackForRender() {
        return ItemStack.EMPTY;
    }

}
