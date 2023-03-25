package com.flora30.divecore.mechanic;

import com.flora30.diveapi.plugins.ItemAPI;
import com.flora30.diveapi.tools.BlockLoc;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;

public class Light {

    public static final Map<BlockLoc,Integer> lightLocations = new HashMap<>();
    public static final Set<BlockLoc> waters = new HashSet<>();

    public static void lightStop(){
        for (Player player : Bukkit.getOnlinePlayers()){
            for (BlockLoc before : lightLocations.keySet()){
                player.sendBlockChange(before.getLocation(),before.getBlock().getBlockData());
            }
        }
        lightLocations.clear();
    }

    public static void lightCheck(){
        long time = System.currentTimeMillis();

        // 今プレイヤーが見てる光の座標を追加
        for (Player player : Bukkit.getOnlinePlayers()){
            bright(player);
        }

        // 光を表示
        for (Player player : Bukkit.getOnlinePlayers()){
            // エラーが出たらやり直し（locationsに変更があった）
            while(true) {
                try{
                    for (BlockLoc after : lightLocations.keySet()) {
                        org.bukkit.block.data.type.Light data = (org.bukkit.block.data.type.Light) Material.LIGHT.createBlockData();
                        if (waters.contains(after)) {
                            data.setWaterlogged(true);
                        }
                        player.sendBlockChange(after.getLocation(),data);
                    }
                    // 処理が完了した
                    break;
                } catch (ConcurrentModificationException ignored){}
            }
        }

        // 現在の座標をカウント経過（取得中に変更されるとエラーを吐く）
        try {
            lightLocations.replaceAll((l, v) -> lightLocations.get(l) + 1);
        } catch (ConcurrentModificationException ignored) {}

        // 過去の光の記録を取得（取得中に変更されるとエラーを吐くのでやり直し）
        Set<BlockLoc> oldLocations;
        while(true) {
            try {
                oldLocations = new HashSet<>(lightLocations.keySet());
                break;
            } catch (ConcurrentModificationException ignored) {}
        }

        // プレイヤーに表示されている過去の光を削除
        for (Player player : Bukkit.getOnlinePlayers()){
            for (BlockLoc before : oldLocations){
                // たまにlightLocationsに残っておらず、NullPointerExceptionが出るが、その場合は他が消してくれているのでOK
                try{
                    // 時間経過したもの = 消す予定の光
                    if (lightLocations.get(before) > 3) {
                        player.sendBlockChange(before.getLocation(),before.getBlock().getBlockData());
                    }
                } catch (NullPointerException ignored) {}
            }
        }

        // 時間経過したものを削除
        // エラーが出たら飛ばす（locationsに変更があった -> ラグいときはこの処理が重なる）
        try {
            lightLocations.entrySet().removeIf(i -> i.getValue() > 3);
        } catch (ConcurrentModificationException ignored) {}

        // 光の更新にかかったms数
        time = System.currentTimeMillis() - time;
        //Bukkit.getLogger().info("lightCheck Time = "+time);
    }

    public static void bright(Player player){
        Location lightLocation = getLightAbleLocation(player, player.getLocation().add(0,1,0));

        if (lightLocation != null) {
            lightLocations.put(new BlockLoc(lightLocation), 0);
        }

        if (player.getInventory().getHelmet() != null){
            expandBright(player);
        }
    }

    public static void expandBright(Player player){
        Location lightLocation = player.getLocation().add(0,1.5,0);
        Location to = player.getLocation().add(0,1.5,0).add(player.getLocation().getDirection().normalize());
        Vector vector = new Vector(to.getX() - lightLocation.getX(), to.getY() - lightLocation.getY(), to.getZ() - lightLocation.getZ());
        vector.multiply(4.0);

        int count = 0;

        // ワールドをまたいだテレポートの瞬間はワールド比較エラーが出るが、その時は拡張ライトは必要ないので適当に流す
        try {
            while (lightLocation.distance(player.getLocation()) < 10 && count < 3) {
                lightLocation.add(vector);

                Location newLoc = getLightAbleLocation(player, lightLocation.clone());

                if (newLoc != null) {
                    lightLocations.put(new BlockLoc(newLoc), 0);
                }

                count++;
            }
        } catch (IllegalArgumentException ignored) {}
    }

    private static Location getLightAbleLocation(Player player, Location startLoc) {
        BlockLoc blockLoc = new BlockLoc(startLoc);
        if (isLightAble(player,blockLoc)) return blockLoc.getLocation();
        blockLoc.add(0,1,0);
        if (isLightAble(player,blockLoc)) return blockLoc.getLocation();
        blockLoc.add(0,-2,0);
        if (isLightAble(player,blockLoc)) return blockLoc.getLocation();
        blockLoc.add(1,1,0);
        if (isLightAble(player,blockLoc)) return blockLoc.getLocation();
        blockLoc.add(-2,0,0);
        if (isLightAble(player,blockLoc)) return blockLoc.getLocation();
        blockLoc.add(1,0,1);
        if (isLightAble(player,blockLoc)) return blockLoc.getLocation();
        blockLoc.add(0,0,-2);
        if (isLightAble(player,blockLoc)) return blockLoc.getLocation();
        return null;
    }
    private static boolean isLightAble(Player player, BlockLoc loc) {
        switch (loc.getBlock().getType()) {
            case AIR,CAVE_AIR,VOID_AIR -> {}
            case WATER,SEAGRASS,TALL_SEAGRASS -> {
                waters.add(new BlockLoc(loc.getLocation()));
            }
            default -> {
                return false;
            }
        }

        //if (!BlockUtil.isIgnoreBlockType(loc.getBlock())) return false;
        if (ItemAPI.isRopeLocation(player,loc)) return false;
        return !ItemAPI.isLootLocation(loc);
    }
}
