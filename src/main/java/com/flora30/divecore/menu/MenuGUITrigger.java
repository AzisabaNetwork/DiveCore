package com.flora30.divecore.menu;

import com.flora30.divecore.Listeners;
import com.flora30.divecore.tools.SoundUtil;
import com.flora30.divecore.tools.type.DiveSound;
import com.flora30.divelib.data.MenuSlot;
import com.flora30.divelib.event.MenuClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.Map;

public class MenuGUITrigger {

    static Map<Integer,MenuSlot> inventorySlots = getInventorySlots();
    private static Map<Integer,MenuSlot> getInventorySlots(){
        Map<Integer,MenuSlot> before =  new HashMap<>();
        before.put(10,MenuSlot.Slot1);
        before.put(12,MenuSlot.Slot2);
        before.put(14,MenuSlot.Slot3);
        before.put(16,MenuSlot.Slot4);
        before.put(28,MenuSlot.Slot5);
        before.put(30,MenuSlot.Slot6);
        before.put(32,MenuSlot.Slot7);
        before.put(34,MenuSlot.Slot8);
        return before;
    }

    // アイテムを持つ状況が発生しないのでDrag停止は必要なし
    public static void onClick(InventoryClickEvent event){
        event.setCancelled(true);
        if (event.getClickedInventory() == null){
            return;
        }
        if (event.getClickedInventory() == event.getView().getBottomInventory()){
            return;
        }

        Player player = (Player) event.getWhoClicked();
        if (inventorySlots.containsKey(event.getSlot())){
            MenuClickEvent menuClickEvent = new MenuClickEvent(player,inventorySlots.get(event.getSlot()),event.getCurrentItem());
            Listeners.callEvent(menuClickEvent);
            if (menuClickEvent.getUseClickSound()){
                SoundUtil.playSound(player, DiveSound.GuiClick, 1.0);
            }
        }
    }
}
