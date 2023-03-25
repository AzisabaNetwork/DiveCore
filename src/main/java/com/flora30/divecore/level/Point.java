package com.flora30.divecore.level;

import com.flora30.divecore.level.type.PointType;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Point {

    // ポイントを入れた時に追加されるもの
    public static Map<Integer,PointData> intMap = new HashMap<>();
    public static Map<Integer,PointData> vitMap = new HashMap<>();
    public static Map<Integer,PointData> atkMap = new HashMap<>();
    public static Map<Integer,PointData> lucMap = new HashMap<>();

    /**
     * 現在の「次」に得られるものがある場所
     */
    public static int getNextPoint(PointType type, int point) {
        Map<Integer,PointData> map;
        switch (type){
            case Int -> map = intMap;
            case Vit -> map = vitMap;
            case Atk -> map = atkMap;
            case Luc -> map = lucMap;
            default -> {return -1;}
        }

        for (int mapPoint : map.keySet()) {
            if (mapPoint <= point) continue;
            return mapPoint;
        }

        return -1;
    }
    
    // 現在のポイントで適用されるもの
    public static Map<Integer,PointData> intApplyMap = new HashMap<>();
    public static Map<Integer,PointData> vitApplyMap = new HashMap<>();
    public static Map<Integer,PointData> atkApplyMap = new HashMap<>();
    public static Map<Integer,PointData> lucApplyMap = new HashMap<>();

    public static double getStamina(int point){
        //Bukkit.getLogger().info("get stamina with point "+point);
        if (point > vitApplyMap.size() || point == 0) {
            return 0;
        }
        return vitApplyMap.get(point).stamina;
    }

    public static double getHealth(int point){
        if (point > vitApplyMap.size() || point == 0) return 0;
        return vitApplyMap.get(point).health;
    }

    //////////////////
    // ここから倍率

    public static double getExpRate(int point){
        if (point > intApplyMap.size() || point == 0) return 1.0;
        return (100 + intApplyMap.get(point).exp) / 100;
    }

    public static double getAttackRate(int point){
        if (point > atkApplyMap.size() || point == 0) return 1.0;
        return (100 + atkApplyMap.get(point).weapon) / 100;
    }

    public static double getArtifactRate(int point){
        if (point > atkApplyMap.size() || point == 0) return 1.0;
        return (100 + atkApplyMap.get(point).artifact) / 100;
    }

    /**
     * 確率の小数として渡す
     */
    public static double getLuckyRate(int point){
        if (point > lucApplyMap.size() || point == 0) return 0.0;
        return (lucApplyMap.get(point).lucky) / 100.0;
    }


    /**
     * 乗算用（+15% → 1.15）
     */
    public static double getGatherRelicRate(int point){
        if (point > lucApplyMap.size() || point == 0) return 1.0;
        return (100 + lucApplyMap.get(point).gatherRelic) / 100;
    }

    /**
     * 乗算用（-25% → 0.75）
     */
    public static double getGatherMonsterRate(int point){
        if (point > intApplyMap.size() || point == 0) return 1.0;
        return (100 + intApplyMap.get(point).gatherMonster) / 100;
    }

}
