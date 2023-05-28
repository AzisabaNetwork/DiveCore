package com.flora30.divecore.help;

import com.flora30.divelib.data.player.PlayerData;
import com.flora30.divelib.data.player.PlayerDataObject;
import com.flora30.divelib.util.GuiItem;
import com.flora30.divecore.menu.MenuMain;
import com.flora30.diveconstant.data.Help;
import com.flora30.diveconstant.data.HelpObject;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class HelpGUI {
    public static Inventory getGui(Player player){

        Inventory inv = Bukkit.createInventory(null,45,"ヘルプ一覧");
        GuiItem.INSTANCE.grayBack(inv);

        PlayerData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId());
        if (data == null){
            return inv;
        }

        int i = 9;
        for (int helpId : HelpObject.INSTANCE.getHelpMap().keySet()){
            if (!data.getHelpIdSet().contains(helpId)){
                continue;
            }

            Help help = HelpObject.INSTANCE.getHelpMap().get(helpId);
            inv.setItem(i,help.getItem());
            i++;

            if (i >= 36){
                break;
            }
        }

        inv.setItem(44,GuiItem.INSTANCE.getReturn());

        return inv;
    }

    public static void onClick(InventoryClickEvent e){
        e.setCancelled(true);
        if (e.getClickedInventory() == null) return;
        Bukkit.getLogger().info("help slot = "+e.getSlot());
        if (e.getSlot() == 44) {
            Player player = (Player) e.getWhoClicked();
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK,1,1);
            MenuMain.openMenu(player);
        }
    }
}
