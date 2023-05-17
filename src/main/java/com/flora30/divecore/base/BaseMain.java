package com.flora30.divecore.base;

import com.flora30.diveapin.data.Base;
import com.flora30.diveapin.data.BaseObject;
import com.flora30.diveapin.data.player.PlayerData;
import com.flora30.diveapin.data.player.PlayerDataObject;
import com.flora30.diveapin.event.HelpEvent;
import com.flora30.diveapin.event.HelpType;
import com.flora30.divecore.base.gui.BaseGUI;
import com.flora30.divenew.data.BaseDataObject;
import com.flora30.divenew.data.BaseLocation;
import com.flora30.divenew.data.LayerObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BaseMain {

    //拠点データ（レイヤーごとの設定）
    //public static final Map<String,BaseData> baseDataMap = new HashMap<>();

    //拠点データ（ワールド全体）
    //public static final Map<Integer,Base> baseMap = new HashMap<>();

    //一時保存データ（BaseUpgrade連携用）
    public static final List<Location> baseUpgradeInstantList = new ArrayList<>();

    public static void onClickBase(Player player, Location location){
        int baseId = getBaseId(location);
        if (baseId == -1){
            //Bukkit.getLogger().info("baseId isn't exist");
            return;
        }

        Base base = BaseObject.INSTANCE.getBaseMap().get(baseId);
        BaseLocation baseLocation = BaseDataObject.INSTANCE.getBaseLocationMap().get(baseId);

        //Bukkit.getLogger().info("remain = "+base.getRemain());

        // 料理判定
        if (base.getLevel() >= 2) {
            Bukkit.getPluginManager().callEvent(new HelpEvent(player, HelpType.Base_Cook));
            if (Cook.cook(player, baseLocation.getLocation())) return;
        }

        PlayerData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId());
        if (data.getCoolDownMap().containsKey("Cook")) {
            int cookCooldown = data.getCoolDownMap().get("Cook");
            if (cookCooldown != 0) return;
        }

        Bukkit.getPluginManager().callEvent(new HelpEvent(player, HelpType.BaseGUI));
        if (base.getLevel() >= 3) {
            Bukkit.getPluginManager().callEvent(new HelpEvent(player, HelpType.BaseGUI_PowerUp));
        }
        BaseGUI.open(player, baseId);
    }


    public static int getBaseId(Location location) {
        // EntrySetを一列に並べる
        return BaseDataObject.INSTANCE.getBaseLocationMap().entrySet().stream()
                // Locationのブロック座標が全て一致するものをフィルターする
                .filter(entry -> {
                    BaseLocation baseLocation = entry.getValue();
                    return baseLocation.getLocation().getBlockX() == location.getBlockX()
                            && baseLocation.getLocation().getBlockY() == location.getBlockY()
                            && baseLocation.getLocation().getBlockZ() == location.getBlockZ();
                })
                // keyを取り出す（その場所のIDを取得する）
                .map(Map.Entry::getKey)
                // 最初のものだけ
                .findFirst()
                // 取得できなかったときは -1
                .orElse(-1);
    }

    /**
     * 拠点の登録より前に、階層の登録が必要になった
     * （拠点が町の中であるかの判定を自動化するため）
     */
    public static void registerBase(Player player, Location location){
        int id = BaseDataObject.INSTANCE.getBaseLocationMap().size();
        while (BaseDataObject.INSTANCE.getBaseLocationMap().get(id) != null) {
            id--;
        }

        String layerName = LayerObject.INSTANCE.getLayerName(location);
        if (layerName == null) {
            player.sendMessage("拠点の登録に失敗しました（階層の設定が必要です）");
        }

        BaseLocation baseLocation = new BaseLocation(
                location,
                ((Campfire)location.getBlock().getBlockData()).getFacing(),
                LayerObject.INSTANCE.getLayerMap().get(layerName).isTown()
                );

        BaseDataObject.INSTANCE.getBaseLocationMap().put(id, baseLocation);
        BaseObject.INSTANCE.getBaseMap().put(id,new Base());
        BaseConfig.save(layerName,id);

        player.sendMessage("拠点を登録しました（id: "+id+"）");
    }

    public static void unRegisterBase(Player player, Location location){
        int id = BaseMain.getBaseId(location);
        if (id == -1){
            return;
        }

        BaseDataObject.INSTANCE.getBaseLocationMap().remove(id);
        BaseObject.INSTANCE.getBaseMap().remove(id);

        BaseConfig.remove(id);

        player.sendMessage("拠点を登録解除しました（id: "+id+"）");
    }
}
