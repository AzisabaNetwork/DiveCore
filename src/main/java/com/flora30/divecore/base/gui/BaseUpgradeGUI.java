package com.flora30.divecore.base.gui;

import com.flora30.diveapi.plugins.ItemAPI;
import com.flora30.diveapi.plugins.RegionAPI;
import com.flora30.diveapi.tools.GuiItem;
import com.flora30.divecore.base.Base;
import com.flora30.divecore.base.BaseData;
import com.flora30.divecore.base.BaseMain;
import com.flora30.divecore.base.BaseRequire;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class BaseUpgradeGUI {

    public static void open(Player player, int baseId) {
        Inventory gui = create(baseId);
        player.openInventory(gui);
    }

    private static Inventory create(int baseId) {
        Base base = BaseMain.baseMap.get(baseId);
        Inventory gui = Bukkit.createInventory(null,27,"拠点強化");
        for (int i = 0; i < 27; i++){
            gui.setItem(i, GuiItem.getItem(Material.GRAY_STAINED_GLASS_PANE));
        }

        gui.setItem(4,getNewBaseIcon(base.getLocation(),base.getLevel() + 1));

        BaseData data = BaseMain.baseDataMap.get(RegionAPI.getLayerName(base.getLocation()));
        BaseRequire require = data.getLevelMap().get(base.getLevel()+1);

        List<Integer> itemIdList;
        if (base.isTown()){
            itemIdList = new ArrayList<>();
        }
        else{
            itemIdList = new ArrayList<>(require.getRequireMap().keySet());
        }
        for (int i = 0; i < itemIdList.size() && i < 5; i++){
            int itemId = itemIdList.get(i);
            int amount = require.getRequireMap().get(itemId);

            gui.setItem(getSlot(i),getRequireItem(itemId,amount));
        }

        gui.setItem(22,getEnterIcon(base.isTown()));

        return gui;
    }


    private static ItemStack getNewBaseIcon(Location location, int nextLevel){

        //location
        int locId = BaseMain.baseUpgradeInstantList.size();
        BaseMain.baseUpgradeInstantList.add(location);

        //item
        ItemStack item = new ItemStack(Material.CAMPFIRE);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        meta.setDisplayName(ChatColor.GREEN+"拠点レベル ‣ "+ChatColor.WHITE+nextLevel);

        meta.setCustomModelData(locId);
        Bukkit.getLogger().info("setLocId - "+locId);

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GREEN+"使える機能 ‣");
        switch (nextLevel) {
            case 4 -> {
                lore.add(ChatColor.GREEN + "‣ " + ChatColor.WHITE + "エンダーチェスト (new)");
                lore.add(ChatColor.GREEN + "‣ " + ChatColor.WHITE + "ステータス強化");
                lore.add(ChatColor.GREEN + "‣ " + ChatColor.WHITE + "料理");
                lore.add(ChatColor.GREEN + "‣ " + ChatColor.WHITE + "しゃがんでHP回復");
                lore.add(ChatColor.GREEN + "‣ " + ChatColor.WHITE + "原生生物からの守護");
                lore.add(ChatColor.GREEN + "‣ " + ChatColor.WHITE + "残り時間延長 (+120秒)");
            }
            case 3 -> {
                lore.add(ChatColor.GREEN + "‣ " + ChatColor.WHITE + "ステータス強化 (new)");
                lore.add(ChatColor.GREEN + "‣ " + ChatColor.WHITE + "料理");
                lore.add(ChatColor.GREEN + "‣ " + ChatColor.WHITE + "しゃがんでHP回復");
                lore.add(ChatColor.GREEN + "‣ " + ChatColor.WHITE + "原生生物からの守護");
                lore.add(ChatColor.GREEN + "‣ " + ChatColor.WHITE + "残り時間延長 (+60秒)");
            }
            case 2 -> {
                lore.add(ChatColor.GREEN + "‣ " + ChatColor.WHITE + "料理 (new)");
                lore.add(ChatColor.GREEN + "‣ " + ChatColor.WHITE + "しゃがんでHP回復");
                lore.add(ChatColor.GREEN + "‣ " + ChatColor.WHITE + "原生生物からの守護");
                lore.add(ChatColor.GREEN + "‣ " + ChatColor.WHITE + "残り時間延長 (+60秒)");
            }
            case 1 -> {
                lore.add(ChatColor.GREEN + "‣ " + ChatColor.WHITE + "しゃがんでHP回復 (new)");
                lore.add(ChatColor.GREEN + "‣ " + ChatColor.WHITE + "原生生物からの守護 (new)");
                lore.add(ChatColor.GREEN + "‣ " + ChatColor.WHITE + "残り時間延長 (+30秒)");
            }
        }
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack getRequireItem(int id, int amount){
        ItemStack item = ItemAPI.getItem(id);
        item.setAmount(amount);
        return item;
    }

    private static ItemStack getEnterIcon(boolean isTown){
        ItemStack item = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        meta.setDisplayName(ChatColor.GOLD+"拠点レベルを上げる");

        List<String> lore = new ArrayList<>();
        lore.add("");
        if (isTown){
            lore.add(ChatColor.GREEN+"町の中の拠点は、素材を消費しません");
        }
        else{
            lore.add(ChatColor.GREEN+"素材を消費します");
        }

        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    private static int getSlot(int number){
        switch (number){
            case 0:
                return 11;
            case 1:
                return 15;
            case 2:
                return 13;
            case 3:
                return 12;
            case 4:
                return 14;
            default:
                return -1;
        }
    }
}
