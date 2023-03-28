package com.flora30.divecore.base;

import com.flora30.data.Base;
import com.flora30.data.BaseObject;
import com.flora30.diveapi.DiveAPI;
import com.flora30.diveapi.event.LayerLoadEvent;
import data.BaseDataObject;
import data.BaseLocation;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.api.exceptions.InvalidMobTypeException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.entity.Entity;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class BaseTrigger {

    public static void onLayerLoad(LayerLoadEvent e) {
        BaseConfig.loadBaseData(e.getKey(), e.getSection());
    }

    public static void onPlace(BlockPlaceEvent event){
        if (event.getBlockPlaced().getType().equals(Material.CAMPFIRE) ||
        event.getBlockPlaced().getType().equals(Material.SOUL_CAMPFIRE)){
            BaseMain.registerBase(event.getPlayer(), event.getBlockPlaced().getLocation());
        }
    }

    public static void onBreak(BlockBreakEvent event){
        if (event.getBlock().getType().equals(Material.CAMPFIRE) ||
                event.getBlock().getType().equals(Material.SOUL_CAMPFIRE)){
            BaseMain.unRegisterBase(event.getPlayer(),event.getBlock().getLocation());
        }
    }

    public static void onRightClick(PlayerInteractEvent e){
        if (e.getClickedBlock() == null){
            return;
        }
        if (e.getClickedBlock().getType().equals(Material.CAMPFIRE) ||
        e.getClickedBlock().getType().equals(Material.SOUL_CAMPFIRE)){
            //Bukkit.getLogger().info("clicked campfire");
            BaseMain.onClickBase(e.getPlayer(), e.getClickedBlock().getLocation());
        }
    }

    public static void onTickBaseGuard(){
        long lagLimit_ms = DiveAPI.lagTime + 50L;

        for (int id : BaseObject.INSTANCE.getBaseMap().keySet()) {
            Base base = BaseObject.INSTANCE.getBaseMap().get(id);
            BaseLocation baseLocation = BaseDataObject.INSTANCE.getBaseLocationMap().get(id);

            if (!base.isPrepared()){
                // prepare location set -> chunk load lagging
                if (System.currentTimeMillis() > lagLimit_ms) {
                    continue;
                }

                Location location = baseLocation.getLocation();
                if(!location.isWorldLoaded()) continue;
                if (location.getBlock().getType() != Material.CAMPFIRE && location.getBlock().getType() != Material.SOUL_CAMPFIRE){
                    location.getBlock().setType(Material.CAMPFIRE);
                    ((Campfire)location.getBlock().getBlockData()).setFacing(baseLocation.getFace());
                }
                base.setPrepared(true);
            }
            if (base.getLevel() > 0){
                if (System.currentTimeMillis() > DiveAPI.lagTime + 500L) {
                    Bukkit.getLogger().info("[DiveCore-Base]guard lag skip");
                    return;
                }

                try{
                    Location spawn = baseLocation.getLocation().clone();
                    MythicMobs.inst().getAPIHelper().spawnMythicMob("BaseGuard",spawn.add(0.5,0,0.5));
                } catch (InvalidMobTypeException e){
                    Bukkit.getLogger().info("[DiveCore-Base]mob名BaseGuardが確認出来ません");
                }
            }
        }
    }

    public static void onTick(){
        for (Base base : BaseObject.INSTANCE.getBaseMap().values()){
            // 拠点の残り時間を減らす
            if (base.getRemain() != 0){
                base.setRemain(base.getRemain() - 1);
            }

            // 拠点の残り時間が0になったとき
            if (base.getRemain() == 0 && base.getLevel() > 0){
                base.setLevel(0);
                if (base.getModel() != null) {
                    Entity beforeModel = Bukkit.getEntity(base.getModel());
                    if(beforeModel != null) beforeModel.remove();
                    base.setModel(null);
                }
            }
        }
    }
}
