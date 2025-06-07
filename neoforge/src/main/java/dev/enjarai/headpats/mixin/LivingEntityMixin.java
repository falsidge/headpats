package dev.enjarai.headpats.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import dev.enjarai.headpats.Headpats;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends EntityMixin {
    @Shadow public abstract float getScale();

    @Shadow public abstract float getAgeScale();

    @Inject(
            method = "Lnet/minecraft/world/entity/LivingEntity;tick()V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/entity/LivingEntity;attackAnim:F"
            )
    )
    private void turnBodyWhenPetting(CallbackInfo ci, @Local(ordinal = 1) LocalFloatRef g) {
        if (((LivingEntity) (Object) (this)).hasData(Headpats.PETTING_COPMPONENT)) {
            var component = ((LivingEntity) (Object) (this)).getData(Headpats.PETTING_COPMPONENT);
            if (component.isPetting()) {
                g.set(getYRot());
            }
        }
    }
}
