package dev.enjarai.headpats.net;

import dev.enjarai.headpats.Headpats;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class ClientPayloadHandler {
    @Nullable
    public static Map<Integer, List<Integer>> playerMap = null;
    public static int retries=  0;

    public static void handleDataOnMain(final PettingSyncS2CPacket data, final IPayloadContext context) {
        playerMap = data.pattingPlayerMap();
        retries = 5;
    }

    public static void tick(Level level)
    {
        if (playerMap == null)
            return;
        Headpats.LOGGER.warn("received player map {}  {}", retries, playerMap);
        boolean allSucceed = true;
        for (var entry : playerMap.entrySet())
        {
            var entity = level.getEntity(entry.getKey());
            Headpats.LOGGER.warn("considering  {}", entry.getKey());

            if (entity != null) {
                Headpats.LOGGER.warn("adding player map {} {}", entry.getKey(), entry.getValue());
                var component = entity.getData(Headpats.PETTING_COPMPONENT);
                component.incomingPetters = entry.getValue().size();
                for (var entityId : entry.getValue())
                {
                    var otherentity = level.getEntity(entityId);
                    if (otherentity != null) {
                        var otherComponent = otherentity.getData(Headpats.PETTING_COPMPONENT);
                        otherComponent.petting = entry.getKey();
                    }
                    else
                    {
                        allSucceed = false;
                    }
                }
            }
            else
            {
                allSucceed = false;
            }
        }
        retries -= 1;
        if (allSucceed || retries <= 0) {
            playerMap = null;
            retries = 0;
        }
    }

    public static void handleDataOnMain(final PettingUpdateS2CPacket data, final IPayloadContext context) {
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