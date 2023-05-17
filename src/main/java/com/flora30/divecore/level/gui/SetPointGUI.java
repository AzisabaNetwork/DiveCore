package com.flora30.divecore.level.gui;

import com.flora30.diveapin.data.player.LevelData;
import com.flora30.diveapin.data.player.PlayerDataObject;
import com.flora30.diveapin.util.GuiItem;
import com.flora30.diveapin.util.GuiItemType;
import com.flora30.divecore.data.PlayerDataMain;
import com.flora30.divecore.level.Point;
import com.flora30.divecore.level.PointData;
import com.flora30.divecore.level.type.PointType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
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
        PointData data = Point.lucApplyMap.get(point);
        if (point == 0) data = new PointData();

        ItemStack item = GuiItem.INSTANCE.getItem(GuiItemType.PointLuc);
        if (point == 0) item = GuiItem.INSTANCE.getItem(GuiItemType.PointZero);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        meta.setDisplayName(ChatColor.GOLD+"幸運 ‣ "+point);

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE+"");
        lore.add(ChatColor.WHITE+"豪華なチェストの確率 ‣ "+ChatColor.GOLD+"+"+(int) data.lucky+"％");
        lore.add(ChatColor.WHITE+"採集の遺物獲得率 ‣ "+ChatColor.GOLD+"+"+(int) data.gatherRelic+"％");
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
        int nextPoint = Point.getNextPoint(PointType.Luc, point);
        PointData nPointData = Point.lucMap.get(nextPoint);
        lore.add(ChatColor.WHITE+"");
        if (nPointData != null) {
            lore.add(ChatColor.WHITE+"追加ボーナス ‣ 幸運 "+nextPoint);
            if (nPointData.lucky != 0) {
                lore.add(ChatColor.WHITE+"豪華なチェストの確率 ‣ "+ChatColor.GOLD+"+"+(int) nPointData.lucky+"％");
            }
            if (nPointData.gatherRelic != 0) {
                lore.add(ChatColor.WHITE+"採集の遺物獲得率 ‣ "+ChatColor.GOLD+"+"+(int) nPointData.gatherRelic+"％");
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
        PointData data = Point.intApplyMap.get(point);
        if (point == 0) data = new PointData();

        ItemStack item = GuiItem.INSTANCE.getItem(GuiItemType.PointInt);
        if (point == 0) item = GuiItem.INSTANCE.getItem(GuiItemType.PointZero);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        meta.setDisplayName(ChatColor.GOLD+"知識 ‣ "+point);

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE+"");
        lore.add(ChatColor.WHITE+"経験値倍率 ‣ "+ChatColor.GOLD+"+"+data.exp+"％");
        lore.add(ChatColor.WHITE+"採掘・伐採の原生生物出現率 ‣ "+ChatColor.GOLD+data.gatherMonster+"％");
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
        int nextPoint = Point.getNextPoint(PointType.Int, point);
        PointData nPointData = Point.intMap.get(nextPoint);
        lore.add(ChatColor.WHITE+"");
        if (nPointData != null) {
            lore.add(ChatColor.WHITE+"追加ボーナス ‣ 知識 "+nextPoint);
            if (nPointData.exp != 0) {
                lore.add(ChatColor.WHITE+"経験値倍率 ‣ "+ChatColor.GOLD+"+"+(int) nPointData.exp+"％");
            }
            if (nPointData.gatherMonster != 0) {
                lore.add(ChatColor.WHITE+"採掘・伐採の原生生物出現率 ‣ "+ChatColor.GOLD+(int) nPointData.gatherMonster+"％");
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
        PointData data = Point.vitApplyMap.get(point);
        if (point == 0) data = new PointData();

        ItemStack item = GuiItem.INSTANCE.getItem(GuiItemType.PointVit);
        if (point == 0) item = GuiItem.INSTANCE.getItem(GuiItemType.PointZero);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        meta.setDisplayName(ChatColor.GOLD+"体力 ‣ "+point);

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE+"");
        lore.add(ChatColor.WHITE+"HP増加 ‣ "+ChatColor.GOLD+"+"+(int) data.health);
        lore.add(ChatColor.WHITE+"スタミナ増加 ‣ "+ChatColor.GOLD+"+"+(int) data.stamina / 20);
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
        int nextPoint = Point.getNextPoint(PointType.Vit, point);
        PointData nPointData = Point.vitMap.get(nextPoint);
        lore.add(ChatColor.WHITE+"");
        if (nPointData != null) {
            lore.add(ChatColor.WHITE+"追加ボーナス ‣ 体力 "+nextPoint);
            if (nPointData.health != 0) {
                lore.add(ChatColor.WHITE+"HP増加 ‣ "+ChatColor.GOLD+"+"+(int) nPointData.health);
            }
            if (nPointData.stamina != 0) {
                lore.add(ChatColor.WHITE+"スタミナ増加 ‣ "+ChatColor.GOLD+"+"+(int) nPointData.stamina / 20);
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
        PointData data = Point.atkApplyMap.get(point);
        if (point == 0) data = new PointData();

        ItemStack item = GuiItem.INSTANCE.getItem(GuiItemType.PointAtk);
        if (point == 0) item = GuiItem.INSTANCE.getItem(GuiItemType.PointZero);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        meta.setDisplayName(ChatColor.GOLD+"武力 ‣ "+point);

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE+"");
        lore.add(ChatColor.WHITE+"攻撃力増加（武器） ‣ "+ChatColor.GOLD+"+"+(int) data.weapon+"％");
        lore.add(ChatColor.WHITE+"攻撃力増加（遺物） ‣ "+ChatColor.GOLD+"+"+(int) data.artifact+"％");
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
        int nextPoint = Point.getNextPoint(PointType.Atk, point);
        PointData nPointData = Point.atkMap.get(nextPoint);
        lore.add(ChatColor.WHITE+"");
        if (nPointData != null) {
            lore.add(ChatColor.WHITE+"追加ボーナス ‣ 武力 "+nextPoint);
            if (nPointData.weapon != 0) {
                lore.add(ChatColor.WHITE+"攻撃力増加（武器） ‣ "+ChatColor.GOLD+"+"+(int) nPointData.weapon+"％");
            }
            if (nPointData.artifact != 0) {
                lore.add(ChatColor.WHITE+"攻撃力増加（遺物） ‣ "+ChatColor.GOLD+"+"+(int) nPointData.artifact+"％");
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
}
