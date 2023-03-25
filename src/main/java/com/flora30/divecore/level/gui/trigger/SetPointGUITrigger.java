package com.flora30.divecore.level.gui.trigger;

import com.flora30.diveapi.data.player.LevelData;
import com.flora30.divecore.DiveCore;
import com.flora30.divecore.data.PlayerDataMain;
import com.flora30.divecore.level.LevelMain;
import com.flora30.divecore.level.Point;
import com.flora30.divecore.level.PointData;
import com.flora30.divecore.level.gui.SetPointGUI;
import com.flora30.divecore.level.type.PointType;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SetPointGUITrigger {

    public static void onClick(InventoryClickEvent e){
        if(e.getClickedInventory() == null){
            return;
        }
        e.setCancelled(true);

        Player player = (Player) e.getWhoClicked();

        switch (e.getSlot()) {
            case 28 -> addPoint(player, e.getClickedInventory(), PointType.Atk);
            case 30 -> addPoint(player, e.getClickedInventory(), PointType.Vit);
            case 32 -> addPoint(player, e.getClickedInventory(), PointType.Int);
            case 34 -> addPoint(player, e.getClickedInventory(), PointType.Luc);
            case 44 -> {
                player.playSound(player.getLocation(),Sound.UI_BUTTON_CLICK,1,1);
                player.closeInventory();
            }

        }
    }

    private static void addPoint(Player player, Inventory gui, PointType type){
        LevelData data = PlayerDataMain.getPlayerData(player.getUniqueId()).levelData;
        Material glass;


        // 条件確認
        if (data.rawPoint == 0){
            player.sendMessage("ポイントが不足しています");
            return;
        }
        switch (type) {
            case Luc -> {
                if ( Point.lucApplyMap.get(data.pointLuc + 1) == null) return;
            }
            case Int -> {
                if ( Point.intApplyMap.get(data.pointInt + 1) == null) return;
            }
            case Vit -> {
                if ( Point.vitApplyMap.get(data.pointVit + 1) == null) return;
            }
            case Atk -> {
                if ( Point.atkApplyMap.get(data.pointAtk + 1) == null) return;
            }
        }

        // 使用可能なポイントを減らす
        data.rawPoint--;
        gui.setItem(4,SetPointGUI.getCurrentPoint(data.rawPoint));
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT,1,1);

        // ポイント適用
        switch (type) {
            case Luc -> {
                data.addPointLuc();
                gui.setItem(25,SetPointGUI.getIconLuc(data.pointLuc));
                gui.setItem(34,SetPointGUI.getIconLucPlus(data.pointLuc));
                glass = Material.GREEN_STAINED_GLASS_PANE;
            }
            case Int -> {
                data.addPointInt();
                gui.setItem(23,SetPointGUI.getIconInt(data.pointInt));
                gui.setItem(32,SetPointGUI.getIconIntPlus(data.pointInt));
                glass = Material.BLUE_STAINED_GLASS_PANE;
            }
            case Vit -> {
                data.addPointVit();
                LevelMain.setMaxHpSt(player);
                gui.setItem(21,SetPointGUI.getIconVit(data.pointVit));
                gui.setItem(30,SetPointGUI.getIconVitPlus(data.pointVit));
                glass = Material.ORANGE_STAINED_GLASS_PANE;
            }
            case Atk -> {
                data.addPointAtk();
                gui.setItem(19,SetPointGUI.getIconAtk(data.pointAtk));
                gui.setItem(28,SetPointGUI.getIconAtkPlus(data.pointAtk));
                glass = Material.RED_STAINED_GLASS_PANE;
            }
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }


        // 背景演出用のメモをアイテム名に入れる
        ItemStack memoItem = gui.getItem(0);
        if (memoItem == null || memoItem.getItemMeta() == null) return;
        ItemMeta meta = memoItem.getItemMeta();
        meta.setDisplayName("0");
        memoItem.setItemMeta(meta);

        //演出
        for (int i = 0; i < gui.getSize(); i++) {
            // 背景以外は除外
            switch (i){
                case 4,19,21,23,25,28,30,32,34,44:
                    continue;
            }

            ItemStack item = gui.getItem(i);
            if (item == null) continue;
            item.setType(glass);
        }
        DiveCore.plugin.delayedTask(2,() -> onAnim(player, data.rawPoint));
    }

    /**
     * 演出（選んだポイントの色に変わった背景が徐々に戻っていく）
     */
    public static void onAnim(Player player, int rawPoint) {
        // プレイヤーがポイント強化画面を開いている？
        if (player.getOpenInventory().getType() == InventoryType.PLAYER) return;
        if (!player.getOpenInventory().getTitle().equals("ステータス強化")) return;

        // 演出中にポイントを振っていない？
        if (PlayerDataMain.getPlayerData(player.getUniqueId()).levelData.rawPoint != rawPoint) return;

        // 演出の進行度を取得する
        int count;
        Inventory gui = player.getOpenInventory().getTopInventory();
        ItemStack countItem = gui.getItem(0);
        if (countItem == null || countItem.getItemMeta() == null) return;
        ItemMeta countMeta = countItem.getItemMeta();
        try{
            count = Integer.parseInt(countMeta.getDisplayName());
        } catch (NumberFormatException e) {
            return;
        }

        // 最後（5）まで進行している場合は終了
        if (count >= 5) {
            countMeta.setDisplayName(" ");
            countItem.setItemMeta(countMeta);
            return;
        }

        // 演出を行う
        for (int i = 0; i < 9; i++) {
            // 背景以外は除外
            switch (count * 9 + i){
                case 4,19,21,23,25,28,30,32,34,44:
                    continue;
            }

            ItemStack item = gui.getItem(count * 9 + i);
            if (item == null) continue;
            item.setType(Material.GRAY_STAINED_GLASS_PANE);
        }

        // 進行度を1進める
        countMeta.setDisplayName( count+1 +"");
        countItem.setItemMeta(countMeta);

        // 次の演出
        DiveCore.plugin.delayedTask(2,() -> onAnim(player, PlayerDataMain.getPlayerData(player.getUniqueId()).levelData.rawPoint));
    }
}
