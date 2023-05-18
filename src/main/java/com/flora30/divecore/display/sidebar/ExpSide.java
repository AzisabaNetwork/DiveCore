package com.flora30.divecore.display.sidebar;

import com.flora30.diveapin.data.player.PlayerData;
import com.flora30.diveapin.data.player.PlayerDataObject;
import com.flora30.divecore.level.Level;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ExpSide extends SideOption {
    @Override
    public String getLine(Player player) {
        PlayerData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId());
        int current = data.getLevelData().getExp();
        int next = Level.getNextExp(data.getLevelData().getLevel());
        if (data.getLevelData().getLevel() >= Level.getMaxLevel()) {
            return ChatColor.GOLD +"経験値 ： " + ChatColor.WHITE + current +ChatColor.GRAY + " / " + ChatColor.WHITE + "MAX";
        }
        return ChatColor.GOLD + "経験値 ： " + ChatColor.WHITE + current + ChatColor.GRAY + " / " + ChatColor.WHITE + next;
    }
}
