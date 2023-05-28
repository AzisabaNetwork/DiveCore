package com.flora30.divecore.data;

import com.flora30.divelib.ItemMain;
import com.flora30.divelib.data.player.PlayerDataObject;
import com.flora30.divelib.util.PlayerItem;
import com.flora30.divecore.DiveCore;
import com.flora30.divecore.tools.StringUtil;
import com.flora30.divedb.DiveDBAPI;
import com.flora30.diveconstant.data.item.ItemData;
import com.flora30.diveconstant.data.item.ItemDataObject;
import com.flora30.diveconstant.data.item.ItemType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerConfig {

    public PlayerConfig(){
        invItemLabels = new ArrayList<>();
        for(int i = 0; i < 41; i++){
            invItemLabels.add("Inv"+i);
        }

        enderItemLabels = new ArrayList<>();
        for (int i = 0; i < 27; i++){
            enderItemLabels.add("Ender"+i);
        }
    }

    public static final String locationLabel = "Location";
    public static final String foodLabel = "Food";
    public static final String hpLabel = "Hp";
    public static final String moneyLabel = "Money";

    public static List<String> invItemLabels;
    public static List<String> enderItemLabels;

    public void loadAll(){
        for(Player player : Bukkit.getOnlinePlayers()){
            load(player);
        }
    }

    public void load(Player player){
        if(DiveCore.plugin.isEnabled()){
            DiveCore.plugin.asyncTask(() -> loadExecute(player));
        }
        else{
            loadExecute(player);
        }
    }

    public void loadExecute(Player player){
        // 処理時間を計測する
        long firstTime = System.currentTimeMillis();

        UUID uuid = player.getUniqueId();

        String table = "player";
        DiveDBAPI.insertSQL(table,uuid);

        //Location
        Location location = loadLocation(table, uuid, locationLabel);
        if (location != null){
            DiveCore.plugin.syncTask(() -> player.teleport(location));
        }

        //InvItem
        PlayerInventory playerInv = player.getInventory();
        for(int i = 0; i < 41; i++){
            ItemStack item = loadItem(table, uuid, invItemLabels.get(i));
            playerInv.setItem(i,item);
        }

        //EnderItem
        Inventory enderInv = player.getEnderChest();
        enderInv.clear();
        for (int i = 0; i < 27; i++){
            ItemStack item = loadItem(table, uuid, enderItemLabels.get(i));
            enderInv.setItem(i,item);
        }

        //HP
        String hp = DiveDBAPI.loadSQL(table,uuid,hpLabel,"20");
        try{
            //Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(Double.parseDouble(hp));
            player.setHealth(Double.parseDouble(hp));
        } catch (NumberFormatException ignored){}

        //Food
        String food = DiveDBAPI.loadSQL(table,uuid,foodLabel,"20");
        try{
            player.setFoodLevel(Integer.parseInt(food));
        } catch (NumberFormatException ignored){}

        // ロード完了を記録する
        PlayerDataObject.INSTANCE.getLoadedPlayerSet().add(uuid);

        // 処理時間を計測する
        long lastTime = System.currentTimeMillis();
        long taskTime = (lastTime - firstTime);
        Bukkit.getLogger().info("[DiveCore-Player]"+player.getDisplayName()+"をロードしました（"+taskTime+"ms）");
    }

    public void saveAll(){
        for (Player player : Bukkit.getOnlinePlayers()){
            save(player);
        }
    }

    public void save(Player player){
        // 処理時間を計測する
        long firstTime = System.currentTimeMillis();

        UUID uuid = player.getUniqueId();

        String table = "player";
        DiveDBAPI.insertSQL(table,uuid);

        //Location
        saveLocation(table,uuid,locationLabel,player.getLocation());

        //InvItem
        PlayerInventory playerInv = player.getInventory();
        for (int i = 0; i < 41; i++){
            saveItem(table,uuid, invItemLabels.get(i),playerInv.getItem(i));
        }

        //EnderItem
        Inventory enderInv = player.getEnderChest();
        for(int i = 0; i < 27; i++){
            saveItem(table,uuid, enderItemLabels.get(i),enderInv.getItem(i));
        }

        //Hp
        DiveDBAPI.saveSQL(table,uuid,hpLabel,String.valueOf(player.getHealth()));

        //Food
        DiveDBAPI.saveSQL(table,uuid,foodLabel,String.valueOf(player.getFoodLevel()));

        // 処理時間を計測する
        long lastTime = System.currentTimeMillis();
        long taskTime = (lastTime - firstTime);
        Bukkit.getLogger().info("[DiveCore-Player]"+player.getDisplayName()+"をセーブしました（"+taskTime+"ms）");
    }

    public void saveAsync(Player player){
        PlayerLog playerLog = new PlayerLog(player);

        DiveCore.plugin.asyncTask(() -> {
            // 処理時間を計測する
            long firstTime = System.currentTimeMillis();

            String table = "player";
            DiveDBAPI.insertSQL(table,playerLog.uuid);
            DiveDBAPI.saveSQL(table,playerLog.uuid,locationLabel,playerLog.locationSQL);
            DiveDBAPI.saveSQL(table,playerLog.uuid,hpLabel,playerLog.hpSQL);
            DiveDBAPI.saveSQL(table,playerLog.uuid,foodLabel,playerLog.foodSQL);
            int i = 0;
            for (; i < 27; i++) {
                DiveDBAPI.saveSQL(table,playerLog.uuid,invItemLabels.get(i),playerLog.inventorySQLList.get(i));
                DiveDBAPI.saveSQL(table,playerLog.uuid,enderItemLabels.get(i),playerLog.enderSQLList.get(i));
            }
            for (; i < 41; i++) {
                DiveDBAPI.saveSQL(table,playerLog.uuid,invItemLabels.get(i),playerLog.inventorySQLList.get(i));
            }

            // 処理時間を計測する
            long lastTime = System.currentTimeMillis();
            long taskTime = (lastTime - firstTime);
            Bukkit.getLogger().info("[DiveCore-Player]"+playerLog.name+"をセーブしました（"+taskTime+"ms）");
        });
    }

    private Location loadLocation(String table, UUID uuid, String label){
        String str = DiveDBAPI.loadSQL(table,uuid,label,null);
        if (str == null){
            return null;
        }

        List<String> list = StringUtil.convertList(str);
        if (list.size() < 6){
            return null;
        }
        //0：World
        String worldName = list.get(0);
        double x,y,z;
        float yaw,pitch;
        try {
            //1:x
            x = Double.parseDouble(list.get(1)) + 0.5;
            //2:y
            y = Double.parseDouble(list.get(2));
            //3:z
            z = Double.parseDouble(list.get(3)) + 0.5;
            //4:yaw
            yaw = Float.parseFloat(list.get(4));
            //5:pitch
            pitch = Float.parseFloat(list.get(5));
        } catch (NullPointerException | NumberFormatException e){
            return null;
        }

        World world = Bukkit.getWorld(worldName);
        if (world == null){
            return null;
        }
        Location location = world.getSpawnLocation().clone();

        location.setX(x);
        location.setY(y);
        location.setZ(z);
        location.setYaw(yaw);
        location.setPitch(pitch);

        return location;
    }

    private void saveLocation(String table, UUID uuid, String label, Location location){
        List<String> list = new ArrayList<>();

        //Locationの中身を入れる
        if (location.getWorld() == null){
            return;
        }
        list.add(location.getWorld().getName());
        list.add(String.valueOf(location.getBlockX()));
        list.add(String.valueOf(location.getBlockY()));
        list.add(String.valueOf(location.getBlockZ()));
        list.add(String.format("%10.2f",location.getYaw()));
        list.add(String.format("%10.2f",location.getPitch()));

        //1つのStringにまとめる
        String str = StringUtil.convertString(list);
        //保存
        DiveDBAPI.saveSQL(table,uuid,label,str);
    }

    private ItemStack loadItem(String table, UUID uuid, String label){
        String str = DiveDBAPI.loadSQL(table,uuid,label,null);
        if (str == null){
            return null;
        }

        List<String> list = StringUtil.convertList(str);
        if (list.size() < 8){
            return null;
        }
        //2-7：Option
        int itemId,amount;
        String addition1;
        try{
            //0：ItemId
            itemId = Integer.parseInt(list.get(0));
            //1：Amount
            amount = Integer.parseInt(list.get(1));
            // 2-3 : Addition1
            addition1 = list.get(3);
        } catch (NullPointerException | NumberFormatException e){
            return null;
        }

        ItemStack item = ItemMain.INSTANCE.getItemWithValue(itemId,addition1);
        if (item != null){
            item.setAmount(amount);
        }

        return item;
    }

    private void saveItem(String table, UUID uuid, String label, ItemStack item){
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
            // 耐久値（防具・採集ツール）
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
        String str = StringUtil.convertString(list);
        //保存
        DiveDBAPI.saveSQL(table,uuid,label,str);
    }
}
