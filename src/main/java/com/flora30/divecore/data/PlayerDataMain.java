package com.flora30.divecore.data;

import com.flora30.diveapi.data.PlayerData;
import com.flora30.divecore.DiveCore;

import java.util.*;

public class PlayerDataMain {
    // プレイヤーデータを入れている
    private static final Map<UUID, PlayerData> playerDataMap = new HashMap<>();

    private static final Map<String,UUID> playerIdMap = new HashMap<>();

    public static final Set<UUID> loadedPlayerSet = new HashSet<>();

    public static void setPlayerData(UUID id, PlayerData data){
        if (DiveCore.disabling) return;
        playerDataMap.put(id,data);
    }

    public static PlayerData getPlayerData(UUID id){
        return playerDataMap.get(id);
    }

    public static void removePlayerData(UUID id) {
        if (DiveCore.disabling) return;
        playerDataMap.remove(id);
    }

    public static Set<UUID> getAllPlayers(){
        return playerDataMap.keySet();
    }


    public static Set<String> getPlayerNameSet(){
        return playerIdMap.keySet();
    }

    public static UUID getPlayerID(String name){
        return playerIdMap.get(name);
    }

    public static void putPlayerID(String name, UUID id){
        playerIdMap.put(name,id);
    }
}
