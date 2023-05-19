package com.flora30.divecore.level.gui.trigger;

import com.flora30.diveapin.data.player.LevelData;
import com.flora30.diveapin.data.player.PlayerDataObject;
import com.flora30.divecore.DiveCore;
import com.flora30.divecore.level.LevelMain;
import com.flora30.divecore.level.gui.SetPointGUI;
import com.flora30.divenew.data.PointObject;
import com.flora30.divenew.data.PointType;
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
        LevelData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId()).getLevelData();
        Material glass;


        // 条件確認
        if (data.getRawPoint() == 0){
            player.sendMessage("ポイントが不足しています");
            return;
        }
        switch (type) {
            case Luc -> {
                if ( PointObject.INSTANCE.getLucApplyMap().get(data.getPointLuc() + 1) == null) return;
            }
            case Int -> {
                if ( PointObject.INSTANCE.getIntApplyMap().get(data.getPointInt() + 1) == null) return;
            }
            case Vit -> {
                if ( PointObject.INSTANCE.getVitApplyMap().get(data.getPointVit() + 1) == null) return;
            }
            case Atk -> {
                if ( PointObject.INSTANCE.getAtkApplyMap().get(data.getPointAtk() + 1) == null) return;
            }
        }

        // 使用可能なポイントを減らす
        data.setRawPoint(data.getRawPoint()-1);
        gui.setItem(4,SetPointGUI.getCurrentPoint(data.getRawPoint()));
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT,1,1);

        // ポイント適用
        switch (type) {
            case Luc -> {
                data.setPointLuc(data.getPointLuc()+1);
                gui.setItem(25,SetPointGUI.getIconLuc(data.getPointLuc()));
                gui.setItem(34,SetPointGUI.getIconLucPlus(data.getPointLuc()));
                glass = Material.GREEN_STAINED_GLASS_PANE;
            }
            case Int -> {
                data.setPointInt(data.getPointInt()+1);
                gui.setItem(23,SetPointGUI.getIconInt(data.getPointInt()));
                gui.setItem(32,SetPointGUI.getIconIntPlus(data.getPointInt()));
                glass = Material.BLUE_STAINED_GLASS_PANE;
            }
            case Vit -> {
                data.setPointVit(data.getPointVit()+1);
                LevelMain.setMaxHpSt(player);
                gui.setItem(21,SetPointGUI.getIconVit(data.getPointVit()));
                gui.setItem(30,SetPointGUI.getIconVitPlus(data.getPointVit()));
                glass = Material.ORANGE_STAINED_GLASS_PANE;
            }
            case Atk -> {
                data.setPointAtk(data.getPointAtk()+1);
                gui.setItem(19,SetPointGUI.getIconAtk(data.getPointAtk()));
                gui.setItem(28,SetPointGUI.getIconAtkPlus(data.getPointAtk()));
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
            switch (i) {
                case 4, 19, 21, 23, 25, 28, 30, 32, 34, 44 -> {
                    continue;
                }
            }

            ItemStack item = gui.getItem(i);
            if (item == null) continue;
            item.setType(glass);
        }
        DiveCore.plugin.delayedTask(2,() -> onAnim(player, data.getRawPoint()));
    }

    /**
     * 演出（選んだポイントの色に変わった背景が徐々に戻っていく）
     */
    public static void onAnim(Player player, int rawPoint) {
        // プレイヤーがポイント強化画面を開いている？
        if (player.getOpenInventory().getType() == InventoryType.PLAYER) return;
        if (!player.getOpenInventory().getTitle().equals("ステータス強化")) return;

        // 演出中にポイントを振っていない？
        if (PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId()).getLevelData().getRawPoint() != rawPoint) return;

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
            switch (count * 9 + i) {
                case 4, 19, 21, 23, 25, 28, 30, 32, 34, 44 -> {
                    continue;
                }
            }

            ItemStack item = gui.getItem(count * 9 + i);
            if (item == null) continue;
            item.setType(Material.GRAY_STAINED_GLASS_PANE);
        }

        // 進行度を1進める
        countMeta.setDisplayName( count+1 +"");
        countItem.setItemMeta(countMeta);

        // 次の演出
        DiveCore.plugin.delayedTask(2,() -> onAnim(player, PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId()).getLevelData().getRawPoint()));
    }
}
