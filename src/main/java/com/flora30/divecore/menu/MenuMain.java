package com.flora30.divecore.menu;

import com.flora30.divelib.event.HelpEvent;
import com.flora30.divelib.event.HelpType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class MenuMain {
    public static ItemStack menuIcon;

    public static void createIcon(){
        menuIcon = new ItemStack(Material.COMPASS);
        ItemMeta meta = menuIcon.getItemMeta();
        assert meta != null;

        meta.setDisplayName(ChatColor.WHITE+"メニュー ‣ 右クリック");
        meta.setCustomModelData(10000);
        menuIcon.setItemMeta(meta);
    }

    /**
     * コンパス検知
     */
    public static boolean isMenuIcon(ItemStack item){
        if (item == null || item.getItemMeta() == null){
            return false;
        }

        return item.getType() == Material.COMPASS;
    }

    public static void openMenu(Player player){
        Bukkit.getPluginManager().callEvent(new HelpEvent(player, HelpType.MenuGUI));
        player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_CLUSTER_STEP,1,1);
        player.openInventory(MenuGUI.getGui(player));
    }

    public static ItemStack getMenuIcon(Player player){
        return player.getInventory().getItem(8);
    }

}
