package dev.enjarai.headpats;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
@EventBusSubscriber(modid = Headpats.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.DoubleValue FIRST_PERSON_SWAY_STRENGTH = BUILDER
            .defineInRange("firstPersonSwayStrength", 1.0, 0, Double.MAX_VALUE);

    private static final ModConfigSpec.BooleanValue PETTED_PLAYERS_PURR = BUILDER
            .define("pettedPlayersPurr", false);


    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean pettedPlayersPurr = false;
    public static float firstPersonSwayStrength = 1f;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        firstPersonSwayStrength = FIRST_PERSON_SWAY_STRENGTH.get().floatValue();
        pettedPlayersPurr = PETTED_PLAYERS_PURR.get();
    }
}
