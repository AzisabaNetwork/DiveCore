package com.flora30.divecore.base.gui.trigger;

import com.flora30.data.Base;
import com.flora30.data.BaseObject;
import com.flora30.diveapi.plugins.ItemAPI;
import com.flora30.divecore.base.BaseMain;
import com.flora30.divecore.base.gui.BaseGUI;
import com.flora30.divecore.base.gui.BaseUpgradeGUI;
import com.flora30.divecore.level.gui.SetPointGUI;
import com.flora30.divecore.tools.SoundUtil;
import com.flora30.divecore.tools.type.DiveSound;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class BaseGUITrigger {
    // プレイヤーが何も持たないのでDrag検知なし
    public static void onClick(InventoryClickEvent event){
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();

        if (event.getClickedInventory() == null){
            return;
        }

        ItemStack item = event.getClickedInventory().getItem(event.getSlot());

        if (item == null){
            return;
        }

        ItemStack camp = event.getClickedInventory().getItem(22);
        if (camp == null || camp.getItemMeta() == null){
            return;
        }
        int baseId = camp.getItemMeta().getCustomModelData();


        switch (item.getType()) {
            case FIRE_CHARGE -> {
                SoundUtil.playSound(player, DiveSound.GuiClick,1.0);
                SetPointGUI.open(player);
            }
            case ENDER_CHEST -> {
                SoundUtil.playSound(player, DiveSound.GuiClick,1.0);
                ItemAPI.openEnderGUI(player);
            }
            case CAMPFIRE -> {
                Base base = BaseObject.INSTANCE.getBaseMap().get(baseId);
                if (base.getLevel() == 4) {
                    return;
                }
                SoundUtil.playSound(player, DiveSound.GuiClick,1.0);
                BaseUpgradeGUI.open(player, baseId);
            }
        }
    }
    public static void onTickUpdate(Player player){
        InventoryView view = player.getOpenInventory();
        if (view.getTitle().equals("拠点")){
            //更新
            Inventory inv = view.getTopInventory();
            ItemStack fuel = inv.getItem(23);
            ItemStack baseItem = inv.getItem(22);
            assert fuel != null;
            if (!fuel.getType().equals(Material.CHARCOAL)) {
                return;
            }
            if (baseItem == null || baseItem.getItemMeta() == null){
                return;
            }
            Base base = BaseObject.INSTANCE.getBaseMap().get(baseItem.getItemMeta().getCustomModelData());
            inv.setItem(23, BaseGUI.getFuelIcon(base.getRemain()));

            if (base.getRemain() == 0){
                ItemStack furnace = inv.getItem(10);
                if (furnace == null){
                    return;
                }
                if (furnace.getType().equals(Material.BLAST_FURNACE)){
                    player.closeInventory();
                }
            }
        }
    }
}
