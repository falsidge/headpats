package dev.enjarai.headpats.net;

import dev.enjarai.headpats.Headpats;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record PettingSyncS2CPacket(
       Map<Integer, List<Integer>> pattingPlayerMap) implements CustomPacketPayload {

    public static final Type<PettingSyncS2CPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Headpats.MODID, "sync_pats"));
    public static final StreamCodec<FriendlyByteBuf, PettingSyncS2CPacket> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(
                    HashMap::new,
                    ByteBufCodecs.INT,
                    ByteBufCodecs.INT.apply(ByteBufCodecs.list())
            ), PettingSyncS2CPacket::pattingPlayerMap,
            PettingSyncS2CPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
