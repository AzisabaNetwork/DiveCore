package com.flora30.divecore.display.sidebar;

import com.flora30.diveapi.plugins.CoreAPI;
import com.flora30.diveapi.plugins.QuestAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class AreaSide extends SideOption{
    @Override
    public String getLine(Player player) {
        try {
            String name = QuestAPI.getStory(CoreAPI.getPlayerData(player.getUniqueId()).layerData.layer).displayName;
            return ChatColor.GOLD+"エリア : " +ChatColor.WHITE+ name;
        }catch (NullPointerException e){
            return ChatColor.GOLD+"エリア : "+ChatColor.WHITE+"なし";
        }
    }
}
