package com.flora30.divecore.data;

import com.flora30.diveapi.data.PlayerData;
import com.flora30.diveapi.data.player.LayerData;
import com.flora30.diveapi.data.player.LevelData;
import com.flora30.diveapi.data.player.NpcData;
import com.flora30.diveapi.plugins.CoreAPI;
import com.flora30.diveapi.plugins.RegionAPI;
import com.flora30.divecore.DiveCore;
import com.flora30.divecore.display.SideBar;
import com.flora30.divecore.level.LevelMain;
import com.flora30.divedb.DiveDBAPI;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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
                CoreAPI.adminSet.add(id);
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
                PlayerDataMain.putPlayerID(str,id);
            } catch (IllegalArgumentException e){
                Bukkit.getLogger().info("[DiveCore-Data]"+str+" がIDに変換できません");
            }
        }


        for(Player player : Bukkit.getServer().getOnlinePlayers()){
            load(player.getUniqueId());

            initialPlayPrepare(player);
        }
    }
    public void initialPlayPrepare(Player player){
        DiveCore.plugin.asyncTask(() -> {
            PlayerData data = PlayerDataMain.getPlayerData(player.getUniqueId());
            if(data == null){
                initialPlayPrepare(player);
                return;
            }
            LevelMain.setMaxHpSt(player);
            RegionAPI.layerCheck(player);
            data.currentST = data.maxST;
            data.food = 10;
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

    public void loadAsync(UUID uuid){
        // 処理時間を計測する
        long firstTime = System.currentTimeMillis();

        Player player = Bukkit.getPlayer(uuid);
        if(player == null){
            Bukkit.getLogger().info("[DiveCore-Data]UUID : "+ uuid +" からプレイヤーが取得できません");
            DiveCore.plugin.delayedTask(1, () -> load(uuid));
            return;
        }
        PlayerData data = new PlayerData();
        NpcData npcData = data.npcData;
        LayerData layerData = data.layerData;
        LevelData levelData = data.levelData;


        String table = "player_data";
        DiveDBAPI.insertSQL(table,uuid);
        DiveDBAPI.insertSQL("loots",uuid);


        levelData.exp = Integer.parseInt(DiveDBAPI.loadSQL(table,uuid,"Exp","0"));
        levelData.level = Integer.parseInt(DiveDBAPI.loadSQL(table,uuid,"Level","1"));
        levelData.whistleExp = Integer.parseInt(DiveDBAPI.loadSQL(table,uuid,"WhistleExp","0"));
        levelData.whistleRank = Integer.parseInt(DiveDBAPI.loadSQL(table,uuid,"WhistleRank","1"));
        levelData.rawPoint = Integer.parseInt(DiveDBAPI.loadSQL(table,uuid,"RawPoint","0"));
        levelData.pointLuc = Integer.parseInt(DiveDBAPI.loadSQL(table,uuid,"Luc","0"));
        levelData.pointInt = Integer.parseInt(DiveDBAPI.loadSQL(table,uuid,"IntP","0"));
        levelData.pointVit = Integer.parseInt(DiveDBAPI.loadSQL(table,uuid,"Vit","0"));
        levelData.pointAtk = Integer.parseInt(DiveDBAPI.loadSQL(table,uuid,"Atk","0"));

        layerData.storySpeed = Integer.parseInt(DiveDBAPI.loadSQL(table,uuid,"StorySpeed","40"));
        layerData.curse = Double.parseDouble(DiveDBAPI.loadSQL(table,uuid,"Curse","0"));
        layerData.lootLayer = DiveDBAPI.loadSQL(table,uuid,"LootLayer","no");
        layerData.layer = layerData.lootLayer;

        npcData.storyMissionId = Integer.parseInt(DiveDBAPI.loadSQL(table,uuid,"StoryMissionId","-1"));
        npcData.mobMissionId = Integer.parseInt(DiveDBAPI.loadSQL(table,uuid,"MobMissionId","-1"));
        npcData.itemMissionId = Integer.parseInt(DiveDBAPI.loadSQL(table,uuid,"ItemMissionId","-1"));
        npcData.talkDelay = Integer.parseInt(DiveDBAPI.loadSQL(table,uuid,"TalkDelay","20"));

        data.fuel = Integer.parseInt(DiveDBAPI.loadSQL(table,uuid,"Fuel","0"));
        data.baseId = Integer.parseInt(DiveDBAPI.loadSQL(table,uuid,"BaseId","-1"));
        data.money = Integer.parseInt(DiveDBAPI.loadSQL(table,uuid,"Money","0"));
        data.isFirstJoin = Integer.parseInt(DiveDBAPI.loadSQL(table,uuid,"IsFirstJoin","1")) == 1;
        if (data.isFirstJoin) {
            // 本来はPlayerConfigでやること、タイミングをFirstJoinイベントの前にするためにここ
            player.getInventory().clear();
        }
        Bukkit.getLogger().info("FirstJoin -> "+DiveDBAPI.loadSQL(table,uuid,"IsFirstJoin","true"));

        //map・set系はここ
        npcData.talkProgressMap = loadIntegerMap(DiveDBAPI.loadSQL(table,uuid,"TalkProgress","",false));
        loadLootMap(uuid,data);
        layerData.visitedLayers = loadStringSet(DiveDBAPI.loadSQL(table,uuid,"VisitedLayers","",false));
        data.helpIdSet = loadIntegerSet(DiveDBAPI.loadSQL(table,uuid,"Helps","",false));
        data.foundRecipeSet = loadIntegerSet(DiveDBAPI.loadSQL(table,uuid,"FoundRecipes","",false));
        data.completedRecipeSet = loadIntegerSet(DiveDBAPI.loadSQL(table,uuid,"CompletedRecipes","",false));

        data.food = Integer.parseInt(DiveDBAPI.loadSQL("player",uuid,"Food","10"));
        data.sideBarId = SideBar.getUniqueSideBarID();
        PlayerDataMain.setPlayerData(uuid,data);

        // 処理時間を計測する
        long lastTime = System.currentTimeMillis();
        long taskTime = (lastTime - firstTime);
        Bukkit.getLogger().info("[DiveCore-Data]プレイヤー"+ player.getDisplayName()+"のデータをロードしました（"+taskTime+"ms）");
    }

    private Map<Integer,Integer> loadIntegerMap(String from) {
        Map<Integer,Integer> map = new HashMap<>();
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

    private Set<Integer> loadIntegerSet(String from) {
        Set<Integer> set = new HashSet<>();
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

    private Set<String> loadStringSet(String from) {
        Set<String> set = new HashSet<>();
        if (from.equals("no")) {
            return set;
        }
        Collections.addAll(set, from.split("_"));
        return set;
    }

    private void loadLootMap(UUID uuid, PlayerData data) {
        String[] keys = {"Loots_1","Loots_2","Loots_3"};
        for (int lv = 1; lv <= 3; lv++) {
            Set<Integer> set = loadIntegerSet(DiveDBAPI.loadSQL("loots",uuid,keys[lv - 1],"",false));
            for (int id : set) {
                data.layerData.lootMap.put(id,lv);
            }
        }
    }


    public void saveAll(){
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        /////////////////////////////////////////////////
        List<String> adminList = new ArrayList<>();
        for (UUID id : CoreAPI.adminSet){
            adminList.add(id.toString());
        }
        config.set("admin",adminList);
        /////////////////////////////////////////////////

        for (String name : PlayerDataMain.getPlayerNameSet()){
            config.set("uuid."+name, PlayerDataMain.getPlayerID(name).toString());
        }
        try{
            config.save(configFile);
        } catch (IOException e){
            e.printStackTrace();
        }
        Bukkit.getLogger().info("[DiveCore-Data]adminリストをセーブしました");


        for (UUID id : PlayerDataMain.getAllPlayers()){
            save(id);
        }
        Bukkit.getLogger().info("[DiveCore-Data]全プレイヤーのデータを最終セーブしました");
    }

    public void save(UUID uuid){
        // 処理時間を計測する
        long firstTime = System.currentTimeMillis();

        PlayerData data = PlayerDataMain.getPlayerData(uuid);
        LevelData levelData = data.levelData;
        NpcData npcData = data.npcData;
        LayerData layerData = data.layerData;

        String table = "player_data";

        DiveDBAPI.insertSQL(table,uuid);

        DiveDBAPI.saveSQL(table,uuid,"Exp",String.valueOf(levelData.exp));
        DiveDBAPI.saveSQL(table,uuid,"Level",String.valueOf(levelData.level));
        DiveDBAPI.saveSQL(table,uuid,"WhistleExp",String.valueOf(levelData.whistleExp));
        DiveDBAPI.saveSQL(table,uuid,"WhistleRank",String.valueOf(levelData.whistleRank));
        DiveDBAPI.saveSQL(table,uuid,"RawPoint",String.valueOf(levelData.rawPoint));
        DiveDBAPI.saveSQL(table,uuid,"Luc",String.valueOf(levelData.pointLuc));
        DiveDBAPI.saveSQL(table,uuid,"IntP",String.valueOf(levelData.pointInt));
        DiveDBAPI.saveSQL(table,uuid,"Vit",String.valueOf(levelData.pointVit));
        DiveDBAPI.saveSQL(table,uuid,"Atk",String.valueOf(levelData.pointAtk));

        DiveDBAPI.saveSQL(table,uuid,"StorySpeed",String.valueOf(layerData.storySpeed));
        DiveDBAPI.saveSQL(table,uuid,"Curse",String.valueOf(layerData.curse));
        DiveDBAPI.saveSQL(table,uuid,"LootLayer","'"+layerData.lootLayer+"'");

        DiveDBAPI.saveSQL(table,uuid,"StoryMissionId",String.valueOf(npcData.storyMissionId));
        DiveDBAPI.saveSQL(table,uuid,"MobMissionId",String.valueOf(npcData.mobMissionId));
        DiveDBAPI.saveSQL(table,uuid,"ItemMissionId",String.valueOf(npcData.itemMissionId));
        DiveDBAPI.saveSQL(table,uuid,"TalkDelay",String.valueOf(npcData.talkDelay));

        DiveDBAPI.saveSQL(table,uuid,"Fuel",String.valueOf(data.fuel));
        DiveDBAPI.saveSQL(table,uuid,"BaseId",String.valueOf(data.baseId));
        DiveDBAPI.saveSQL(table,uuid,"IsFirstJoin",String.valueOf(false));
        DiveDBAPI.saveSQL(table,uuid,"Money",String.valueOf(data.money));

        //map・set系は別関数
        saveLootMap(uuid,layerData);
        DiveDBAPI.saveSQL(table,uuid,"TalkProgress",convIntegerMap(npcData.talkProgressMap),false);
        DiveDBAPI.saveSQL(table,uuid,"VisitedLayers",convStringSet(data.layerData.visitedLayers),false);
        DiveDBAPI.saveSQL(table,uuid,"Helps",convIntSet(data.helpIdSet),false);
        DiveDBAPI.saveSQL(table,uuid,"FoundRecipes",convIntSet(data.foundRecipeSet),false);
        DiveDBAPI.saveSQL(table,uuid,"CompletedRecipes",convIntSet(data.completedRecipeSet),false);

        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        // 処理時間を計測する
        long lastTime = System.currentTimeMillis();
        long taskTime = (lastTime - firstTime);
        Bukkit.getLogger().info("[DiveCore-Data]プレイヤー"+player.getName()+"のデータをセーブしました（"+taskTime+"ms）");

        // 次来た時に、ロードが完了するまで他の処理を止める
        PlayerDataMain.removePlayerData(uuid);
    }

    public void saveAsync(UUID uuid){
        DiveCore.plugin.asyncTask(() -> {
            // 処理時間を計測する
            long firstTime = System.currentTimeMillis();

            PlayerData data = PlayerDataMain.getPlayerData(uuid);
            LevelData levelData = data.levelData;
            NpcData npcData = data.npcData;
            LayerData layerData = data.layerData;

            String table = "player_data";

            DiveDBAPI.insertSQL(table,uuid);

            DiveDBAPI.saveSQL(table,uuid,"Exp",String.valueOf(levelData.exp));
            DiveDBAPI.saveSQL(table,uuid,"Level",String.valueOf(levelData.level));
            DiveDBAPI.saveSQL(table,uuid,"WhistleExp",String.valueOf(levelData.whistleExp));
            DiveDBAPI.saveSQL(table,uuid,"WhistleRank",String.valueOf(levelData.whistleRank));
            DiveDBAPI.saveSQL(table,uuid,"RawPoint",String.valueOf(levelData.rawPoint));
            DiveDBAPI.saveSQL(table,uuid,"Luc",String.valueOf(levelData.pointLuc));
            DiveDBAPI.saveSQL(table,uuid,"IntP",String.valueOf(levelData.pointInt));
            DiveDBAPI.saveSQL(table,uuid,"Vit",String.valueOf(levelData.pointVit));
            DiveDBAPI.saveSQL(table,uuid,"Atk",String.valueOf(levelData.pointAtk));

            DiveDBAPI.saveSQL(table,uuid,"StorySpeed",String.valueOf(layerData.storySpeed));
            DiveDBAPI.saveSQL(table,uuid,"Curse",String.valueOf(layerData.curse));
            DiveDBAPI.saveSQL(table,uuid,"LootLayer","'"+layerData.lootLayer+"'");

            DiveDBAPI.saveSQL(table,uuid,"StoryMissionId",String.valueOf(npcData.storyMissionId));
            DiveDBAPI.saveSQL(table,uuid,"MobMissionId",String.valueOf(npcData.mobMissionId));
            DiveDBAPI.saveSQL(table,uuid,"ItemMissionId",String.valueOf(npcData.itemMissionId));
            DiveDBAPI.saveSQL(table,uuid,"TalkDelay",String.valueOf(npcData.talkDelay));

            DiveDBAPI.saveSQL(table,uuid,"Fuel",String.valueOf(data.fuel));
            DiveDBAPI.saveSQL(table,uuid,"BaseId",String.valueOf(data.baseId));
            DiveDBAPI.saveSQL(table,uuid,"IsFirstJoin",String.valueOf(false));
            DiveDBAPI.saveSQL(table,uuid,"Money",String.valueOf(data.money));

            //map・set系は別関数
            saveLootMap(uuid,layerData);
            DiveDBAPI.saveSQL(table,uuid,"TalkProgress",convIntegerMap(npcData.talkProgressMap),false);
            DiveDBAPI.saveSQL(table,uuid,"VisitedLayers",convStringSet(data.layerData.visitedLayers),false);
            DiveDBAPI.saveSQL(table,uuid,"Helps",convIntSet(data.helpIdSet),false);
            DiveDBAPI.saveSQL(table,uuid,"FoundRecipes",convIntSet(data.foundRecipeSet),false);
            DiveDBAPI.saveSQL(table,uuid,"CompletedRecipes",convIntSet(data.completedRecipeSet),false);

            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

            // 処理時間を計測する
            long lastTime = System.currentTimeMillis();
            long taskTime = (lastTime - firstTime);
            Bukkit.getLogger().info("[DiveCore-Data]プレイヤー"+player.getName()+"のデータをセーブしました（"+taskTime+"ms）");

            // 次来た時に、ロードが完了するまで他の処理を止める
            PlayerDataMain.removePlayerData(uuid);
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

        Set<Integer> lootSet1 = new HashSet<>();
        Set<Integer> lootSet2 = new HashSet<>();
        Set<Integer> lootSet3 = new HashSet<>();

        for (int id : data.lootMap.keySet()) {
            switch (data.lootMap.get(id)) {
                case 1 -> lootSet1.add(id);
                case 2 -> lootSet2.add(id);
                case 3 -> lootSet3.add(id);
            }
        }

        DiveDBAPI.saveSQL("loots",uuid,"Loots_1",convIntSet(lootSet1),false);
        DiveDBAPI.saveSQL("loots",uuid,"Loots_2",convIntSet(lootSet2),false);
        DiveDBAPI.saveSQL("loots",uuid,"Loots_3",convIntSet(lootSet3),false);
    }

}
