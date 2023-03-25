package com.flora30.divecore.menu;

import com.flora30.diveapi.plugins.CoreAPI;
import com.flora30.diveapi.plugins.QuestAPI;
import com.flora30.diveapi.tools.GuiItem;
import com.flora30.diveapi.tools.GuiItemType;
import com.flora30.divecore.tools.SoundUtil;
import com.flora30.divecore.tools.type.DiveSound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class DeathGUI {
    public static Inventory getGui(){
        Inventory inv = Bukkit.createInventory(null,27,"死亡する？");
        GuiItem.grayBack(inv);
        inv.setItem(4,getDeathIcon());

        inv.setItem(11,getTrueIcon());

        inv.setItem(15,getFalseIcon());

        return inv;
    }

    private static ItemStack getTrueIcon() {
        ItemStack item = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);

        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        meta.setDisplayName(ChatColor.GREEN + "死亡する");
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack getFalseIcon() {
        ItemStack item = new ItemStack(Material.RED_STAINED_GLASS_PANE);

        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        meta.setDisplayName(ChatColor.RED + "死亡しない");
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getDeathIcon() {
        ItemStack item = new ItemStack(Material.SKELETON_SKULL);

        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        meta.setDisplayName(ChatColor.RED + "死亡する？");
        List<String> lore = new ArrayList<>(2);
        lore.add("");
        lore.add(ChatColor.WHITE + "全ての遺物が失われます");
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    // アイテムを持つ状況が発生しないのでDrag停止は必要なし
    public static void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getClickedInventory() == null){
            return;
        }
        if (event.getClickedInventory() == event.getView().getBottomInventory()){
            return;
        }

        Player player = (Player) event.getWhoClicked();
        switch (event.getSlot()) {
            case 11 -> player.damage(player.getHealth());
            case 15 -> {
                SoundUtil.playSound(player, DiveSound.GuiClick, 1.0);
                MenuMain.openMenu(player);
            }
        }
    }
}
