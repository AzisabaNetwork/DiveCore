package com.flora30.divecore.base.gui;

import com.flora30.diveapi.tools.GuiItem;
import com.flora30.divecore.base.Base;
import com.flora30.divecore.base.BaseMain;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class BaseGUI {

    public static void open(Player player, int baseId) {
        Inventory gui = create(baseId);
        player.openInventory(gui);
    }

    private static Inventory create(int baseId) {
        Base base = BaseMain.baseMap.get(baseId);
        Inventory gui = Bukkit.createInventory(null,27,"拠点");
        for (int i = 0; i < 27; i++){
            gui.setItem(i, GuiItem.getItem(Material.GRAY_STAINED_GLASS_PANE));
        }

        gui.setItem(10, getGuardIcon(base.getLevel()));
        gui.setItem(3,getCookIcon(base.getLevel()));
        gui.setItem(5,getStatusIcon(base.getLevel()));
        gui.setItem(16,getEnderIcon(base.getLevel()));

        gui.setItem(22,getBaseIcon(base.getLevel(), baseId));
        gui.setItem(23,getFuelIcon(base.getRemain()));

        return gui;
    }

    private static ItemStack getBaseIcon(int level, int id){
        ItemStack item = new ItemStack(Material.CAMPFIRE);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        meta.setDisplayName(ChatColor.GREEN+"拠点レベル ‣ "+ChatColor.WHITE+level);

        meta.setCustomModelData(id);

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GREEN+"使える機能 ‣");
        switch(level){
            case 4:
                lore.add(ChatColor.GREEN+"‣ "+ChatColor.WHITE+"エンダーチェスト");
            case 3:
                lore.add(ChatColor.GREEN+"‣ "+ChatColor.WHITE+"ステータス強化");
            case 2:
                lore.add(ChatColor.GREEN+"‣ "+ChatColor.WHITE+"料理");
            case 1:
                lore.add(ChatColor.GREEN+"‣ "+ChatColor.WHITE+"しゃがんでHP回復");
                lore.add(ChatColor.GREEN+"‣ "+ChatColor.WHITE+"原生生物からの守護");
                break;
            default:
                lore.add(ChatColor.GREEN+"‣ "+ChatColor.WHITE+"なし");
        }
        lore.add("");
        lore.add(ChatColor.GREEN+"クリック ‣ "+ChatColor.WHITE+"拠点レベル強化");
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getFuelIcon(int remain){
        ItemStack item = new ItemStack(Material.CHARCOAL);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        meta.setDisplayName(ChatColor.GREEN+"残り時間 ‣ "+ChatColor.WHITE+remain/20+"秒");

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.WHITE+"残り時間が無くなると、拠点レベルが0になります");
        lore.add("");
        lore.add(ChatColor.WHITE+"残り時間は、拠点レベル強化により延長できます");
        meta.setLore(lore);

        item.setItemMeta(meta);
        int amount = Math.min(remain / 20, 64);
        if (amount == 0){
            amount = 1;
        }
        item.setAmount(amount);

        return item;
    }


    private static ItemStack getGuardIcon(int level){
        if (level < 1){
            return getErrorIcon("焚き火の領域",1,"Shiftキーを押している間、HPが回復する","原生生物が近づけなくなる");
        }
        List<String> textList = new ArrayList<>();
        textList.add("Shiftキーを押している間、HPが回復する");
        textList.add("原生生物が近づけなくなる");
        return getNoAbleIcon(Material.BEACON,"焚き火の領域",1,textList);
    }

    private static ItemStack getCookIcon(int level){
        if (level < 2){
            return getErrorIcon("料理",2,"食材を料理できる");
        }
        List<String> textList = new ArrayList<>();
        textList.add("食材を持って、焚き火に右クリック");
        return getNoAbleIcon(Material.FURNACE,"料理",2,textList);
    }

    private static ItemStack getStatusIcon(int level){
        if (level < 3){
            return getErrorIcon("ステータス強化",3,"ポイントをステータスに振ることができる");
        }
        return getAbleIcon(Material.FIRE_CHARGE,"ステータス強化",3,"ポイントをステータスに振ることができる");
    }

    private static ItemStack getEnderIcon(int level){
        if (level < 4){
            return getErrorIcon("エンダーチェスト",4,"エンダーチェストを開けることができる");
        }
        return getAbleIcon(Material.ENDER_CHEST,"エンダーチェスト",4,"エンダーチェストを開けることができる");
    }


    private static ItemStack getAbleIcon(Material material, String title, int level, String text){

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        meta.setDisplayName(ChatColor.GREEN+title);

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GREEN+"必要拠点レベル ‣ "+ChatColor.WHITE+level);
        lore.add("");
        lore.add(ChatColor.WHITE+text);
        lore.add("");
        lore.add(ChatColor.WHITE+"クリックで開く");
        meta.setLore(lore);

        item.setItemMeta(meta);

        return item;
    }

    private static ItemStack getNoAbleIcon(Material material, String title, int level, List<String> textList){

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        meta.setDisplayName(ChatColor.GREEN+title);

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GREEN+"必要拠点レベル ‣ "+ChatColor.WHITE+level);
        for (String text : textList) {
            lore.add("");
            lore.add(ChatColor.WHITE+text);
        }
        meta.setLore(lore);

        item.setItemMeta(meta);

        return item;
    }

    private static ItemStack getErrorIcon(String title, int level, String text){
        return getErrorIcon(title, level, text, null);
    }

    private static ItemStack getErrorIcon(String title, int level, String text, String text2){
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        meta.setDisplayName(ChatColor.RED+title+ "（未開放）");

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GREEN+"必要拠点レベル ‣ "+ChatColor.WHITE+level);
        lore.add("");
        lore.add(ChatColor.WHITE+text);
        if (text2 != null) {
            lore.add("");
            lore.add(ChatColor.WHITE+text2);
        }
        meta.setLore(lore);

        item.setItemMeta(meta);

        return item;
    }
}
