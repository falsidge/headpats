package dev.enjarai.headpats;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class PettingComponent {
    public int petting = -1;
    public int incomingPetters = 0;

    public int prevPettingTicks;
    public int pettingTicks;
    public float prevPettingMultiplier;
    public float pettingMultiplier;
    public int prevPettedTicks;
    public int pettedTicks;
    public float prevPettedMultiplier;
    public float pettedMultiplier;
    public boolean isPetting() {
        return petting != -1;
    }

    public boolean isBeingPet() {
        return incomingPetters > 0;
    }

    public void clientTick(Player player) {
        prevPettingTicks = pettingTicks;
        prevPettingMultiplier = pettingMultiplier;
        if (isPetting()) {
            pettingTicks++;
            pettingMultiplier += (1 - pettingMultiplier) * 0.3f;
        } else {
            pettingMultiplier -= pettingMultiplier * 0.3f;
            if (pettingMultiplier < 0.01f) {
                pettingMultiplier = 0;
                pettingTicks = 0;
            }
        }

        // Imagine clean code :clueless:
        prevPettedTicks = pettedTicks;
        prevPettedMultiplier = pettedMultiplier;
        if (isBeingPet()) {
            if (pettedTicks % 40 == 0 && Config.pettedPlayersPurr) {
                player.level().playLocalSound(player, SoundEvents.CAT_PURR,
                        SoundSource.PLAYERS, 1f, player.getVoicePitch());
            }

            pettedTicks++;
            pettedMultiplier += (1 - pettedMultiplier) * 0.3f;
        } else {
            pettedMultiplier -= pettedMultiplier * 0.3f;
            if (pettedMultiplier < 0.01f) {
                pettedMultiplier = 0;
                pettedTicks = 0;
            }
        }
    }
    public boolean isPetting(@Nullable Entity player) {
        return petting != -1 && player != null && player.getId() == petting;
    }
}
