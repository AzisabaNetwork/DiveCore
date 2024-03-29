package com.flora30.divecore.menu;

import com.flora30.divelib.data.teleport.TravelObject;
import com.flora30.divelib.data.MenuSlot;
import com.flora30.divelib.data.player.PlayerData;
import com.flora30.divelib.data.player.PlayerDataObject;
import com.flora30.divelib.event.HelpEvent;
import com.flora30.divelib.event.HelpType;
import com.flora30.divelib.event.MenuOpenEvent;
import com.flora30.divelib.util.GuiItem;
import com.flora30.divelib.util.GuiItemType;
import com.flora30.divecore.data.PlayerDataMain;
import com.flora30.divelib.data.LayerObject;
import com.flora30.divelib.data.Whistle;
import com.flora30.divelib.data.WhistleObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class MenuGUI {


    public static Inventory getGui(Player player){
        Inventory inv = Bukkit.createInventory(null,45,"メニュー");
        GuiItem.INSTANCE.grayBack(inv);
        String name = LayerObject.INSTANCE.getLayerMap().get(PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId()).getLayerData().getLayer()).getDisplayName();
        inv.setItem(4,getTitled(new ItemStack(Material.GRASS_BLOCK),ChatColor.GOLD+"エリア ‣ " +ChatColor.WHITE+ name));

        inv.setItem(16,getReturn(player));

        MenuOpenEvent event = new MenuOpenEvent(player,new HashMap<>());
        for (Map.Entry<MenuSlot, ItemStack> entry : event.getIconMap().entrySet()) {
            inv.setItem(getInventorySlot(entry.getKey()), entry.getValue());
        }
        return inv;
    }

    private static ItemStack getTitled(ItemStack item, String title){
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(title);

        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack getReturn(Player player) {
        ItemStack icon = GuiItem.INSTANCE.getReturn();
        ItemMeta meta = icon.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.GOLD + "オースに帰還する");

        // オースの場合は不要
        PlayerData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId());
        if (data.getLayerData().getLayer().equals("oldOrth")){
            icon.setType(Material.BARRIER);
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.WHITE + "探窟中にのみ使用可能です");
            meta.setLore(lore);
            icon.setItemMeta(meta);
            return icon;
        }

        // 条件を確認
        Whistle whistle = WhistleObject.INSTANCE.getWhistleMap().get(data.getLevelData().getWhistleRank());
        int returnDepth = whistle.getReturnDepth();

        //　現在の深さを確認
        double fallPlus = 200 - player.getLocation().getY();
        double fallLayer = LayerObject.INSTANCE.getLayerMap().get(data.getLayerData().getLayer()).getFall();
        int fall = (int) (fallPlus+fallLayer);

        // Loreを設定
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GOLD + "帰還可能な深度 ‣ " + ChatColor.WHITE + returnDepth + "m");
        lore.add(ChatColor.GOLD + "現在の深度 ‣ " + ChatColor.WHITE + fall + "m");
        lore.add("");
        if (fall <= returnDepth) {
            Bukkit.getPluginManager().callEvent(new HelpEvent(player, HelpType.ReturnAble));
            lore.add(ChatColor.GREEN + "帰還可能です" + ChatColor.YELLOW + "（手持ちの遺物が、50%の確率で失われます）");
            lore.add(ChatColor.GRAY + "<<" + ChatColor.WHITE +  " クリックで帰還 " + ChatColor.GRAY + ">>");
        }
        else {
            icon.setType(Material.BARRIER);
            lore.add(ChatColor.RED + "帰還できません");
        }
        meta.setLore(lore);
        icon.setItemMeta(meta);
        return icon;
    }

    private static ItemStack getTravelIcon(Player player) {
        ItemStack icon = new ItemStack(Material.IRON_BOOTS);
        ItemMeta meta = icon.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.GOLD + "ファストトラベル");
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        // ファストトラベルが使えない場合
        PlayerData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId());
        if (data == null || !TravelObject.INSTANCE.getTravelMap().containsKey(data.getLayerData().getLayer())) {
            icon.setType(Material.BARRIER);
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.WHITE + "現在の場所ではファストトラベルができません");
            meta.setLore(lore);
        }

        icon.setItemMeta(meta);
        return icon;
    }

    public static int getInventorySlot(MenuSlot slot) {
        return switch (slot) {
            case Slot1 -> 10;
            case Slot2 -> 12;
            case Slot3 -> 14;
            case Slot4 -> 16;
            case Slot5 -> 28;
            case Slot6 -> 30;
            case Slot7 -> 32;
            case Slot8 -> 34;
        };
    }
}
