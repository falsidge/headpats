package dev.enjarai.headpats.net;

import dev.enjarai.headpats.Headpats;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record PettingC2SPacket(int entityId) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PettingC2SPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Headpats.MODID, "send_pats"));
    public static final StreamCodec<FriendlyByteBuf, PettingC2SPacket> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, PettingC2SPacket::entityId,
            PettingC2SPacket::new
    );


    @Override
    public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
