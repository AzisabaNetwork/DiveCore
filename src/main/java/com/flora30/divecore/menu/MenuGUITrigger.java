package com.flora30.divecore.menu;

import com.flora30.diveapi.plugins.ItemAPI;
import com.flora30.diveapi.plugins.RegionAPI;
import com.flora30.divecore.Listeners;
import com.flora30.divecore.api.event.tutorial.PointLookEvent;
import com.flora30.divecore.help.HelpMain;
import com.flora30.divecore.level.gui.StatusGUI;
import com.flora30.divecore.tools.SoundUtil;
import com.flora30.divecore.tools.type.DiveSound;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class MenuGUITrigger {

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

        switch (event.getSlot()) {
            case 10 -> {
                SoundUtil.playSound(player, DiveSound.GuiClick, 1.0);
                // ステータスGUIを開く
                StatusGUI.open(player);
                Listeners.callEvent(new PointLookEvent(player));
            }
            case 12 -> {
                SoundUtil.playSound(player, DiveSound.GuiClick, 1.0);
                player.openInventory(ItemAPI.getCraftListGui(player));
            }

            case 14 -> {
                // ファストトラベル可能な場合は、MaterialがIRON_BOOTS
                ItemStack icon = event.getClickedInventory().getItem(14);
                if (icon == null) return;
                if (icon.getType() == Material.IRON_BOOTS) {
                    //クリック音
                    SoundUtil.playSound(player, DiveSound.GuiClick, 1.0);
                    RegionAPI.openTravelGUI(player);
                }
            }
            case 16 -> {
                // 帰還可能な場合は、MaterialがBarrierではない
                ItemStack icon = event.getClickedInventory().getItem(16);
                if (icon == null) return;
                if (icon.getType() != Material.BARRIER) {
                    //クリック音
                    SoundUtil.playSound(player, DiveSound.GuiClick, 1.0);
                    RegionAPI.returnTeleport(player);
                }
            }
            case 28 -> {
                SoundUtil.playSound(player, DiveSound.GuiClick, 1.0);
                HelpMain.openGUI(player);
            }
            case 34 -> {
                SoundUtil.playSound(player, DiveSound.GuiClick, 1.0);
                player.openInventory(DeathGUI.getGui());
            }
        }
    }
}
