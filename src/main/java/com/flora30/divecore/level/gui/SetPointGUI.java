package com.flora30.divecore.level.gui;

import com.flora30.diveapin.data.player.LevelData;
import com.flora30.diveapin.data.player.PlayerDataObject;
import com.flora30.diveapin.util.GuiItem;
import com.flora30.diveapin.util.GuiItemType;
import com.flora30.divenew.data.Point;
import com.flora30.divenew.data.PointObject;
import com.flora30.divenew.data.PointType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SetPointGUI {

    public static void open(Player player) {
        Inventory gui = create(player.getUniqueId());
        player.openInventory(gui);
    }

    public static Inventory create(UUID id) {
        LevelData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(id).getLevelData();
        Inventory gui = GuiItem.INSTANCE.grayBack(Bukkit.createInventory(null,45, "ステータス強化"));

        gui.setItem(4,getCurrentPoint(data.getRawPoint()));

        gui.setItem(19,getIconAtk(data.getPointAtk()));
        gui.setItem(21,getIconVit(data.getPointVit()));
        gui.setItem(23,getIconInt(data.getPointInt()));
        gui.setItem(25,getIconLuc(data.getPointLuc()));

        gui.setItem(28,getIconAtkPlus(data.getPointAtk()));
        gui.setItem(30,getIconVitPlus(data.getPointVit()));
        gui.setItem(32,getIconIntPlus(data.getPointInt()));
        gui.setItem(34,getIconLucPlus(data.getPointLuc()));

        gui.setItem(44, GuiItem.INSTANCE.getReturn());

        return gui;
    }

    public static ItemStack getIconLuc(int point){
        Point data = PointObject.INSTANCE.getLucApplyMap().get(point);
        if (point == 0) data = new Point(0,0,0,0,0,0,0,0);

        ItemStack item = GuiItem.INSTANCE.getItem(GuiItemType.PointLuc);
        if (point == 0) item = GuiItem.INSTANCE.getItem(GuiItemType.PointZero);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        meta.setDisplayName(ChatColor.GOLD+"幸運 ‣ "+point);

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE+"");
        lore.add(ChatColor.WHITE+"豪華なチェストの確率 ‣ "+ChatColor.GOLD+"+"+(int) data.getLucky()+"％");
        lore.add(ChatColor.WHITE+"採集の遺物獲得率 ‣ "+ChatColor.GOLD+"+"+(int) data.getGatherRelic()+"％");
        lore.add(ChatColor.WHITE+"");

        meta.setLore(lore);
        item.setAmount(applyLimit(point));

        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getIconLucPlus(int point) {

        ItemStack item = GuiItem.INSTANCE.getItem(GuiItemType.Plus);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        List<String> lore = new ArrayList<>();
        meta.setDisplayName(ChatColor.WHITE+"<< クリックでポイントを振る >>");

        // 次に追加されるボーナスの表示
        int nextPoint = getNextPoint(PointType.Luc, point);
        Point data = PointObject.INSTANCE.getLucMap().get(nextPoint);
        lore.add(ChatColor.WHITE+"");
        if (data != null) {
            lore.add(ChatColor.WHITE+"追加ボーナス ‣ 幸運 "+nextPoint);
            if (data.getLucky() != 0) {
                lore.add(ChatColor.WHITE+"豪華なチェストの確率 ‣ "+ChatColor.GOLD+"+"+data.getLucky()+"％");
            }
            if (data.getGatherRelic() != 0) {
                lore.add(ChatColor.WHITE+"採集の遺物獲得率 ‣ "+ChatColor.GOLD+"+"+data.getGatherRelic()+"％");
            }
        } else {
            lore.add(ChatColor.WHITE+"追加ボーナス ‣ 以降は未実装");
        }
        lore.add(ChatColor.WHITE+"");

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getIconInt(int point){
        Point data = PointObject.INSTANCE.getIntApplyMap().get(point);
        if (point == 0) data = new Point(0,0,0,0,0,0,0,0);

        ItemStack item = GuiItem.INSTANCE.getItem(GuiItemType.PointInt);
        if (point == 0) item = GuiItem.INSTANCE.getItem(GuiItemType.PointZero);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        meta.setDisplayName(ChatColor.GOLD+"知識 ‣ "+point);

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE+"");
        lore.add(ChatColor.WHITE+"経験値倍率 ‣ "+ChatColor.GOLD+"+"+data.getExp()+"％");
        lore.add(ChatColor.WHITE+"採掘・伐採の原生生物出現率 ‣ "+ChatColor.GOLD+data.getGatherMonster()+"％");
        lore.add(ChatColor.WHITE+"");

        meta.setLore(lore);
        item.setAmount(applyLimit(point));

        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getIconIntPlus(int point) {

        ItemStack item = GuiItem.INSTANCE.getItem(GuiItemType.Plus);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        List<String> lore = new ArrayList<>();
        meta.setDisplayName(ChatColor.WHITE+"<< クリックでポイントを振る >>");

        // 次に追加されるボーナスの表示
        int nextPoint = getNextPoint(PointType.Int, point);
        Point data = PointObject.INSTANCE.getIntMap().get(nextPoint);
        lore.add(ChatColor.WHITE+"");
        if (data != null) {
            lore.add(ChatColor.WHITE+"追加ボーナス ‣ 知識 "+nextPoint);
            if (data.getExp() != 0) {
                lore.add(ChatColor.WHITE+"経験値倍率 ‣ "+ChatColor.GOLD+"+"+data.getExp()+"％");
            }
            if (data.getGatherMonster() != 0) {
                lore.add(ChatColor.WHITE+"採掘・伐採の原生生物出現率 ‣ "+ChatColor.GOLD+data.getGatherMonster()+"％");
            }
        } else {
            lore.add(ChatColor.WHITE+"追加ボーナス ‣ 以降は未実装");
        }
        lore.add(ChatColor.WHITE+"");

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getIconVit(int point){
        Point data = PointObject.INSTANCE.getVitApplyMap().get(point);
        if (point == 0) data = new Point(0,0,0,0,0,0,0,0);

        ItemStack item = GuiItem.INSTANCE.getItem(GuiItemType.PointVit);
        if (point == 0) item = GuiItem.INSTANCE.getItem(GuiItemType.PointZero);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        meta.setDisplayName(ChatColor.GOLD+"体力 ‣ "+point);

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE+"");
        lore.add(ChatColor.WHITE+"HP増加 ‣ "+ChatColor.GOLD+"+"+data.getHealth());
        lore.add(ChatColor.WHITE+"スタミナ増加 ‣ "+ChatColor.GOLD+"+"+data.getStamina() / 20);
        lore.add(ChatColor.WHITE+"");


        meta.setLore(lore);
        item.setAmount(applyLimit(point));

        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getIconVitPlus(int point) {
        ItemStack item = GuiItem.INSTANCE.getItem(GuiItemType.Plus);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        List<String> lore = new ArrayList<>();
        meta.setDisplayName(ChatColor.WHITE+"<< クリックでポイントを振る >>");

        // 次に追加されるボーナスの表示
        int nextPoint = getNextPoint(PointType.Vit, point);
        Point data = PointObject.INSTANCE.getVitMap().get(nextPoint);
        lore.add(ChatColor.WHITE+"");
        if (data != null) {
            lore.add(ChatColor.WHITE+"追加ボーナス ‣ 体力 "+nextPoint);
            if (data.getHealth() != 0) {
                lore.add(ChatColor.WHITE+"HP増加 ‣ "+ChatColor.GOLD+"+"+data.getHealth());
            }
            if (data.getStamina() != 0) {
                lore.add(ChatColor.WHITE+"スタミナ増加 ‣ "+ChatColor.GOLD+"+"+data.getStamina() / 20);
            }
        } else {
            lore.add(ChatColor.WHITE+"追加ボーナス ‣ 以降は未実装");
        }
        lore.add(ChatColor.WHITE+"");

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getIconAtk(int point){
        Point data = PointObject.INSTANCE.getAtkApplyMap().get(point);
        if (point == 0) data = new Point(0,0,0,0,0,0,0,0);

        ItemStack item = GuiItem.INSTANCE.getItem(GuiItemType.PointAtk);
        if (point == 0) item = GuiItem.INSTANCE.getItem(GuiItemType.PointZero);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        meta.setDisplayName(ChatColor.GOLD+"武力 ‣ "+point);

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE+"");
        lore.add(ChatColor.WHITE+"攻撃力増加（武器） ‣ "+ChatColor.GOLD+"+"+data.getWeapon()+"％");
        lore.add(ChatColor.WHITE+"攻撃力増加（遺物） ‣ "+ChatColor.GOLD+"+"+data.getArtifact()+"％");
        lore.add(ChatColor.WHITE+"");


        meta.setLore(lore);
        item.setAmount(applyLimit(point));

        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getIconAtkPlus(int point) {
        ItemStack item = GuiItem.INSTANCE.getItem(GuiItemType.Plus);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        List<String> lore = new ArrayList<>();
        meta.setDisplayName(ChatColor.WHITE+"<< クリックでポイントを振る >>");

        // 次に追加されるボーナスの表示
        int nextPoint = getNextPoint(PointType.Atk, point);
        Point data = PointObject.INSTANCE.getAtkMap().get(nextPoint);
        lore.add(ChatColor.WHITE+"");
        if (data != null) {
            lore.add(ChatColor.WHITE+"追加ボーナス ‣ 武力 "+nextPoint);
            if (data.getWeapon() != 0) {
                lore.add(ChatColor.WHITE+"攻撃力増加（武器） ‣ "+ChatColor.GOLD+"+"+data.getWeapon()+"％");
            }
            if (data.getArtifact() != 0) {
                lore.add(ChatColor.WHITE+"攻撃力増加（遺物） ‣ "+ChatColor.GOLD+"+"+data.getArtifact()+"％");
            }
        } else {
            lore.add(ChatColor.WHITE+"追加ボーナス ‣ 以降は未実装");
        }
        lore.add(ChatColor.WHITE+"");

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getCurrentPoint(int point){
        ItemStack item = GuiItem.INSTANCE.getItem(GuiItemType.Point);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.GOLD+"所持ポイント ‣ "+point);
        item.setAmount(applyLimit(point));
        item.setItemMeta(meta);
        return item;
    }



    private static int applyLimit(int point){
        point = Math.min(point,64);
        point = Math.max(point,1);
        return point;
    }

    /**
     * 現在の「次」に得られるものがある場所
     */
    public static int getNextPoint(PointType type, int point) {
        Map<Integer,Point> map;
        switch (type){
            case Int -> map = PointObject.INSTANCE.getIntMap();
            case Vit -> map = PointObject.INSTANCE.getVitMap();
            case Atk -> map = PointObject.INSTANCE.getAtkMap();
            case Luc -> map = PointObject.INSTANCE.getLucMap();
            default -> {return -1;}
        }

        for (int mapPoint : map.keySet()) {
            if (mapPoint <= point) continue;
            return mapPoint;
        }

        return -1;
    }
}
