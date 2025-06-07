package dev.enjarai.headpats.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
	@Shadow public abstract int getId();

	@Shadow public abstract double distanceToSqr(Entity entity);

	@Shadow public abstract float getYRot();

	@Shadow public abstract float getBbHeight();

	@Inject(
			at = @At("HEAD"),
			method = "interactAt",
			cancellable = true
	)
	protected void interact(Player player, Vec3 hitPos, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {

	}
}