package com.flora30.divecore.base.gui;

import com.flora30.divelib.data.player.PlayerData;
import com.flora30.divelib.data.player.PlayerDataObject;
import com.flora30.divelib.util.GuiItem;
import com.flora30.divelib.data.WhistleObject;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EnderChestGUI {
    public static Inventory getGui(Player player) {
        // エンダーチェストを取得
        Inventory ender = player.getEnderChest();

        // 容量を取得
        PlayerData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId());
        int capacity = WhistleObject.INSTANCE.getWhistleMap().get(data.getLevelData().getWhistleRank()).getEnderCapacity();

        Inventory gui = Bukkit.createInventory(null,27, "エンダーチェスト");
        GuiItem.INSTANCE.grayBack(gui);

        for (int i = 0; i < capacity && i < 27; i++) {
            gui.setItem(i, ender.getItem(i));
        }

        return gui;
    }

    /**
     * 灰色の領域はクリック無効化
     */
    public static void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;
        if (e.getClickedInventory().getType() == InventoryType.PLAYER) return;
        ItemStack item = e.getClickedInventory().getItem(e.getSlot());
        if (item != null && item.getType() == Material.GRAY_STAINED_GLASS_PANE) {
            e.setCancelled(true);
        }
    }

    /**
     * 中身をプレイヤーに保存
     */
    public static void onClose(InventoryCloseEvent e) {
        Inventory ender = e.getPlayer().getEnderChest();
        int size = e.getInventory().getSize();
        for (int i = 0; i < size; i++) {
            ItemStack from = e.getInventory().getItem(i);
            if (from != null && from.getType() == Material.GRAY_STAINED_GLASS_PANE) {
                return;
            }

            ender.setItem(i, from);
        }
    }
}
