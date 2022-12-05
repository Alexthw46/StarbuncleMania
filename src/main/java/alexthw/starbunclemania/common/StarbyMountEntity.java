package alexthw.starbunclemania.common;

import alexthw.starbunclemania.registry.ModRegistry;
import com.hollingsworth.arsnouveau.common.entity.ChangeableBehavior;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class StarbyMountEntity extends Starbuncle implements PlayerRideable {


    public StarbyMountEntity(Level world) {
        super(world, true);
    }

    public StarbyMountEntity(Level world, StarbuncleData data) {
        super(world, true);
        this.data = data;
        restoreFromTag();
        refreshDimensions();
    }

    @Override
    public void setCosmeticItem(ItemStack stack) {
        this.level.addFreshEntity(new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(), stack));
    }

    @Override
    public void setBehavior(ChangeableBehavior behavior) {
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
    }

    final EntityDimensions BB = new EntityDimensions(2, 2, true);
    @Override
    public EntityDimensions getDimensions(Pose p_213305_1_) {
        return BB;
    }

    @Override
    public void die(DamageSource source) {
        this.setCosmeticItem(ItemStack.EMPTY);
        super.die(source);
    }

    @Override
    public EntityType<?> getType() {
        return ModRegistry.STARBY_MOUNT.get();
    }

    public void travel(Vec3 pTravelVector) {
        if (this.isAlive()) {
            if (this.isVehicle() && this.getControllingPassenger() instanceof Player livingentity) {
                this.setYRot(livingentity.getYRot());
                this.yRotO = this.getYRot();
                this.setXRot(livingentity.getXRot() * 0.5F);
                this.setRot(this.getYRot(), this.getXRot());
                this.yBodyRot = this.getYRot();
                this.yHeadRot = this.yBodyRot;
                float strafe = livingentity.xxa * 0.5F;
                float forward = livingentity.zza * 2;
                if (forward <= 0.0F) {
                    forward *= 0.25F;
                }

                this.flyingSpeed = this.getSpeed() * 0.1F;
                if (this.isControlledByLocalInstance()) {
                    setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED));
                    super.travel(new Vec3(strafe, pTravelVector.y, forward));
                } else {
                    this.setDeltaMovement(Vec3.ZERO);
                }

            } else {
                this.flyingSpeed = 0.02F;
                super.travel(pTravelVector);
            }
        }
    }

    @Override
    public void onWanded(Player playerEntity) {
        Starbuncle carbuncle = new Starbuncle(playerEntity.level, true);
        Starbuncle.StarbuncleData data = this.data;
        carbuncle.setPos(getX() + 0.5, getY() + 1, getZ() + 0.5);
        carbuncle.data = data;
        carbuncle.restoreFromTag();
        playerEntity.level.addFreshEntity(carbuncle);
        carbuncle.onWanded(playerEntity);
        this.discard();
    }

    @Override
    public void positionRider(Entity passenger) {
        super.positionRider(passenger);
        if (passenger instanceof Mob mob && this.getControllingPassenger() == passenger && mob.zza > 0) {
            this.yBodyRot = mob.yBodyRot;
        }
        if (this.hasPassenger(passenger) && passenger instanceof Player) {
            double d0 = this.getY() + this.getPassengersRidingOffset() + passenger.getMyRidingOffset();
            float f1 = Mth.sin(this.yBodyRot * (0.017453292f));
            float f = Mth.cos(this.yBodyRot * (0.017453292f));
            passenger.setPos(getX() + f1 * 0.8, d0, this.getZ() - f* 0.8);
        }
    }

    @Override
    public double getPassengersRidingOffset() {
        return 1.4D;
    }

    @Override
    public boolean rideableUnderWater() {
        return true;
    }

    @Override
    public LivingEntity getControllingPassenger() {

        Entity entity = this.getFirstPassenger();
        if (entity instanceof LivingEntity) {
            return (LivingEntity) entity;
        }

        return null;
    }

    @Override
    protected boolean canRide(Entity pEntity) {
        return pEntity instanceof Player;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {

        if (player.level.isClientSide()) return InteractionResult.PASS;

        if (player.getMainHandItem().isEmpty() && !player.isShiftKeyDown()) {
            player.startRiding(this);
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

}
