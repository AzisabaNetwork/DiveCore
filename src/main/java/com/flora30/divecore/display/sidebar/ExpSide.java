package com.flora30.divecore.display.sidebar;

import com.flora30.diveapi.data.PlayerData;
import com.flora30.divecore.data.PlayerDataMain;
import com.flora30.divecore.level.Level;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ExpSide extends SideOption {
    @Override
    public String getLine(Player player) {
        PlayerData data = PlayerDataMain.getPlayerData(player.getUniqueId());
        int current = data.levelData.exp;
        int next = Level.getNextExp(data.levelData.level);
        if (data.levelData.level >= Level.getMaxLevel()) {
            return ChatColor.GOLD +"経験値 ： " + ChatColor.WHITE + current +ChatColor.GRAY + " / " + ChatColor.WHITE + "MAX";
        }
        return ChatColor.GOLD + "経験値 ： " + ChatColor.WHITE + current + ChatColor.GRAY + " / " + ChatColor.WHITE + next;
    }
}
