package dev.enjarai.headpats.net;

import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.*;


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
    public static void clearInvalid(Level level )
    {
        Map<Integer, List<Integer>> updatedMap = new HashMap<>();
        List<Integer> toRemove = new ArrayList<>();
        for (var entry : ServerPayloadHandler.petterMap.entrySet()) {
            if (level.getEntity(entry.getKey()) == null)
            {
                toRemove.add(entry.getKey());
                if (ServerPayloadHandler.pattingPlayerMap.containsKey(entry.getValue()))
                {
                    ServerPayloadHandler.pattingPlayerMap.get(entry.getValue()).remove(entry.getKey());
                }
            }
        }
        for (int removed : toRemove)
        {
            ServerPayloadHandler.petterMap.remove(removed);
        }
        List<Integer> toRemove1 = new ArrayList<>();
        for (var entry : ServerPayloadHandler.pattingPlayerMap.entrySet())
        {
            if (entry.getValue().isEmpty() || level.getEntity(entry.getKey()) == null)
            {
                toRemove1.add(entry.getKey());
            }
            else
            {
                updatedMap.put(entry.getKey(), entry.getValue().stream().toList());
            }
        }
        for (int removed : toRemove1)
        {
            ServerPayloadHandler.pattingPlayerMap.remove(removed);
        }
    }
}