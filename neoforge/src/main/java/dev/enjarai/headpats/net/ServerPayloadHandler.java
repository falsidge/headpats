package dev.enjarai.headpats.net;

import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class ServerPayloadHandler {
    static public Map<Integer, Set<Integer>> pattingPlayerMap = new HashMap<>();
    static public Map<Integer, Integer> petterMap = new HashMap<>();

    public static void handleDataOnNetwork(final PettingC2SPacket data, final IPayloadContext context) {
        if (data.entityId() == -1)
        {
            var otherPlayerId = petterMap.get(context.player().getId());
            if (pattingPlayerMap.containsKey(otherPlayerId)) {
                pattingPlayerMap.get(otherPlayerId).remove(context.player().getId());
                PacketDistributor.sendToAllPlayers(new PettingUpdateS2CPacket(context.player().getId(), -1));
            }
        }
        else
        {
            if (pattingPlayerMap.containsKey(data.entityId()) && pattingPlayerMap.get(data.entityId()).contains(context.player().getId()))
                return;
            pattingPlayerMap.computeIfAbsent(data.entityId(),(k)-> new HashSet<>())
                    .add(context.player().getId());
            petterMap.put(context.player().getId(), data.entityId());
            PacketDistributor.sendToAllPlayers(new PettingUpdateS2CPacket(context.player().getId(), data.entityId()));
        }
    }
}