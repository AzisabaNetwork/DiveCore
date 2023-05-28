package com.flora30.divecore.display.sidebar;

import com.flora30.divelib.data.player.NpcData;
import com.flora30.divelib.data.player.PlayerData;
import com.flora30.divelib.data.player.PlayerDataObject;
import com.flora30.divecore.data.PlayerDataMain;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MissionStorySide extends SideOption{

    public MissionStorySide(){
        super();
    }

    @Override
    public String getLine(Player player) {
        PlayerData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId());
        NpcData npcData = data.getNpcData();

        int storyId = npcData.getStoryMissionId();

        if (storyId == -1) {
            return null;
        }

        //Mission mission = QuestAPI.getMission("Story", storyId);
        //String title = mission.title;

        //return ChatColor.GOLD+"ミッション ‣ "+ChatColor.WHITE+ title;
        return null;
    }
}
