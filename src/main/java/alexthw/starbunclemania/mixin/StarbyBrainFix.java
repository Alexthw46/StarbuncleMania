package alexthw.starbunclemania.mixin;

import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Starbuncle.class, remap = false)
public abstract class StarbyBrainFix {

    @Shadow
    protected abstract void reloadGoals();

    @Inject(method = "onWanded", at = @At("TAIL"), remap = false)
    public void onWanded(Player player, CallbackInfo ci) {
        if (!player.level().isClientSide()) {
            reloadGoals();
        }
    }

}

