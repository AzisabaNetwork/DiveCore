package com.flora30.divecore.level;



import java.util.HashMap;
import java.util.Map;

public class Level {
    //レベル｜必要経験値
    private static final Map<Integer, Integer> expMap = new HashMap<>();

    public static int getNextExp(int level){
        return expMap.getOrDefault(level + 1, 999999);
    }

    public static int getMaxLevel(){
        return expMap.keySet().size()-1;
    }

    public static void putExpMap(int level, int exp){
        expMap.put(level,exp);
    }
}
