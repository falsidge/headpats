package dev.enjarai.headpats;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class PetRendering {
    public static int pettingMultiplier;

    public static void modifyHandMatrix(Player player, float tickDelta, PoseStack matrices) {
        var petting = player.getData(Headpats.PETTING_COPMPONENT);
        if (petting.pettingMultiplier > 0) {
            var petTime = Mth.lerp(tickDelta, (float) petting.prevPettingTicks, (float) petting.pettingTicks);
            var multiplier = Mth.lerp(tickDelta, petting.prevPettingMultiplier, petting.pettingMultiplier);
            matrices.translate(player.getMainArm() == HumanoidArm.RIGHT ? 1 : -1, -1, 0);
            matrices.mulPose(Axis.YP.rotationDegrees(Mth.sin(petTime * 0.4f) * 16.0f * multiplier));
            matrices.translate(player.getMainArm() == HumanoidArm.RIGHT ? -1 : 1, 1, 0);
        }
    }

    public static void setPetAngles(Player player, float tickDelta, ModelPart rightArm, ModelPart leftArm, ModelPart head) {
        var petting = player.getData(Headpats.PETTING_COPMPONENT);

        if (petting.pettingMultiplier > 0) {
            var petTime = Mth.lerp(tickDelta, (float) petting.prevPettingTicks, (float) petting.pettingTicks);
            var multiplier = Mth.lerp(tickDelta, petting.prevPettingMultiplier, petting.pettingMultiplier);

            if (player.getMainArm() == HumanoidArm.RIGHT) {
                rightArm.xRot = rightArm.xRot * (1 - multiplier) - multiplier * 2.1f;
                rightArm.yRot = rightArm.yRot * (1 - multiplier) - Mth.sin(petTime * 0.4f) * multiplier * 0.5f;
            } else {
                leftArm.xRot = leftArm.xRot * (1 - multiplier) - multiplier * 2.1f;
                leftArm.yRot = leftArm.yRot * (1 - multiplier) - Mth.sin(petTime * 0.4f) * multiplier * 0.5f;
            }
        }

        if (petting.pettedMultiplier > 0) {
            var petTime = Mth.lerp(tickDelta, (float) petting.prevPettedTicks, (float) petting.pettedTicks);
            var multiplier = Mth.lerp(tickDelta, petting.prevPettedMultiplier, petting.pettedMultiplier);

            head.xRot += multiplier * 0.4f;
            head.zRot = -Mth.sin(petTime * 0.4f) * multiplier * 0.15f;
        } else {
            head.zRot = 0;
        }
    }

    public static void fixFirstPersonAngles(Player player, float tickDelta, ModelPart arm, ModelPart sleeve) {
        var petting = player.getData(Headpats.PETTING_COPMPONENT);

        var multiplier = 1 - Mth.lerp(tickDelta, petting.prevPettingMultiplier, petting.pettingMultiplier);
        arm.yRot *= multiplier;
        arm.zRot *= multiplier;
        sleeve.yRot *= multiplier;
        sleeve.zRot *= multiplier;
    }

    public static @Nullable Float getCameraRoll(Player player, float tickDelta) {
        var petting = player.getData(Headpats.PETTING_COPMPONENT);
        var finalFirstPersonSwayStrength = Config.firstPersonSwayStrength * Minecraft.getInstance().options.screenEffectScale().get();

        if (petting.pettedMultiplier > 0 && finalFirstPersonSwayStrength > 0) {
            var petTime = Mth.lerp(tickDelta, (float) petting.prevPettedTicks, (float) petting.pettedTicks);
            var multiplier = Mth.lerp(tickDelta, petting.prevPettedMultiplier, petting.pettedMultiplier);

            return -Mth.sin(petTime * 0.4f) * multiplier * 0.1f * (float) finalFirstPersonSwayStrength;
        }

        return null;
    }
}
