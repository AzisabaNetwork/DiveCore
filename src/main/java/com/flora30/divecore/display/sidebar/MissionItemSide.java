package com.flora30.divecore.display.sidebar;

import com.flora30.diveapi.data.Mission;
import com.flora30.diveapi.data.player.NpcData;
import com.flora30.diveapi.plugins.QuestAPI;
import com.flora30.diveapin.data.player.NpcData;
import com.flora30.diveapin.data.player.PlayerData;
import com.flora30.diveapin.data.player.PlayerDataObject;
import com.flora30.divecore.data.PlayerDataMain;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MissionItemSide extends SideOption{

    @Override
    public String getLine(Player player) {
        PlayerData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId());
        NpcData npcData = data.getNpcData();

        int itemId = npcData.getItemMissionId();

        if (itemId == -1) {
            return null;
        }

        //Mission mission = QuestAPI.getMission("Item", itemId);

        //String title = mission.title;

        //return ChatColor.GOLD+"ミッション ‣ " +ChatColor.WHITE+ title;

        return null;
    }
}
