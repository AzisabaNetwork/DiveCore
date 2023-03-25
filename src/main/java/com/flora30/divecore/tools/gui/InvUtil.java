package com.flora30.divecore.tools.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Inventoryに関係する便利なもの
 */
public class InvUtil {

    /**
     * @param col 縦 (0 ~ ???)
     * @param row 横 (0 ~ 8)
     */
    public static void setItem(Inventory inv, ItemStack item, int col, int row) {
        inv.setItem(col * 9 + row, item);
    }
}
