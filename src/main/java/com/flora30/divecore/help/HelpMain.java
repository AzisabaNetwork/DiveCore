package com.flora30.divecore.help;

import com.flora30.divelib.data.player.PlayerData;
import com.flora30.divelib.data.player.PlayerDataObject;
import com.flora30.divelib.event.HelpEvent;
import com.flora30.divelib.event.HelpType;
import com.flora30.diveconstant.data.Help;
import com.flora30.diveconstant.data.HelpObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class HelpMain {

    // こっちは直接追加用
    public static void addHelp(Player player, int id){
        PlayerData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId());
        if (!HelpObject.INSTANCE.getHelpMap().containsKey(id)){
            return;
        }

        data.getHelpIdSet().add(id);
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_TRADE,1,1);
        player.sendMessage(ChatColor.GREEN+"ヘルプ「"+HelpObject.INSTANCE.getHelpMap().get(id).getTitle()+ChatColor.GREEN+"」を獲得しました");
    }


    public static void openGUI(Player player){
        Bukkit.getPluginManager().callEvent(new HelpEvent(player, HelpType.HelpGUI));
        player.openInventory(HelpGUI.getGui(player));
    }

    public static void onHelpTrigger(HelpEvent e) {
        PlayerData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(e.getPlayer().getUniqueId());
        for (int key : HelpObject.INSTANCE.getHelpMap().keySet()) {
            if (data.getHelpIdSet().contains(key)) continue;
            Help help = HelpObject.INSTANCE.getHelpMap().get(key);

            if (help.getTrigger() == e.getType()) {
                data.getHelpIdSet().add(key);
                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_TRADE,1,1);
                e.getPlayer().sendMessage(ChatColor.GREEN+"ヘルプ「"+help.getTitle()+ChatColor.GREEN+"」を獲得しました");
            }
        }
    }
}
