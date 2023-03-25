package com.flora30.divecore.level.gui.trigger;

import com.flora30.divecore.menu.MenuMain;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class StatusGUITrigger {
    public static void onInventoryClick(InventoryClickEvent e) {
        //無効化
        e.setCancelled(true);
        //GUI外のクリックはキャンセル
        if (e.getClickedInventory() == null) {
            return;
        }
        if (e.getClickedInventory() == e.getView().getBottomInventory()){
            return;
        }

        if (e.getSlot() == 35) {
            Player player = (Player) e.getWhoClicked();
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK,1,1);
            MenuMain.openMenu(player);
        }
    }
}
