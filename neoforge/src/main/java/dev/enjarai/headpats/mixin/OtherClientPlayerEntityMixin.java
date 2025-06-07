package dev.enjarai.headpats.mixin;

import dev.enjarai.headpats.Headpats;
import dev.enjarai.headpats.net.PettingC2SPacket;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RemotePlayer.class)
public abstract class OtherClientPlayerEntityMixin extends LivingEntityMixin {
    @Override
    protected void interact(Player player, Vec3 hitPos, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        double y = hitPos.y / (getScale() * getAgeScale());
        double height = getBbHeight() / (getScale() * getAgeScale());
        if (y > height - 0.5 && player instanceof LocalPlayer clientPlayer && player.getMainHandItem().isEmpty() && distanceToSqr(player) < 1.5*1.5) {
            if (((Player) (Object) this).getData(Headpats.PETTING_COPMPONENT).isPetting(player))
                return;
            PacketDistributor.sendToServer(new PettingC2SPacket(((Player) (Object) this).getId()));
            cir.setReturnValue(InteractionResult.sidedSuccess(false));
        }
    }
    @Inject(
            at = @At("HEAD"),
            method = "tick"
    )
    public void tick(CallbackInfo ci)
    {
        ((Player) (Object) this).getData(Headpats.PETTING_COPMPONENT).clientTick(((Player) ((Object) this)));
    }
}
