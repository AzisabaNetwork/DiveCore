package com.flora30.divecore.data;

import com.flora30.divelib.data.gimmick.GimmickLog;
import com.flora30.divelib.data.gimmick.GimmickObject;
import com.flora30.divelib.data.gimmick.action.ChestType;
import com.flora30.divelib.data.player.*;
import com.flora30.divecore.DiveCore;
import com.flora30.divecore.display.SideBar;
import com.flora30.divecore.level.LevelMain;
import com.flora30.divedb.DiveDBAPI;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PlayerDataConfig {
    private static File configFile;

    public PlayerDataConfig(){
        configFile = new File(DiveCore.plugin.getDataFolder(),"config.yml");
    }

    public void loadAll(){
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        for(String str : config.getStringList("admin")){
            UUID id;
            try{
                id = UUID.fromString(str);
                PlayerDataObject.INSTANCE.getAdminSet().add(id);
            } catch (IllegalArgumentException e){
                Bukkit.getLogger().info("[DiveCore-Data]admin : "+str+" がIDに変換できません");
            }
        }
        ConfigurationSection section = config.getConfigurationSection("uuid");
        if (section == null){
            section = config.createSection("uuid");
        }
        for(String str : section.getKeys(false)){
            UUID id;
            try{
                String idStr = config.getString("uuid."+str);
                if (idStr == null){
                    continue;
                }
                id = UUID.fromString(idStr);
                PlayerDataObject.INSTANCE.getPlayerIdMap().put(str,id);
            } catch (IllegalArgumentException e){
                Bukkit.getLogger().info("[DiveCore-Data]"+str+" がIDに変換できません");
            }
        }


        for(Player player : Bukkit.getServer().getOnlinePlayers()){
            load(player.getUniqueId());
            initialPlayPrepare(player);
        }
    }

    // プレイヤーデータ読み込み完了後の準備
    public void initialPlayPrepare(Player player){
        DiveCore.plugin.asyncTask(() -> {
            PlayerData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId());
            if(data == null){
                initialPlayPrepare(player);
                return;
            }
            LevelMain.setMaxHpSt(player);
            data.setCurrentST(data.getMaxST());
            data.setFood(10);
        });
        player.setGameMode(GameMode.ADVENTURE);
    }

    public void load(UUID uuid){
        if(DiveCore.plugin.isEnabled()){
            DiveCore.plugin.asyncTask(() -> loadAsync(uuid));
        }
        else{
            loadAsync(uuid);
        }
    }

    // ロード可能な状態か確認する
    // 他のサーバーからセーブ中の場合はロードできない
    public boolean canLoad(UUID uuid) {
        String table = "player_data";
        DiveDBAPI.insertSQL(table,uuid);
        return Integer.parseInt(DiveDBAPI.loadSQL(table,uuid,"IsSaving","0")) == 0;
    }

    public void setSaving(UUID uuid, boolean isSaving){
        String table = "player_data";
        DiveDBAPI.insertSQL(table,uuid);
        DiveDBAPI.saveSQL(table,uuid,"IsSaving",String.valueOf(isSaving));
    }

    public void loadAsync(UUID uuid){
        // 処理時間を計測する
        long firstTime = System.currentTimeMillis();

        Player player = Bukkit.getPlayer(uuid);
        if(player == null){
            Bukkit.getLogger().info("[DiveCore-Data]UUID : "+ uuid +" からプレイヤーが取得できません");
            DiveCore.plugin.delayedTask(1, () -> load(uuid));
            return;
        }

        String table = "player_data";
        DiveDBAPI.insertSQL(table,uuid);
        DiveDBAPI.insertSQL("loots",uuid);
        DiveDBAPI.insertSQL("gimmicks",uuid);

        PlayerData data = new PlayerData(
                new LevelData(
                        Integer.parseInt(DiveDBAPI.loadSQL(table,uuid,"WhistleExp","0")),
                        Integer.parseInt(DiveDBAPI.loadSQL(table,uuid,"WhistleRank","1")),
                        Integer.parseInt(DiveDBAPI.loadSQL(table,uuid,"Exp","0")),
                        Integer.parseInt(DiveDBAPI.loadSQL(table,uuid,"Level","1")),
                        Integer.parseInt(DiveDBAPI.loadSQL(table,uuid,"RawPoint","0")),
                        Integer.parseInt(DiveDBAPI.loadSQL(table,uuid,"Luc","0")),
                        Integer.parseInt(DiveDBAPI.loadSQL(table,uuid,"IntP","0")),
                        Integer.parseInt(DiveDBAPI.loadSQL(table,uuid,"Vit","0")),
                        Integer.parseInt(DiveDBAPI.loadSQL(table,uuid,"Atk","0"))
                ),
                new NpcData(
                        Integer.parseInt(DiveDBAPI.loadSQL(table,uuid,"TalkDelay","20")),
                        Integer.parseInt(DiveDBAPI.loadSQL(table,uuid,"StoryMissionId","-1")),
                        Integer.parseInt(DiveDBAPI.loadSQL(table,uuid,"MobMissionId","-1")),
                        Integer.parseInt(DiveDBAPI.loadSQL(table,uuid,"ItemMissionId","-1")),
                        loadIntegerMap(DiveDBAPI.loadSQL(table,uuid,"TalkProgress","",false)),
                        false,
                        false
                ),
                new LayerData(
                        Integer.parseInt(DiveDBAPI.loadSQL(table,uuid,"StorySpeed","40")),
                        Double.parseDouble(DiveDBAPI.loadSQL(table,uuid,"Curse","0")),
                        DiveDBAPI.loadSQL(table,uuid,"LootLayer","no"),
                        loadLootMap(uuid),
                        loadStringSet(DiveDBAPI.loadSQL(table,uuid,"VisitedLayers","",false)),
                        loadGimmickLogSet(uuid),
                        DiveDBAPI.loadSQL(table,uuid,"LootLayer","no"),
                        null
                ),
                loadIntegerSet(DiveDBAPI.loadSQL(table,uuid,"Helps","",false)),
                loadIntegerSet(DiveDBAPI.loadSQL(table,uuid,"FoundRecipes","",false)),
                loadIntegerSet(DiveDBAPI.loadSQL(table,uuid,"CompletedRecipes","",false)),
                Integer.parseInt(DiveDBAPI.loadSQL(table,uuid,"IsFirstJoin","1")) == 1,
                Integer.parseInt(DiveDBAPI.loadSQL(table,uuid,"Money","0"))
        );

        if (data.isFirstJoin()) {
            // 本来はPlayerConfigでやること、タイミングをFirstJoinイベントの前にするためにここ
            player.getInventory().clear();
        }
        Bukkit.getLogger().info("FirstJoin -> "+DiveDBAPI.loadSQL(table,uuid,"IsFirstJoin","true"));

        data.setFood(Integer.parseInt(DiveDBAPI.loadSQL("player",uuid,"Food","10")));
        PlayerDataObject.INSTANCE.getPlayerDataMap().put(uuid,data);

        // 処理時間を計測する
        long lastTime = System.currentTimeMillis();
        long taskTime = (lastTime - firstTime);
        Bukkit.getLogger().info("[DiveCore-Data]プレイヤー"+ player.getDisplayName()+"のデータをロードしました（"+taskTime+"ms）");
    }

    private HashMap<Integer,Integer> loadIntegerMap(String from) {
        HashMap<Integer,Integer> map = new HashMap<>();
        if (from.equals("no")) {
            return map;
        }

        String[] arrayCombined = from.split("_");
        for (String str : arrayCombined) {
            String[] array = str.split("-");
            if (array.length == 2) {
                try {
                    int key = Integer.parseInt(array[0]);
                    int value = Integer.parseInt(array[1]);
                    map.put(key,value);
                } catch (NumberFormatException ignored){}
            }
        }

        return map;
    }

    private HashSet<Integer> loadIntegerSet(String from) {
        HashSet<Integer> set = new HashSet<>();
        if (from.equals("no")) {
            return set;
        }

        String[] array = from.split("_");
        for (String str : array) {
            try {
                int value = Integer.parseInt(str);
                set.add(value);
            } catch (NumberFormatException ignored){}
        }

        return set;
    }

    private HashSet<String> loadStringSet(String from) {
        HashSet<String> set = new HashSet<>();
        if (from.equals("no")) {
            return set;
        }
        Collections.addAll(set, from.split("_"));
        return set;
    }

    private HashMap<Location,LootData> loadLootMap(UUID uuid) {
        HashMap<Location,LootData> map = new HashMap<>();

        // 読み込む
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++){
            String load = DiveDBAPI.loadSQL("loots",uuid,"Loots_"+i,"no");

            if (!(load.equals("no"))) sb.append(load);
        }

        // _ 分割する
        String[] lootArray = sb.toString().split("_");
        for (String str : lootArray) {
            if (Objects.equals(str, "no")) continue;

            try {
                String[] array = str.split("[XYZTL]");
                String world = array[0];
                int x = Integer.parseInt(array[1]);
                int y = Integer.parseInt(array[2]);
                int z = Integer.parseInt(array[3]);
                ChestType type = ChestType.values()[Integer.parseInt(array[4])];
                int level = Integer.parseInt(array[5]);

                Location location = new Location(Bukkit.getWorld(world),x,y,z);
                LootData data = new LootData(type,level);
                map.put(location,data);
            } catch (NumberFormatException|ArrayIndexOutOfBoundsException ignored){}
        }

        return map;
    }

    private HashSet<GimmickLog> loadGimmickLogSet(UUID uuid){
        HashSet<GimmickLog> set = new HashSet<>();

        // 読み込む
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++){
            String load = DiveDBAPI.loadSQL("gimmicks",uuid,"Gimmicks_"+i,"no");

            if (!(load.equals("no"))) sb.append(load);
        }

        // _ 分割する
        String[] lootArray = sb.toString().split("_");
        for (String str : lootArray) {
            if (Objects.equals(str, "no")) continue;

            try {
                String[] array = str.split("[XYZG]");
                String world = array[0];
                int x = Integer.parseInt(array[1]);
                int y = Integer.parseInt(array[2]);
                int z = Integer.parseInt(array[3]);
                String gimmickID = GimmickObject.INSTANCE.getStringID(Integer.parseInt(array[4]));
                if(gimmickID == null) continue;

                Location location = new Location(Bukkit.getWorld(world),x,y,z);
                GimmickLog log = new GimmickLog(location,gimmickID,System.currentTimeMillis());
                set.add(log);
            } catch (NumberFormatException|ArrayIndexOutOfBoundsException ignored){}
        }

        return set;
    }


    public void saveConfig(){
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        /////////////////////////////////////////////////
        List<String> adminList = new ArrayList<>();
        for (UUID id : PlayerDataObject.INSTANCE.getAdminSet()){
            adminList.add(id.toString());
        }
        config.set("admin",adminList);
        /////////////////////////////////////////////////

        for (String name : PlayerDataObject.INSTANCE.getPlayerIdMap().keySet()){
            config.set("uuid."+name, PlayerDataObject.INSTANCE.getPlayerIdMap().get(name).toString());
        }
        try{
            config.save(configFile);
        } catch (IOException e){
            e.printStackTrace();
        }
        Bukkit.getLogger().info("[DiveCore-Data]adminリストをセーブしました");
    }

    public void save(UUID uuid){
        // 処理時間を計測する
        long firstTime = System.currentTimeMillis();

        PlayerData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(uuid);
        LevelData levelData = data.getLevelData();
        NpcData npcData = data.getNpcData();
        LayerData layerData = data.getLayerData();

        String table = "player_data";

        DiveDBAPI.insertSQL(table,uuid);

        DiveDBAPI.saveSQL(table,uuid,"Exp",String.valueOf(levelData.getExp()));
        DiveDBAPI.saveSQL(table,uuid,"Level",String.valueOf(levelData.getLevel()));
        DiveDBAPI.saveSQL(table,uuid,"WhistleExp",String.valueOf(levelData.getWhistleExp()));
        DiveDBAPI.saveSQL(table,uuid,"WhistleRank",String.valueOf(levelData.getWhistleRank()));
        DiveDBAPI.saveSQL(table,uuid,"RawPoint",String.valueOf(levelData.getRawPoint()));
        DiveDBAPI.saveSQL(table,uuid,"Luc",String.valueOf(levelData.getPointLuc()));
        DiveDBAPI.saveSQL(table,uuid,"IntP",String.valueOf(levelData.getPointInt()));
        DiveDBAPI.saveSQL(table,uuid,"Vit",String.valueOf(levelData.getPointVit()));
        DiveDBAPI.saveSQL(table,uuid,"Atk",String.valueOf(levelData.getPointAtk()));

        DiveDBAPI.saveSQL(table,uuid,"StorySpeed",String.valueOf(layerData.getStorySpeed()));
        DiveDBAPI.saveSQL(table,uuid,"Curse",String.valueOf(layerData.getCurse()));
        DiveDBAPI.saveSQL(table,uuid,"LootLayer","'"+layerData.getLootLayer()+"'");

        DiveDBAPI.saveSQL(table,uuid,"StoryMissionId",String.valueOf(npcData.getStoryMissionId()));
        DiveDBAPI.saveSQL(table,uuid,"MobMissionId",String.valueOf(npcData.getMobMissionId()));
        DiveDBAPI.saveSQL(table,uuid,"ItemMissionId",String.valueOf(npcData.getItemMissionId()));
        DiveDBAPI.saveSQL(table,uuid,"TalkDelay",String.valueOf(npcData.getTalkDelay()));

        DiveDBAPI.saveSQL(table,uuid,"IsFirstJoin",String.valueOf(false));
        DiveDBAPI.saveSQL(table,uuid,"Money",String.valueOf(data.getMoney()));

        //map・set系は別関数
        saveLootMap(uuid,layerData);
        DiveDBAPI.saveSQL(table,uuid,"TalkProgress",convIntegerMap(npcData.getTalkProgressMap()),false);
        DiveDBAPI.saveSQL(table,uuid,"VisitedLayers",convStringSet(layerData.getVisitedLayers()),false);
        DiveDBAPI.saveSQL(table,uuid,"Helps",convIntSet(data.getHelpIdSet()),false);
        DiveDBAPI.saveSQL(table,uuid,"FoundRecipes",convIntSet(data.getFoundRecipeSet()),false);
        DiveDBAPI.saveSQL(table,uuid,"CompletedRecipes",convIntSet(data.getCompletedRecipeSet()),false);

        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        // 処理時間を計測する
        long lastTime = System.currentTimeMillis();
        long taskTime = (lastTime - firstTime);
        Bukkit.getLogger().info("[DiveCore-Data]プレイヤー"+player.getName()+"のデータをセーブしました（"+taskTime+"ms）");

        // 次来た時に、ロードが完了するまで他の処理を止める
        PlayerDataObject.INSTANCE.getPlayerDataMap().remove(uuid);
    }

    public void saveAsync(UUID uuid){
        DiveCore.plugin.asyncTask(() -> {
            // 処理時間を計測する
            long firstTime = System.currentTimeMillis();


            PlayerData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(uuid);
            LevelData levelData = data.getLevelData();
            NpcData npcData = data.getNpcData();
            LayerData layerData = data.getLayerData();

            String table = "player_data";

            DiveDBAPI.insertSQL(table,uuid);

            DiveDBAPI.saveSQL(table,uuid,"Exp",String.valueOf(levelData.getExp()));
            DiveDBAPI.saveSQL(table,uuid,"Level",String.valueOf(levelData.getLevel()));
            DiveDBAPI.saveSQL(table,uuid,"WhistleExp",String.valueOf(levelData.getWhistleExp()));
            DiveDBAPI.saveSQL(table,uuid,"WhistleRank",String.valueOf(levelData.getWhistleRank()));
            DiveDBAPI.saveSQL(table,uuid,"RawPoint",String.valueOf(levelData.getRawPoint()));
            DiveDBAPI.saveSQL(table,uuid,"Luc",String.valueOf(levelData.getPointLuc()));
            DiveDBAPI.saveSQL(table,uuid,"IntP",String.valueOf(levelData.getPointInt()));
            DiveDBAPI.saveSQL(table,uuid,"Vit",String.valueOf(levelData.getPointVit()));
            DiveDBAPI.saveSQL(table,uuid,"Atk",String.valueOf(levelData.getPointAtk()));

            DiveDBAPI.saveSQL(table,uuid,"StorySpeed",String.valueOf(layerData.getStorySpeed()));
            DiveDBAPI.saveSQL(table,uuid,"Curse",String.valueOf(layerData.getCurse()));
            DiveDBAPI.saveSQL(table,uuid,"LootLayer","'"+layerData.getLootLayer()+"'");

            DiveDBAPI.saveSQL(table,uuid,"StoryMissionId",String.valueOf(npcData.getStoryMissionId()));
            DiveDBAPI.saveSQL(table,uuid,"MobMissionId",String.valueOf(npcData.getMobMissionId()));
            DiveDBAPI.saveSQL(table,uuid,"ItemMissionId",String.valueOf(npcData.getItemMissionId()));
            DiveDBAPI.saveSQL(table,uuid,"TalkDelay",String.valueOf(npcData.getTalkDelay()));

            DiveDBAPI.saveSQL(table,uuid,"IsFirstJoin",String.valueOf(false));
            DiveDBAPI.saveSQL(table,uuid,"Money",String.valueOf(data.getMoney()));

            //map・set系は別関数
            saveLootMap(uuid,layerData);
            saveGimmickMap(uuid,layerData);
            DiveDBAPI.saveSQL(table,uuid,"TalkProgress",convIntegerMap(npcData.getTalkProgressMap()),false);
            DiveDBAPI.saveSQL(table,uuid,"VisitedLayers",convStringSet(layerData.getVisitedLayers()),false);
            DiveDBAPI.saveSQL(table,uuid,"Helps",convIntSet(data.getHelpIdSet()),false);
            DiveDBAPI.saveSQL(table,uuid,"FoundRecipes",convIntSet(data.getFoundRecipeSet()),false);
            DiveDBAPI.saveSQL(table,uuid,"CompletedRecipes",convIntSet(data.getCompletedRecipeSet()),false);

            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

            // 処理時間を計測する
            long lastTime = System.currentTimeMillis();
            long taskTime = (lastTime - firstTime);
            Bukkit.getLogger().info("[DiveCore-Data]プレイヤー"+player.getName()+"のデータをセーブしました（"+taskTime+"ms）");

            // 次来た時に、ロードが完了するまで他の処理を止める
            PlayerDataObject.INSTANCE.getPlayerDataMap().remove(uuid);
        });
    }

    private String convIntegerMap(Map<Integer,Integer> map) {
        if (map.size() == 0) {
            return "'no'";
        }

        StringBuilder builder = new StringBuilder();
        boolean isAfter = false;
        builder.append("'");
        for (int i : map.keySet()) {
            if (isAfter) builder.append("_");
            builder.append(i);
            builder.append("-");
            builder.append(map.get(i));
            isAfter = true;
        }
        builder.append("'");

        return builder.toString();
    }

    private String convIntSet(Set<Integer> set) {
        if (set.size() == 0) {
            return "'no'";
        }
        StringBuilder builder = new StringBuilder();
        boolean isAfter = false;
        builder.append("'");
        for (int i : set) {
            if (isAfter) builder.append("_");
            builder.append(i);
            isAfter = true;
        }
        builder.append("'");

        return builder.toString();
    }

    private String convStringSet(Set<String> set) {
        if (set.size() == 0) {
            return "'no'";
        }
        StringBuilder builder = new StringBuilder();
        boolean isAfter = false;
        builder.append("'");
        for (String str : set) {
            if (isAfter) builder.append("_");
            builder.append(str);
            isAfter = true;
        }
        builder.append("'");

        return builder.toString();
    }

    private void saveLootMap(UUID uuid, LayerData data){

        // Location + LootData 保存
        // まず、すべてまとめて一つのStringにする
        StringBuilder sb = new StringBuilder();
        boolean isAfter = false;
        for (Map.Entry<Location, LootData> lootEntry : data.getLootMap().entrySet()) {
            Location loc = lootEntry.getKey();
            LootData lootData = lootEntry.getValue();

            if (isAfter) sb.append("_");
            sb.append(loc.getWorld().getName())
                    .append("X").append(loc.getBlockX())
                    .append("Y").append(loc.getBlockY())
                    .append("Z").append(loc.getBlockZ())
                    .append("T").append(lootData.getType().ordinal())
                    .append("L").append(lootData.getLevel());
            isAfter = true;
        }
        String str = sb.toString();

        // 分割する
        List<String> splitList = new ArrayList<>();
        for (int i = 0; i < StringUtils.length(str); i += 10000) {
            splitList.add(StringUtils.substring(str, i, i + 10000));
        }

        // 保存する
        for (int i = 0; i < 5; i++){

            // 何もない場合は'no'
            if (splitList.size() <= i) {
                DiveDBAPI.saveSQL("loots",uuid,"Loots_"+i,"'no'",false);
            }
            else{
                DiveDBAPI.saveSQL("loots",uuid,"Loots_"+i,"'"+ splitList.get(i) +"'",false);
            }

        }
    }

    private void saveGimmickMap(UUID uuid, LayerData data){

        // Location + GimmickLog 保存
        // まず、すべてまとめて一つのStringにする
        StringBuilder sb = new StringBuilder();
        boolean isAfter = false;
        for (GimmickLog log : data.getGimmickLogs()) {

            // 保存しないはずのデータは登録しない
            if(GimmickObject.INSTANCE.getIntID(log.getGimmickID()) == -1) continue;

            Location loc = log.getLocation();
            if (isAfter) sb.append("_");
            sb.append(loc.getWorld().getName())
                    .append("X").append(loc.getBlockX())
                    .append("Y").append(loc.getBlockY())
                    .append("Z").append(loc.getBlockZ())
                    .append("G").append(GimmickObject.INSTANCE.getIntID(log.getGimmickID()));
            isAfter = true;
        }
        String str = sb.toString();

        // 分割する
        List<String> splitList = new ArrayList<>();
        for (int i = 0; i < StringUtils.length(str); i += 10000) {
            splitList.add(StringUtils.substring(str, i, i + 10000));
        }

        // 保存する
        for (int i = 0; i < 5; i++){

            // 何もない場合は'no'
            if (splitList.size() <= i) {
                DiveDBAPI.saveSQL("gimmicks",uuid,"Gimmicks_"+i,"'no'",false);
            }
            else{
                DiveDBAPI.saveSQL("gimmicks",uuid,"Gimmicks_"+i,"'"+ splitList.get(i) +"'",false);
            }

        }
    }
}
