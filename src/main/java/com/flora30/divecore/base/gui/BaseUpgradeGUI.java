package com.flora30.divecore.base.gui;

import com.flora30.divelib.ItemMain;
import com.flora30.divelib.data.Base;
import com.flora30.divelib.data.BaseObject;
import com.flora30.divelib.util.GuiItem;
import com.flora30.divecore.base.BaseMain;
import com.flora30.diveconstant.data.*;
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
        Base base = BaseObject.INSTANCE.getBaseMap().get(baseId);
        BaseLocation baseLocation = BaseDataObject.INSTANCE.getBaseLocationMap().get(baseId);

        // 次のレベルの必要素材を取得
        BaseLayer baseLayer = BaseDataObject.INSTANCE.getBaseLayerMap().get(LayerObject.INSTANCE.getLayerName(baseLocation.getLocation()));
        BaseRequire baseRequire = baseLayer.getLevelMap().get(base.getLevel()+1);

        Inventory gui = Bukkit.createInventory(null,27,"拠点強化");
        for (int i = 0; i < 27; i++){
            gui.setItem(i, GuiItem.INSTANCE.getItem(Material.GRAY_STAINED_GLASS_PANE));
        }

        gui.setItem(4,getNewBaseIcon(baseLocation.getLocation(),base.getLevel() + 1));

        if (baseLocation.isTown()){
            gui.setItem(11,null);
            gui.setItem(15,null);
        }
        else{
            gui.setItem(11,getRequireItem(baseRequire.getRequire1Id(),baseRequire.getRequire1Amount()));
            gui.setItem(15,getRequireItem(baseRequire.getRequire2Id(),baseRequire.getRequire2Amount()));
        }

        gui.setItem(22,getEnterIcon(baseLocation.isTown()));

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
        ItemStack item = ItemMain.INSTANCE.getItem(id);
        if (item == null) return null;
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
}
