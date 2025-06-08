package dev.enjarai.headpats;

import com.mojang.logging.LogUtils;
import dev.enjarai.headpats.net.*;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Headpats.MODID)
public class Headpats
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "headpats";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // Create the DeferredRegister for attachment types
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MODID);

    public static final Supplier<AttachmentType<PettingComponent>> PETTING_COPMPONENT = ATTACHMENT_TYPES.register(
            "petting_component", () -> AttachmentType.builder(() -> new PettingComponent()).build()
    );
    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public Headpats(IEventBus modEventBus, ModContainer modContainer)
    {
        ATTACHMENT_TYPES.register(modEventBus);
        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }


    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
    public static class ClientGameEvents
    {
        @SubscribeEvent
        public static void onTick(ClientTickEvent.Post tickEvent)
        {
            var client = Minecraft.getInstance();
            if (client.player != null) {
                ClientPayloadHandler.tick(client.level);
                var pettingComponent = client.player.getData(PETTING_COPMPONENT);
                pettingComponent.clientTick(client.player);
                if (pettingComponent.isPetting()) {
                    if (client.hitResult != null && client.crosshairPickEntity instanceof Player otherEntity) {
                        var hitPos = client.hitResult.getLocation().subtract(otherEntity.position());
                        double y = hitPos.y / (otherEntity.getScale() * otherEntity.getAgeScale());
                        double height = otherEntity.getBbHeight() / (otherEntity.getScale() * otherEntity.getAgeScale());
                            if (y > height - 0.5 && client.options.keyUse.isDown() && pettingComponent.isPetting(otherEntity)
                                && client.player.getMainHandItem().isEmpty() && client.player.distanceToSqr(otherEntity) < 1.5 * 1.5) {
                            return;
                        }
                    }
                    PacketDistributor.sendToServer(new PettingC2SPacket(-1));
                }
            }
        }
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.GAME)
    public static class ServerGameEvents
    {
        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onSpawn(EntityJoinLevelEvent event) {
            if (!event.getEntity().level().isClientSide() && event.getEntity() instanceof ServerPlayer player){
                ServerPayloadHandler.clearInvalid(event.getEntity().level());
                LOGGER.warn("{} sending to new player", ServerPayloadHandler.pattingPlayerMap);
                PacketDistributor.sendToPlayer(player, new PettingSyncS2CPacket(ServerPayloadHandler.pattingPlayerMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, (e)->e.getValue().stream().toList(), (x, y)->y))));
            }
        }
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD)
    public static class CommonModEvents {
        @SubscribeEvent
        public static void register(final RegisterPayloadHandlersEvent event){
            final PayloadRegistrar registrar = event.registrar("1");
            registrar.playToServer(
                    PettingC2SPacket.TYPE,
                    PettingC2SPacket.PACKET_CODEC,
                    ServerPayloadHandler::handleDataOnNetwork
            );
            registrar.playToClient(
                    PettingUpdateS2CPacket.TYPE,
                    PettingUpdateS2CPacket.PACKET_CODEC,
                    ClientPayloadHandler::handleDataOnMain
            );
            registrar.playToClient(
                    PettingSyncS2CPacket.TYPE,
                    PettingSyncS2CPacket.PACKET_CODEC,
                    ClientPayloadHandler::handleDataOnMain
            );
        }
    }
}
