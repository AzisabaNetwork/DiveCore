package com.flora30.divecore.level;

import com.flora30.diveapin.data.player.LayerData;
import com.flora30.diveapin.data.player.LevelData;
import com.flora30.diveapin.data.player.PlayerData;
import com.flora30.diveapin.data.player.PlayerDataObject;
import com.flora30.divecore.DiveCore;
import com.flora30.divecore.data.PlayerDataMain;
import com.flora30.divecore.tools.Mathing;
import com.flora30.divenew.data.Layer;
import com.flora30.divenew.data.LayerObject;
import com.flora30.divenew.data.LevelObject;
import com.flora30.divenew.data.PointObject;
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

    // 使ってない
    //level | 減衰倍率
    //private static final Map<Integer,Double> gapRateMap = new HashMap<>();

    //最低値
    private static double leastGapRate = 0;

    public static void onLayerChange(Player player, String next){
        LayerData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId()).getLayerData();
        //訪れていたら終了
        if (data.getVisitedLayers().contains(next)){
            return;
        }

        Bukkit.getLogger().info(player.getDisplayName()+" エリア「"+next+"」を発見");
        Layer layer = LayerObject.INSTANCE.getLayerMap().get(next);
        player.sendMessage("新規エリアを発見！ ‣ "+layer.getExp());
        addExp(player,layer.getExp());
    }

    public static void onMobDeath(LivingEntity killer, MythicMob mobType){
        if (!(killer instanceof Player player)){
            return;
        }
        LevelData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId()).getLevelData();
        int playerLevel = data.getLevel();

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
        LevelData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId()).getLevelData();
        double rated = PointObject.INSTANCE.getExpRate(data.getPointInt()) * exp;
        data.setExp(data.getExp()+(int)rated);
        player.playSound(player.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
        levelCheck(player);
    }

    public static void levelCheck(Player player){
        LevelData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId()).getLevelData();
        int currentExp = data.getExp();
        int nextExp = LevelObject.INSTANCE.getNextExp(data.getLevel());
        if (data.getLevel() >= LevelObject.INSTANCE.getMaxLevel()){
            return;
        }
        if (currentExp >= nextExp){
            //レベルアップ
            data.setExp(currentExp - nextExp);
            data.setLevel(data.getLevel()+1);

            //エフェクト
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,1,1);
            player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
            data.setRawPoint(data.getRawPoint()+1);

            //さらなるレベルアップ判定
            addExp(player,0);
        }
    }

    public static void setMaxHpSt(Player player){
        PlayerData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId());

        if (data == null){
            //Bukkit.getLogger().info("[DiveCore-Level]データ待ち - "+player.getDisplayName());
            DiveCore.plugin.delayedTask(2, () -> {
                setMaxHpSt(player);
            });
            return;
        }
        double plusHp = PointObject.INSTANCE.getHealth(data.getLevelData().getPointVit());
        double plusSt = PointObject.INSTANCE.getStamina(data.getLevelData().getPointVit());
        AttributeInstance currentMax = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if(Objects.isNull(currentMax)){
            return;
        }

        // ログを出して検証
        Bukkit.getLogger().info("["+player.getDisplayName()+"-vit"+data.getLevelData().getPointVit()+"] set hp "+(20.0+plusHp)+" stamina "+(100.0 + plusSt));
        currentMax.setBaseValue(20.0 + plusHp);
        data.setMaxST((int) (100.0 + plusSt));
    }

    public static void display(Player player){
        if (PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId()) == null){
            return;
        }
        LevelData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId()).getLevelData();
        int currentLevel = data.getLevel();
        float currentExp = data.getExp();
        float nextExp = LevelObject.INSTANCE.getNextExp(currentLevel);

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

    public static void putGapRate(int gap, double rate){
        //gapRateMap.put(gap,rate);
    }

    public static void setLeastGapRate(double leastGapRate) {
        LevelMain.leastGapRate = leastGapRate;
    }
}
