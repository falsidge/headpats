package dev.enjarai.headpats.mixin;

import dev.enjarai.headpats.PetRendering;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public class PlayerEntityModelMixin<T extends LivingEntity> extends BipedEntityModelMixin<T> {
    @Override
    protected void positionModelParts(T livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {
        if (livingEntity instanceof Player player) {
            PetRendering.setPetAngles(player, tickDelta, rightArm, leftArm, head);
        }
    }
}
