package com.flora30.divecore.data;

import com.flora30.diveapin.data.player.PlayerData;
import com.flora30.diveapin.data.player.PlayerDataObject;
import com.flora30.diveapin.event.FirstJoinEvent;
import com.flora30.divecore.DiveCore;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class DataTrigger {
    public static boolean logMode = false;

    public static void onJoin(PlayerJoinEvent e){
        PlayerDataObject.INSTANCE.getPlayerIdMap().put(e.getPlayer().getDisplayName(),e.getPlayer().getUniqueId());
        DiveCore.plugin.delayedTask(1, () -> {
            PlayerDataConfig ls = new PlayerDataConfig();
            ls.load(e.getPlayer().getUniqueId());
            PlayerConfig playerConfig = new PlayerConfig();
            playerConfig.load(e.getPlayer());

            checkFirstJoin(e.getPlayer());
        });
        resetGameMode(e.getPlayer().getUniqueId());
    }

    private static void checkFirstJoin(Player player) {
        PlayerData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId());

        if (data != null && PlayerDataObject.INSTANCE.getLoadedPlayerSet().contains(player.getUniqueId())) {
            if (data.isFirstJoin()) {
                FirstJoinEvent event = new FirstJoinEvent(player);
                Bukkit.getLogger().info("FirstJoinEvent called");
                Bukkit.getPluginManager().callEvent(event);
                data.setFirstJoin(false);
            }
            else {
                Bukkit.getLogger().info("not first join");
            }
            return;
        }

        DiveCore.plugin.delayedTask(5,() -> checkFirstJoin(player));
    }

    private static void resetGameMode(UUID id){
        if (PlayerDataObject.INSTANCE.getAdminSet().contains(id)){
            return;
        }

        Player player = Bukkit.getPlayer(id);
        if (player == null){
            DiveCore.plugin.delayedTask(1, () -> resetGameMode(id));
            return;
        }
        if (!(player.getGameMode() == GameMode.ADVENTURE)){
            player.setGameMode(GameMode.ADVENTURE);
        }
        DiveCore.plugin.delayedTask(5, () -> {
            resetGameMode(id);
        });
    }

    public static void onLogout(PlayerQuitEvent e){
        PlayerDataConfig ls = new PlayerDataConfig();
        ls.saveAsync(e.getPlayer().getUniqueId());
        PlayerConfig playerConfig = new PlayerConfig();
        playerConfig.saveAsync(e.getPlayer());
    }



    /*

     */

    public static void onCommand(String command, String command2){
        Set<UUID> adminSet = PlayerDataObject.INSTANCE.getAdminSet();
        //list分岐
        if (command.equals("list")){
            Bukkit.getLogger().info("------list------");
            for (UUID adminId : adminSet){
                OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(adminId);
                Bukkit.getLogger().info(offPlayer.getName());
            }
            return;
        }
        // packet分岐
        if (command.equals("log")) {
            DataTrigger.logMode = !DataTrigger.logMode;
            return;
        }


        UUID id = PlayerDataObject.INSTANCE.getPlayerIdMap().get(command2);
        if (id == null){
            Bukkit.getLogger().info("[DiveCore-Admin]名前が登録されていません");
            return;
        }
        if (Bukkit.getServer().getPlayer(id) != null){
            Bukkit.getLogger().info("[DiveCore-Admin]プレイヤーがログインしている間は登録できません");
            return;
        }
        OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(id);
        switch (command) {
            case "add" -> {
                if (offPlayer.getName() == null) {
                    Bukkit.getLogger().info("[DiveCore-Admin]名前が間違っています");
                    return;
                }
                adminSet.add(offPlayer.getUniqueId());
                Bukkit.getLogger().info("[DiveCore-Admin]" + offPlayer.getName() + "を登録しました");
            }
            case "remove" -> {
                if (offPlayer.getName() == null) {
                    Bukkit.getLogger().info("[DiveCore-Admin]名前が間違っています");
                    return;
                }
                if (!adminSet.contains(id)) {
                    Bukkit.getLogger().info("[DiveCore-Admin]IDがadminリストに存在しません");
                    return;
                }
                adminSet.remove(id);
                Bukkit.getLogger().info("[DiveCore-Admin]" + offPlayer.getName() + "を解除しました");
            }
        }
    }

    public static void onTickLimit(Player player){
        checkItem(player);
    }

    private static void checkItem(Player player){
        /*
        Inventory inventory = player.getInventory();
        //各アイテムの所持数マップ
        Map<Integer,Integer> itemCountMap = new HashMap<>();

        for (ItemStack item : inventory.getContents()){
            if (item == null){
                continue;
            }
            int id = ItemUtil.getId(item);
            if (id == -1){
                //if (item.getItemMeta() != null){
                    //Bukkit.getLogger().info("item["+item.getItemMeta().getDisplayName()+"]からIDを取得できません");
                //}
                continue;
            }

            int amount = PlayerItem.countItem(player,id,true);
            if (amount == 0){
                Bukkit.getLogger().info("item-ID["+id+"]の所持数が0と判定されました");
                continue;
            }
            itemCountMap.put(id,amount);
        }

        for (int id : itemCountMap.keySet()){
            ItemData data = DataMain.getItemData(id);
            if (data == null){
                continue;
            }
            int maxSize = data.getMaxSize();
            //Bukkit.getLogger().info("item検知："+id+"(current: "+itemCountMap.get(id)+") (max: "+maxSize+")");
            if (itemCountMap.get(id) > maxSize){
                PlayerItem.limitItem(player,id,itemCountMap.get(id) - maxSize);
            }
        }
        //判定終了
        //Bukkit.getLogger().info("[DiveCore-Item]"+player.getDisplayName()+"の所持品判定を行いました");

         */
    }
}
