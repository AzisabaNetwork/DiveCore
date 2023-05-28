package com.flora30.divecore.menu;

import com.flora30.divelib.util.PlayerItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class MenuTrigger {
    public static void onInteract(PlayerInteractEvent event){
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK){
            return;
        }
        ItemStack item = event.getItem();
        if (MenuMain.isMenuIcon(item)){
            MenuMain.openMenu(event.getPlayer());
        }
    }

    public static void onAnimation(PlayerAnimationEvent event){
        Player player = event.getPlayer();

        PlayerInventory inventory = player.getInventory();
        if (player.getOpenInventory().getType() != InventoryType.PLAYER) return;

        if (MenuMain.isMenuIcon(inventory.getItemInMainHand())){
            MenuMain.openMenu(player);
        }
    }

    public static void onTick(Player player){
        PlayerInventory inventory = player.getInventory();
        for (int i = 0; i < 40; i++){

            ItemStack item = inventory.getItem(i);

            if (i == 8){
                if (item != null && item.getType() == Material.COMPASS) continue;

                if (item == null){
                    inventory.setItem(i,MenuMain.menuIcon);
                }
                else {
                    // 他のアイテムがある場合は、コンパスを置いた後であげ直す
                    ItemStack other = item.clone();
                    inventory.setItem(i,MenuMain.menuIcon);
                    PlayerItem.INSTANCE.giveItem(player, other);
                }
                continue;
            }

            if (item == null || item.getItemMeta() == null || !item.getItemMeta().hasCustomModelData()){
                continue;
            }

            if (item.getItemMeta().getCustomModelData() == 10000){
                inventory.setItem(i,null);
            }
        }

        ItemStack cursorItem = player.getItemOnCursor();
        if (cursorItem.getType() != Material.COMPASS || !cursorItem.hasItemMeta()){
            return;
        }
        assert cursorItem.getItemMeta() != null;
        if (cursorItem.getItemMeta().getCustomModelData() == 10000){
            player.setItemOnCursor(null);
        }

    }

    public static void onSwap(PlayerSwapHandItemsEvent event){
        if (MenuMain.isMenuIcon(event.getOffHandItem())){
            event.setCancelled(true);
        }
    }

    public static void onClick(InventoryClickEvent event){
        if (event.getClickedInventory() == null){
            return;
        }
        if (MenuMain.isMenuIcon(event.getClickedInventory().getItem(event.getSlot()))){
            //Bukkit.getLogger().info("menu click");
            event.setCancelled(true);
        }
        if (event.getClick() == ClickType.NUMBER_KEY && event.getHotbarButton() == 8) {
            //Bukkit.getLogger().info("menu click");
            event.setCancelled(true);
        }
    }

    public static void onDrag(InventoryDragEvent event){
        if (MenuMain.isMenuIcon(event.getOldCursor())){
            event.setCancelled(true);
        }
    }

    public static void onItemMove(InventoryMoveItemEvent event){
        if (MenuMain.isMenuIcon(event.getItem())){
            event.setCancelled(true);
        }
    }

    public static void onDrop(PlayerDropItemEvent event){
        if (MenuMain.isMenuIcon(event.getItemDrop().getItemStack())){
            event.setCancelled(true);
        }
    }

    public static void onDeath(PlayerDeathEvent event){
        event.getDrops().removeIf(MenuMain::isMenuIcon);

        //全部保存
        event.setKeepInventory(true);
        event.getDrops().clear();
    }

    public static void onRespawn(PlayerRespawnEvent event){
        event.getPlayer().getInventory().setItem(8,MenuMain.menuIcon);
    }
}
