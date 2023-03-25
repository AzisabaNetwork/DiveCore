package com.flora30.divecore.level;

import com.flora30.diveapi.data.PlayerData;
import com.flora30.diveapi.data.player.LayerData;
import com.flora30.diveapi.data.player.LevelData;
import com.flora30.divecore.DiveCore;
import com.flora30.divecore.data.PlayerDataMain;
import com.flora30.divecore.tools.Mathing;
import io.lumine.xikage.mythicmobs.io.MythicConfig;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LevelMain {
    //Layer | 経験値
    private static final Map<String,Integer> layerExpMap = new HashMap<>();
    //level | 減衰倍率
    private static final Map<Integer,Double> gapRateMap = new HashMap<>();
    //最低値
    private static double leastGapRate = 0;

    public static void onLayerChange(Player player, String next){
        LayerData data = PlayerDataMain.getPlayerData(player.getUniqueId()).layerData;
        //訪れていたら終了
        if (data.visitedLayers.contains(next)){
            return;
        }

        Bukkit.getLogger().info(player.getDisplayName()+"エリア「"+next+"」を発見");
        player.sendMessage("新規エリアを発見！ ‣ "+layerExpMap.get(next));
        addExp(player,layerExpMap.get(next));
    }

    public static void onMobDeath(LivingEntity killer, MythicMob mobType){
        if (!(killer instanceof Player)){
            return;
        }
        Player player = (Player) killer;
        LevelData data = PlayerDataMain.getPlayerData(player.getUniqueId()).levelData;
        int playerLevel = data.level;

        MythicConfig mythicConfig = mobType.getConfig();
        if (mythicConfig == null){
            return;
        }
        String str = mythicConfig.getString("Exp","0");
        String str2 = mythicConfig.getString("Level","0");
        int exp = Mathing.getInt(str);
        int mobLevel = Mathing.getInt(str2);
        //exp = applyLevelGap(playerLevel,mobLevel,exp);
        addExp(player,exp);
    }

    /*
    public static int applyLevelGap(int playerLevel,int recLevel, int exp){
        int gap = playerLevel - recLevel;
        Bukkit.getLogger().info("gap判定 | "+gap);
        if (gapRateMap.containsKey(gap)){
            exp = (int) (gapRateMap.get(gap) * exp);
        }
        else if(gap > 0){
            exp = (int) (exp * leastGapRate);
        }
        return exp;
    }

     */


    public static void addExp(Player player, int exp){
        LevelData data = PlayerDataMain.getPlayerData(player.getUniqueId()).levelData;
        double rated = Point.getExpRate(data.pointInt) * exp;
        data.addExp((int) rated);
        player.playSound(player.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
        levelCheck(player);
    }

    public static void levelCheck(Player player){
        LevelData data = PlayerDataMain.getPlayerData(player.getUniqueId()).levelData;
        int currentExp = data.exp;
        int nextExp = Level.getNextExp(data.level);
        if (data.level >= Level.getMaxLevel()){
            return;
        }
        if (currentExp >= nextExp){
            //レベルアップ
            data.exp = (currentExp - nextExp);
            data.level++;

            //エフェクト
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,1,1);
            player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
            data.rawPoint++;

            //さらなるレベルアップ判定
            addExp(player,0);
        }
    }

    public static void setMaxHpSt(Player player){
        PlayerData data = PlayerDataMain.getPlayerData(player.getUniqueId());

        if (data == null){
            //Bukkit.getLogger().info("[DiveCore-Level]データ待ち - "+player.getDisplayName());
            DiveCore.plugin.delayedTask(2, () -> {
                setMaxHpSt(player);
            });
            return;
        }
        double plusHp = Point.getHealth(data.levelData.pointVit);
        double plusSt = Point.getStamina(data.levelData.pointVit);
        AttributeInstance currentMax = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if(Objects.isNull(currentMax)){
            return;
        }

        // ログを出して検証
        Bukkit.getLogger().info("["+player.getDisplayName()+"-vit"+data.levelData.pointVit+"] set hp "+(20.0+plusHp)+" stamina "+(100.0 + plusSt));
        currentMax.setBaseValue(20.0 + plusHp);
        data.maxST = (int) (100.0 + plusSt);
    }

    public static void display(Player player){
        if (PlayerDataMain.getPlayerData(player.getUniqueId()) == null){
            return;
        }
        LevelData data = PlayerDataMain.getPlayerData(player.getUniqueId()).levelData;
        int currentLevel = data.level;
        float currentExp = data.exp;
        float nextExp = Level.getNextExp(currentLevel);

        float progress = currentExp / nextExp;
        if (progress < 0){
            progress = 0;
        }
        else if(progress > 1){
            progress = 1;
        }
        player.setLevel(currentLevel);
        player.setExp(progress);
    }

    public static void putLayerExp(String layerID, int exp){
        layerExpMap.put(layerID,exp);
    }

    public static void putGapRate(int gap, double rate){
        //gapRateMap.put(gap,rate);
    }

    public static void setLeastGapRate(double leastGapRate) {
        LevelMain.leastGapRate = leastGapRate;
    }
}
