package com.flora30.divecore.display.sidebar;

import com.flora30.diveapi.data.PlayerData;
import com.flora30.divecore.data.PlayerDataMain;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MoneySide extends SideOption{

    @Override
    public String getLine(Player player) {
        PlayerData data = PlayerDataMain.getPlayerData(player.getUniqueId());
        int money = data.money;
        return ChatColor.GOLD+"所持金 : " + ChatColor.WHITE + money;
    }
}