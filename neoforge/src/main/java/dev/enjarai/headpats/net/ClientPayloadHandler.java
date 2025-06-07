package dev.enjarai.headpats.net;

import dev.enjarai.headpats.Headpats;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.*;

public class ClientPayloadHandler {
    public static Set<Integer> petted = new HashSet<>();
    public static Set<Integer> petters = new HashSet<>();
    public static void handleDataOnMain(final PettingSyncS2CPacket data, final IPayloadContext context) {
//
//        for (var player : context.player().level().players())
//        {
//
//        }
        Level level = context.player().level();
        var playermap = data.pattingPlayerMap();
        for (var entry : playermap.entrySet())
        {
            var entity = level.getEntity(entry.getKey());
            if (entity != null) {
                var component = entity.getData(Headpats.PETTING_COPMPONENT);
                component.incomingPetters = entry.getValue().size();
                for (var entityId : entry.getValue())
                {
                    var otherentity = level.getEntity(entityId);
                    if (otherentity != null) {
                        var othercomponent = entity.getData(Headpats.PETTING_COPMPONENT);
                        othercomponent.petting = entry.getKey();
                    }
                }
            }
        }
    }
    public static void handleDataOnMain(final PettingUpdateS2CPacket data, final IPayloadContext context) {
        // Do something with the data, on the main thread
        Level level = context.player().level();
        if (data.petting() == -1)
        {

            var entity1 = level.getEntity(data.petter());
            if (entity1 != null) {
                int id2 = entity1.getData(Headpats.PETTING_COPMPONENT).petting;
                var entity2 = level.getEntity(id2);
                if (entity2 != null)
                {
                    entity2.getData(Headpats.PETTING_COPMPONENT).incomingPetters -= 1;
                }
                entity1.getData(Headpats.PETTING_COPMPONENT).petting = -1;
            }
        }
        else {
            var entity1 = level.getEntity(data.petter());
            if (entity1 != null) {
                entity1.getData(Headpats.PETTING_COPMPONENT).petting = data.petting();
            }
            var entity2 =  level.getEntity(data.petting());
            if (entity2 != null) {
               entity2.getData(Headpats.PETTING_COPMPONENT).incomingPetters += 1;
            }
        }
    }
}