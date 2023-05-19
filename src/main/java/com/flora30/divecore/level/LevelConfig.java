package com.flora30.divecore.level;

import com.flora30.divecore.DiveCore;
import com.flora30.divecore.tools.Config;
import com.flora30.divenew.data.LevelObject;
import com.flora30.divenew.data.Point;
import com.flora30.divenew.data.PointObject;
import com.flora30.divenew.data.PointType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Map;

public class LevelConfig extends Config {
    private final File file;

    public LevelConfig(){
        file = new File(DiveCore.plugin.getDataFolder(),File.separator+ "level.yml");
    }

    //ファイルを作成・読み取りする機能
    //プレイヤーデータではないので内部更新はなし

    @Override
    public void load(){
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        ConfigurationSection sec1 = config.getConfigurationSection("Level");
        if (sec1 == null){
            config.createSection("Level");
            sec1 = config.getConfigurationSection("Level");
        }
        //読み込み
        assert sec1 != null;
        double first = loadOrDefault("Level",sec1,"FirstExp",100.0);
        double rate = loadOrDefault("Level",sec1,"IncreaseRate",1.08);
        double plus = loadOrDefault("Level",sec1,"IncreasePlus",47);
        int limit = loadOrDefault("Level",sec1,"Limit",100);

        //自動計算
        calcLvMap(first,rate,plus,limit);

        //レベル差減衰
        /*
        if (config.isList("GapRate")){
            List<String> list = config.getStringList("GapRate");
            double last = 0;
            for (int i = 0; i < list.size(); i++){
                String str = list.get(i);
                double gapRate;
                try{
                    gapRate = Double.parseDouble(str);
                } catch (NullPointerException|NumberFormatException e){
                    gapRate = last;
                }
                LevelMain.putGapRate(i,gapRate);
                last = gapRate;
            }
            LevelMain.setLeastGapRate(last);
        }
         */


        //ポイント関連
        ConfigurationSection secPoint = config.getConfigurationSection("Points");
        if (secPoint == null){
            config.createSection("Points");
            secPoint = config.getConfigurationSection("Points");
            assert secPoint != null;
        }

        //基礎情報を読み込み
        createPointDataMap(PointType.Int, secPoint.getConfigurationSection("Int"));
        createPointDataMap(PointType.Vit, secPoint.getConfigurationSection("Vit"));
        createPointDataMap(PointType.Atk, secPoint.getConfigurationSection("Atk"));
        createPointDataMap(PointType.Luc, secPoint.getConfigurationSection("Luc"));

        Bukkit.getLogger().info("[DiveCore-Level]レベルの読み込みが完了しました");
    }

    @Override
    public void save() {

    }

    private void calcLvMap(double firstExp, double increaseRate, double plus, int limit){
        double current = firstExp;
        LevelObject.INSTANCE.getExpMap().put(1, (int) firstExp);
        for (int i = 2; i <= limit; i++){
            current = current * increaseRate + plus;
            LevelObject.INSTANCE.getExpMap().put(i, (int) current);
        }
    }

    /**
     * 各ポイントのレベル別データを取得する
     */
    private void createPointDataMap(PointType type, ConfigurationSection secType) {
        if (secType == null) return;

        int maxLv = 0;
        Map<Integer, Point> pointMap;
        Map<Integer, Point> applyMap;
        switch (type) {
            case Int -> {
                pointMap = PointObject.INSTANCE.getIntMap();
                applyMap = PointObject.INSTANCE.getIntApplyMap();
            }
            case Vit -> {
                pointMap = PointObject.INSTANCE.getVitMap();
                applyMap = PointObject.INSTANCE.getVitApplyMap();
            }
            case Atk -> {
                pointMap = PointObject.INSTANCE.getAtkMap();
                applyMap = PointObject.INSTANCE.getAtkApplyMap();
            }
            case Luc -> {
                pointMap = PointObject.INSTANCE.getLucMap();
                applyMap = PointObject.INSTANCE.getLucApplyMap();
            }
            default -> {
                return;
            }
        }


        for (String keyLv : secType.getKeys(false)) {
            ConfigurationSection secLv = secType.getConfigurationSection(keyLv);
            assert secLv != null;

            // ステータスポイントを数値で取得する
            int lv;
            try{
                lv = Integer.parseInt(keyLv);
            } catch (NumberFormatException e) {
                Bukkit.getLogger().info("[DiveCore-Level] ステータス情報のポイント "+keyLv+" は数値ではありません");
                continue;
            }

            // ポイント取得ごとに増加する効果
            Point point = new Point(
                    secLv.getInt("Stamina"),
                    secLv.getInt("Health"),
                    secLv.getInt("Exp"),
                    secLv.getInt("Weapon"),
                    secLv.getInt("Artifact"),
                    secLv.getInt("Lucky"),
                    secLv.getInt("GatherMonster"),
                    secLv.getInt("GatherRelic")
            );
            pointMap.put(lv, point);

            maxLv = Math.max(maxLv,lv);
        }

        // ポイント適用時の効果マップも作成
        calcPointApplyMap(pointMap, applyMap, maxLv);
    }


    /**
     * pointMapを元にして「適用時のデータ」を作成する
     */
    private void calcPointApplyMap(Map<Integer,Point> pointMap, Map<Integer,Point> applyMap, int max){

        int stamina = 0;
        int health = 0;
        int exp = 0;
        int weapon = 0;
        int artifact = 0;
        int lucky = 0;
        int gatherMonster = 0;
        int gatherRelic = 0;


        // ポイント増加で獲得できるものを加える
        for (int p = 1; p <= max; p++){
            Point point = pointMap.get(p);

            if (point != null) {
                stamina += point.getStamina();
                health += point.getHealth();
                exp += point.getExp();
                weapon += point.getWeapon();
                artifact += point.getArtifact();
                lucky += point.getLucky();
                gatherMonster += point.getGatherMonster();
                gatherRelic += point.getGatherRelic();
            }

            // ここまでの合計を記録
            Point total = new Point(
                    stamina,
                    health,
                    exp,
                    weapon,
                    artifact,
                    lucky,
                    gatherMonster,
                    gatherRelic
            );
            applyMap.put(p,total);
        }

    }
}
