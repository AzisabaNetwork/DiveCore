package com.flora30.divecore.level;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.flora30.divelib.ItemMain;
import com.flora30.divelib.data.player.PlayerData;
import com.flora30.divelib.data.player.PlayerDataObject;
import com.flora30.divelib.event.LayerChangeEvent;
import com.flora30.divelib.event.LayerLoadEvent;
import com.flora30.divecore.DiveCore;
import com.flora30.divecore.level.gui.SetPointGUI;
import com.flora30.divecore.level.gui.StatusGUI;
import com.flora30.divedb.DiveDBAPI;
import com.flora30.diveconstant.data.item.ItemData;
import com.flora30.diveconstant.data.item.ItemDataObject;
import com.flora30.diveconstant.data.item.ItemType;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;

public class LevelTrigger {

    // 経験値追加はLayerでやる
    /*
    public static void onLayerLoad(LayerLoadEvent e){
        int exp = e.getSection().getInt("exp",10);
        Bukkit.getLogger().info("[LayerLoad]Event listened "+e.getKey()+" -> "+exp);
        LevelMain.putLayerExp(e.getKey(),exp);
    }
     */

    public static void onTickDisplay(Player player){
        LevelMain.display(player);
    }

    public static void onLayerChange(LayerChangeEvent e){
        Player player = Bukkit.getPlayer(e.getUuid());
        if (player == null){
            return;
        }
        LevelMain.onLayerChange(player,e.getNextLayer());
    }

    public static void onMobDeath(MythicMobDeathEvent e){
        LevelMain.onMobDeath(e.getKiller(),e.getMobType());
    }

    public static void onInventoryDrag(InventoryDragEvent e) {
        // 装備判定
        if (e.getInventory().getType() == InventoryType.CRAFTING) {
            //Bukkit.getLogger().info("Player inv click");

            PlayerData playerData = PlayerDataObject.INSTANCE.getPlayerDataMap().get(e.getWhoClicked().getUniqueId());
            if (playerData == null) return;

            // 判定するアイテム
            ItemStack checkItem = e.getOldCursor();

            ItemData data = ItemDataObject.INSTANCE.getItemDataMap().get(ItemMain.INSTANCE.getItemId(checkItem));
            if (data == null) return;

            if (data.getType() == ItemType.Armor && playerData.getLevelData().getLevel() < data.getLevel()) {
                e.setCancelled(true);
                e.getWhoClicked().sendMessage(ChatColor.RED + "防具の必要レベルに達していません");
            }
        }

    }

    public static void onInventoryClick(InventoryClickEvent e){
        //インベントリ以外の時は何も反応しない
        if(e.getClickedInventory() == null){
            return;
        }

        // 経験値アイテム判定
        if (e.getClickedInventory().getType() == InventoryType.CHEST) {
            //クリック先のアイテムを取得
            ItemStack item = e.getClickedInventory().getItem(e.getSlot());
            //nullの時は反応しない
            if(item == null){
                return;
            }
            // 経験値アイテム？
            ItemData data = ItemDataObject.INSTANCE.getItemDataMap().get(ItemMain.INSTANCE.getItemId(item));
            if (data == null) return;
            if (data.getExp() != 0){
                e.setCancelled(true);
                LevelMain.addExp((Player)e.getWhoClicked(),data.getExp() * item.getAmount());
                e.getClickedInventory().setItem(e.getSlot(),null);
            }
        }

        // 装備判定
        if (e.getClickedInventory().getType() == InventoryType.PLAYER) {
            //Bukkit.getLogger().info("Player inv click");

            PlayerData playerData = PlayerDataObject.INSTANCE.getPlayerDataMap().get(e.getWhoClicked().getUniqueId());
            if (playerData == null) return;

            // 判定するアイテム
            ItemStack checkItem = null;

            // 防具スロットを通常クリックの場合：カーソルのアイテムを取得
            if (e.getSlotType() == InventoryType.SlotType.ARMOR) {
                checkItem = e.getCursor();
            }
            // Shift+クリックの場合：クリック地点のアイテムを取得
            else if (e.getClick() == ClickType.SHIFT_LEFT || e.getClick() == ClickType.SHIFT_RIGHT) {
                checkItem = e.getCurrentItem();
                //Bukkit.getLogger().info("item = "+checkItem);
            }

            if (checkItem == null) return;
            ItemData data = ItemDataObject.INSTANCE.getItemDataMap().get(ItemMain.INSTANCE.getItemId(checkItem));
            if (data == null) return;

            if (data.getType() == ItemType.Armor && playerData.getLevelData().getLevel() < data.getLevel()) {
                e.setCancelled(true);
                e.getWhoClicked().sendMessage(ChatColor.RED + "防具の必要レベルに達していません");
            }
        }
    }

    public static void onInteract(PlayerInteractEvent e) {
        PlayerData playerData = PlayerDataObject.INSTANCE.getPlayerDataMap().get(e.getPlayer().getUniqueId());
        if (playerData == null) return;
        ItemStack item = e.getItem();
        if (item == null) return;

        ItemData itemData = ItemDataObject.INSTANCE.getItemDataMap().get(ItemMain.INSTANCE.getItemId(item));
        if (itemData == null) return;

        if (itemData.getType() == ItemType.Armor && playerData.getLevelData().getLevel() < itemData.getLevel()) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + "防具の必要レベルに達していません");
        }
    }


    public static void onCommand(Player player, String args1, String args2){
        //""=null
        switch (args1) {
            case "status" -> StatusGUI.open(player);
            case "point" -> SetPointGUI.open(player);
            case "exp" -> {
                try{
                    LevelMain.addExp(player, Integer.parseInt(args2));
                } catch (NumberFormatException e) {
                    player.sendMessage("経験値の量を正しく入力してください");
                }
            }
            default -> player.sendMessage("/level [status | point | exp]");
        }
    }

    public static void onJoin(PlayerJoinEvent e){
        PlayerData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(e.getPlayer().getUniqueId());
        if (data == null){
            //Bukkit.getLogger().info("[DiveCore-Level]データ待ち - "+e.getPlayer().getDisplayName());
            DiveCore.plugin.delayedTask(2, () -> onJoin(e));
            return;
        }

        LevelMain.setMaxHpSt(e.getPlayer());
        //現在HPが20を超える場合があるので、もう一度設定する
        String hp = DiveDBAPI.loadSQL("player",e.getPlayer().getUniqueId(),"Hp","20");
        try{
            //Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(Double.parseDouble(hp));
            e.getPlayer().setHealth(Double.parseDouble(hp));
            //Bukkit.getLogger().info("health is "+e.getPlayer().getHealth() + " | set " +hp);

            // HPを更新するパケット
            ProtocolManager manager = ProtocolLibrary.getProtocolManager();
            PacketContainer packet = new PacketContainer(PacketType.Play.Server.UPDATE_HEALTH);
            packet.getFloat().write(0,(float)e.getPlayer().getHealth());
            packet.getIntegers().write(0,data.getFood());
            packet.getFloat().write(1,e.getPlayer().getSaturation());
            manager.sendServerPacket(e.getPlayer(), packet);

        } catch (NumberFormatException ignored){} catch (InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }

        data.setCurrentST(data.getMaxST());
    }
}
