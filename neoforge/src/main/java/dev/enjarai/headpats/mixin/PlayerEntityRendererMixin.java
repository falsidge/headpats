package dev.enjarai.headpats.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.enjarai.headpats.PetRendering;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public class PlayerEntityRendererMixin {
    @Inject(
            method = "renderHand",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/AbstractClientPlayer;getSkin()Lnet/minecraft/client/resources/PlayerSkin;"
            )
    )
    private void fixFunnyAnimation(PoseStack matrices, MultiBufferSource vertexConsumers, int light, AbstractClientPlayer player, ModelPart arm, ModelPart sleeve, CallbackInfo ci) {
        PetRendering.fixFirstPersonAngles(player, Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true), arm, sleeve);
    }
}
