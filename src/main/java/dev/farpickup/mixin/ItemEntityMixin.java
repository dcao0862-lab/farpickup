package dev.farpickup.mixin;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    private static final double RANGE = 8.0;
    private static final double SPEED = 0.45;

    @Inject(method = "tick", at = @At("HEAD"))
    private void farpickup$pull(CallbackInfo ci) {
        ItemEntity self = (ItemEntity) (Object) this;
        Level level = self.level();

        if (level.isClientSide()) {
            return;
        }
        if (self.hasPickUpDelay()) {
            return;
        }

        Player player = level.getNearestPlayer(self, RANGE);
        if (player == null || player.isSpectator() || player.isShiftKeyDown()) {
            return;
        }
        if (player.getInventory().getFreeSlot() == -1) {
            return;
        }

        Vec3 target = player.position().add(0.0, player.getBbHeight() * 0.5, 0.0);
        Vec3 delta = target.subtract(self.position());
        double dist = delta.length();
        if (dist < 1.0E-4) {
            return;
        }

        double step = Math.min(SPEED, dist);
        self.setDeltaMovement(delta.scale(step / dist));
    }
}
