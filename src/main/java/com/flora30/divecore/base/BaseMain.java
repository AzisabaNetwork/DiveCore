package com.flora30.divecore.base;

import com.flora30.diveapi.data.PlayerData;
import com.flora30.diveapi.event.HelpEvent;
import com.flora30.diveapi.plugins.ItemAPI;
import com.flora30.diveapi.plugins.RegionAPI;
import com.flora30.diveapi.tools.HelpType;
import com.flora30.divecore.base.gui.BaseGUI;
import com.flora30.divecore.data.PlayerDataMain;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.entity.Player;

import java.util.*;

public class BaseMain {

    //拠点データ（レイヤーごとの設定）
    public static final Map<String,BaseData> baseDataMap = new HashMap<>();

    //拠点データ（ワールド全体）
    public static final Map<Integer,Base> baseMap = new HashMap<>();

    //一時保存データ（BaseUpgrade連携用）
    public static final List<Location> baseUpgradeInstantList = new ArrayList<>();

    public static void onClickBase(Player player, Location location){
        int baseId = getBaseId(location);
        if (baseId == -1){
            //Bukkit.getLogger().info("baseId isn't exist");
            return;
        }

        Base base = baseMap.get(baseId);

        //Bukkit.getLogger().info("remain = "+base.getRemain());

        // 料理判定
        if (base.getLevel() >= 2) {
            Bukkit.getPluginManager().callEvent(new HelpEvent(player, HelpType.Base_Cook));
            if (ItemAPI.cook(player, base.getLocation())) return;
        }

        PlayerData data = PlayerDataMain.getPlayerData(player.getUniqueId());
        if (data.coolDownMap.containsKey("Cook")) {
            int cookCooldown = data.coolDownMap.get("Cook");
            if (cookCooldown != 0) return;
        }

        Bukkit.getPluginManager().callEvent(new HelpEvent(player, HelpType.BaseGUI));
        if (base.getLevel() >= 3) {
            Bukkit.getPluginManager().callEvent(new HelpEvent(player, HelpType.BaseGUI_PowerUp));
        }
        BaseGUI.open(player, baseId);
    }

    public static int getBaseId(Location location){
        for (int id : baseMap.keySet()){
            Location baseLoc = baseMap.get(id).getLocation();
            if (baseLoc.getBlockX() == location.getBlockX() &&
                    baseLoc.getBlockY() == location.getBlockY() &&
                    baseLoc.getBlockZ() == location.getBlockZ()){
                return id;
            }
        }
        return -1;
    }


    public static void registerBase(Player player, Location location){
        int id = baseMap.keySet().size();
        while(baseMap.get(id) != null){
            id--;
        }

        Base base = new Base();
        base.setLocation(location);
        base.setFace(((Campfire)location.getBlock().getBlockData()).getFacing());

        baseMap.put(id,base);

        String layer = RegionAPI.getLayerName(base.getLocation());
        BaseConfig.save(layer,id);

        player.sendMessage("拠点を登録しました（id: "+id+"）");
    }

    public static void unRegisterBase(Player player, Location location){
        int id = BaseMain.getBaseId(location);
        if (id == -1){
            return;
        }

        BaseMain.baseMap.remove(id);

        BaseConfig.remove(id);

        player.sendMessage("拠点を登録解除しました（id: "+id+"）");
    }
}
