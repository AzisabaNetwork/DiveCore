package com.flora30.divecore.menu;

import com.flora30.diveapi.data.PlayerData;
import com.flora30.diveapi.data.Story;
import com.flora30.diveapi.data.Whistle;
import com.flora30.diveapi.event.HelpEvent;
import com.flora30.diveapi.plugins.CoreAPI;
import com.flora30.diveapi.plugins.ItemAPI;
import com.flora30.diveapi.plugins.QuestAPI;
import com.flora30.diveapi.plugins.RegionAPI;
import com.flora30.diveapi.tools.GuiItem;
import com.flora30.diveapi.tools.GuiItemType;
import com.flora30.diveapi.tools.HelpType;
import com.flora30.divecore.data.PlayerDataMain;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MenuGUI {
    public static Inventory getGui(Player player){
        Inventory inv = Bukkit.createInventory(null,45,"メニュー");
        GuiItem.grayBack(inv);
        String name = QuestAPI.getStory(CoreAPI.getPlayerData(player.getUniqueId()).layerData.layer).displayName;
        inv.setItem(4,getTitled(new ItemStack(Material.GRASS_BLOCK),ChatColor.GOLD+"エリア ‣ " +ChatColor.WHITE+ name));

        inv.setItem(10,getTitled(GuiItem.getItem(GuiItemType.Point),ChatColor.GOLD+"現在のステータスを確認する"));

        inv.setItem(12,getTitled(new ItemStack(Material.CRAFTING_TABLE),ChatColor.GOLD+"クラフトをする"));

        inv.setItem(14,getTravelIcon(player));

        inv.setItem(16,getReturn(player));

        inv.setItem(28,getTitled(GuiItem.getItem(GuiItemType.Help),ChatColor.GOLD+"ヘルプを見る"));

        inv.setItem(34,DeathGUI.getDeathIcon());

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
        ItemStack icon = com.flora30.diveapi.tools.GuiItem.getReturn();
        ItemMeta meta = icon.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.GOLD + "オースに帰還する");

        // オースの場合は不要
        PlayerData data = PlayerDataMain.getPlayerData(player.getUniqueId());
        if (data.layerData.layer.equals("oldOrth")){
            icon.setType(Material.BARRIER);
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.WHITE + "探窟中にのみ使用可能です");
            meta.setLore(lore);
            icon.setItemMeta(meta);
            return icon;
        }

        // 条件を確認
        Whistle whistle = ItemAPI.getWhistle(data.levelData.whistleRank);
        int returnDepth = whistle.returnDepth;

        //　現在の深さを確認
        double fallPlus = 200 - player.getLocation().getY();
        double fallLayer = RegionAPI.getLayer(data.layerData.layer).fall;
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
        PlayerData data = PlayerDataMain.getPlayerData(player.getUniqueId());
        if (data == null || !RegionAPI.canFastTravel(data.layerData.layer)) {
            icon.setType(Material.BARRIER);
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.WHITE + "現在の場所ではファストトラベルができません");
            meta.setLore(lore);
        }

        icon.setItemMeta(meta);
        return icon;
    }
}
