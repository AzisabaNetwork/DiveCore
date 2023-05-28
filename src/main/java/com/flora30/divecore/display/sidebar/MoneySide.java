package com.flora30.divecore.display.sidebar;

import com.flora30.divelib.data.player.PlayerData;
import com.flora30.divelib.data.player.PlayerDataObject;
import com.flora30.divecore.data.PlayerDataMain;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MoneySide extends SideOption{

    @Override
    public String getLine(Player player) {
        PlayerData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId());
        int money = data.getMoney();
        return ChatColor.GOLD+"所持金 : " + ChatColor.WHITE + money;
    }
}