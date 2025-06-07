package dev.enjarai.headpats.net;

import dev.enjarai.headpats.Headpats;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record PettingUpdateS2CPacket(
       Integer petter, Integer petting) implements CustomPacketPayload {

    public static final Type<PettingUpdateS2CPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Headpats.MODID, "update_pats"));
    public static final StreamCodec<FriendlyByteBuf, PettingUpdateS2CPacket> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, PettingUpdateS2CPacket::petter,
            ByteBufCodecs.INT, PettingUpdateS2CPacket::petting,
            PettingUpdateS2CPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
