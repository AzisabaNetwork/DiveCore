package com.flora30.divecore.level.gui;

import com.flora30.diveapi.tools.GuiItem;
import com.flora30.diveapi.tools.GuiItemType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TradeExpGUI {

    public static void open(Player player) {
        Inventory gui = create();
        player.openInventory(gui);
    }

    private static Inventory create() {
        Inventory gui = GuiItem.grayBack(Bukkit.createInventory(null, 27, "アイテムを経験値に変換できます"));
        for (int i = 0; i < 18; i++){
            gui.setItem(i,null);
        }

        //決定ボタン
        ItemStack item = GuiItem.getItem(GuiItemType.QuestCompleted);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName("経験値に変換する");
        item.setItemMeta(meta);
        gui.setItem(17,item);

        gui.setItem(26,GuiItem.getItem(GuiItemType.Return));

        return gui;
    }
}
