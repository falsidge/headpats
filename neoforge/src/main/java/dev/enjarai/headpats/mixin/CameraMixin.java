package dev.enjarai.headpats.mixin;

import dev.enjarai.headpats.PetRendering;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public class CameraMixin {
    @Unique
    private Float newRoll = null;

    @Inject(
            method = "setup",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Camera;setRotation(FFF)V",
                    ordinal = 0
            )
    )
    private void calculateRoll(BlockGetter area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        if (focusedEntity instanceof Player player) {
            newRoll = PetRendering.getCameraRoll(player, tickDelta);
        }
    }

    @ModifyArg(
            method = "setRotation(FFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/joml/Quaternionf;rotationYXZ(FFF)Lorg/joml/Quaternionf;"
            ),
            index = 2
    )
    private float applyRoll(float angleY) {
        if (newRoll != null) {
            return newRoll + angleY;
        }
        return angleY;
    }
}
