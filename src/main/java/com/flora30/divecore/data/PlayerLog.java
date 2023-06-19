package com.flora30.divecore.data;

import com.flora30.divelib.ItemMain;
import com.flora30.divelib.util.PlayerItem;
import com.flora30.divecore.tools.StringUtil;
import com.flora30.divelib.data.item.ItemData;
import com.flora30.divelib.data.item.ItemDataObject;
import com.flora30.divelib.data.item.ItemType;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerLog {

    public final UUID uuid;
    public final String name;

    public final List<String> inventorySQLList = new ArrayList<>(41);
    public final List<String> enderSQLList = new ArrayList<>(27);
    public final String hpSQL;
    public final String foodSQL;

    // null可能性あり
    public final String locationSQL;

    public PlayerLog(Player player) {
        // 初期化（Nullが41個詰まったList）
        for (int i = 0; i < 41; i++) {
            inventorySQLList.add(null);
        }
        for (int i = 0; i < 27; i++) {
            enderSQLList.add(null);
        }

        // 保存SQL用の文字列を入れる
        // Inventory
        PlayerInventory inventory = player.getInventory();
        for (int i = 0; i < 41; i++){
            inventorySQLList.set(i,getItemString(inventory.getItem(i)));
        }
        // EnderChest
        Inventory enderInv = player.getEnderChest();
        for (int i = 0; i < 27; i++) {
            enderSQLList.set(i,getItemString(enderInv.getItem(i)));
        }
        locationSQL = getLocationString(player.getLocation());
        hpSQL = String.valueOf(player.getHealth());
        foodSQL = String.valueOf(player.getFoodLevel());
        uuid = player.getUniqueId();
        name = player.getDisplayName();
    }

    private String getLocationString(Location location) {
        List<String> list = new ArrayList<>();

        //Locationの中身を入れる
        if (location.getWorld() == null){
            return null;
        }
        list.add(location.getWorld().getName());
        list.add(String.valueOf(location.getBlockX()));
        list.add(String.valueOf(location.getBlockY()));
        list.add(String.valueOf(location.getBlockZ()));
        list.add(String.format("%10.2f",location.getYaw()));
        list.add(String.format("%10.2f",location.getPitch()));

        //1つのStringにまとめる
        return StringUtil.convertString(list);
    }

    private String getItemString(ItemStack item) {
        int itemId, amount;
        String addition1;
        if (item == null){
            itemId = -1;
            amount = 0;
            addition1 = "null";
        }
        else{
            itemId = ItemMain.INSTANCE.getItemId(item);
            amount = item.getAmount();

            //SaveItemEvent event = new SaveItemEvent(item);
            //Bukkit.getPluginManager().callEvent(event);


            ItemData data = ItemDataObject.INSTANCE.getItemDataMap().get(itemId);

            if (data == null) {
                addition1 = "null";
            }
            // 遺物価値
            else if (data.getArtifactData() != null) {
                addition1 = String.valueOf(PlayerItem.INSTANCE.getInt(item,"artifactValue"));
            }
            // 耐久値（防具）
            else if (data.getType() == ItemType.Armor || data.getToolData() != null) {
                //Bukkit.getLogger().info("Armor found");
                Damageable damageable = (Damageable) item.getItemMeta();
                if (damageable != null) {
                    //Bukkit.getLogger().info("damageable -> "+damageable.getDamage());
                    addition1 = String.valueOf(damageable.getDamage());
                }
                else {
                    addition1 = "null";
                }
            }
            else{
                addition1 = "null";
            }
        }



        List<String> list = new ArrayList<>();
        list.add(String.valueOf(itemId));
        list.add(String.valueOf(amount));

        //option1,2,3
        list.add("a");
        list.add(addition1);
        list.add("b");
        list.add(String.valueOf(0));
        list.add("c");
        list.add(String.valueOf(0));

        //1つのStringにまとめる
        return StringUtil.convertString(list);
    }

}
