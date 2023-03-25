package com.flora30.divecore.help;

import com.flora30.diveapi.data.PlayerData;
import com.flora30.diveapi.event.HelpEvent;
import com.flora30.diveapi.tools.HelpType;
import com.flora30.divecore.data.PlayerDataMain;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class HelpMain {
    public static Map<Integer,Help> helpMap = new HashMap<>();

    public static void addHelp(Player player, int id){
        PlayerData data = PlayerDataMain.getPlayerData(player.getUniqueId());
        if (!helpMap.containsKey(id)){
            return;
        }

        data.helpIdSet.add(id);
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_TRADE,1,1);
        player.sendMessage(ChatColor.GREEN+"ヘルプ「"+helpMap.get(id).getTitle()+ChatColor.GREEN+"」を獲得しました");
    }

    public static void openGUI(Player player){
        Bukkit.getPluginManager().callEvent(new HelpEvent(player,HelpType.HelpGUI));
        player.openInventory(HelpGUI.getGui(player));
    }

    public static void onHelpTrigger(HelpEvent e) {
        PlayerData data = PlayerDataMain.getPlayerData(e.getPlayer().getUniqueId());
        for (int key : helpMap.keySet()) {
            if (data.helpIdSet.contains(key)) continue;
            Help help = helpMap.get(key);

            if (help.trigger == e.getType()) {
                data.helpIdSet.add(key);
                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_TRADE,1,1);
                e.getPlayer().sendMessage(ChatColor.GREEN+"ヘルプ「"+help.getTitle()+ChatColor.GREEN+"」を獲得しました");
            }
        }
    }
}
