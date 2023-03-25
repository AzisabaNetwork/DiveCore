package com.flora30.divecore.display.sidebar;

import com.flora30.diveapi.data.Mission;
import com.flora30.diveapi.data.player.NpcData;
import com.flora30.diveapi.plugins.QuestAPI;
import com.flora30.divecore.data.PlayerDataMain;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MissionMobSide extends SideOption{

    @Override
    public String getLine(Player player) {
        NpcData data = PlayerDataMain.getPlayerData(player.getUniqueId()).npcData;

        int mobId = data.mobMissionId;

        if (mobId == -1) {
            return null;
        }

        Mission mission = QuestAPI.getMission("Mob", mobId);

        String title = mission.title;

        return ChatColor.GOLD+"ミッション ‣ " +ChatColor.WHITE+ title;
    }
}
