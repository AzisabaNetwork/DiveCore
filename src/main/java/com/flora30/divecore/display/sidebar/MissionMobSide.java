package com.flora30.divecore.display.sidebar;

import com.flora30.diveapi.data.Mission;
import com.flora30.diveapi.data.player.NpcData;
import com.flora30.diveapi.plugins.QuestAPI;
import com.flora30.divelib.data.player.NpcData;
import com.flora30.divelib.data.player.PlayerData;
import com.flora30.divelib.data.player.PlayerDataObject;
import com.flora30.divecore.data.PlayerDataMain;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MissionMobSide extends SideOption{

    @Override
    public String getLine(Player player) {
        PlayerData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId());
        NpcData npcData = data.getNpcData();

        int mobId = npcData.getMobMissionId();

        if (mobId == -1) {
            return null;
        }

        //Mission mission = QuestAPI.getMission("Mob", mobId);

        //String title = mission.title;

        //return ChatColor.GOLD+"ミッション ‣ " +ChatColor.WHITE+ title;]

        return null;
    }
}
