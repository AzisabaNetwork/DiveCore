package com.flora30.divecore.display.sidebar;

import com.flora30.diveapi.data.Mission;
import com.flora30.diveapi.data.player.NpcData;
import com.flora30.diveapi.plugins.QuestAPI;
import com.flora30.divecore.data.PlayerDataMain;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MissionStorySide extends SideOption{

    public MissionStorySide(){
        super();
    }

    @Override
    public String getLine(Player player) {
        NpcData data = PlayerDataMain.getPlayerData(player.getUniqueId()).npcData;

        int storyId = data.storyMissionId;

        if (storyId == -1) {
            return null;
        }

        Mission mission = QuestAPI.getMission("Story", storyId);

        String title = mission.title;

        return ChatColor.GOLD+"ミッション ‣ "+ChatColor.WHITE+ title;
    }
}
