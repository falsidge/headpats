package dev.enjarai.headpats.mixin;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public class BipedEntityModelMixin<T extends LivingEntity> {
    @Shadow @Final public ModelPart rightArm;
    @Shadow @Final public ModelPart leftArm;
    @Shadow @Final public ModelPart head;
    @Unique
    protected float tickDelta;

    @Inject(
            method = "prepareMobModel(Lnet/minecraft/world/entity/LivingEntity;FFF)V",
            at = @At("HEAD")
    )
    private void grabTickDelta(T livingEntity, float f, float g, float h, CallbackInfo ci) {
        tickDelta = h;
    }

    @Inject(
            method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/geom/ModelPart;copyFrom(Lnet/minecraft/client/model/geom/ModelPart;)V"
            )
    )
    protected void positionModelParts(T livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {

    }
}
