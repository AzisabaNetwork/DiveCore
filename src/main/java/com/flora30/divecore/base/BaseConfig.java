package com.flora30.divecore.base;

import com.flora30.divecore.DiveCore;
import com.flora30.divecore.tools.Config;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class BaseConfig extends Config {
    public static boolean overrideMode = false;
    private static File[] files = new File[100];
    public BaseConfig(){
        folderCheck(DiveCore.plugin.getDataFolder().getAbsolutePath() +"/base");
        files = new File(DiveCore.plugin.getDataFolder().getAbsolutePath() +"/base").listFiles();
    }

    @Override
    public void load() {
        //時間経過の計測用（時間かかるので）
        long time = System.currentTimeMillis();
        //アイテム数記録用
        int count = 0;
        //ファイルでループ
        Bukkit.getLogger().info("[DiveCore-Base]拠点情報の読み込みを開始します...");
        for (File from : files) {
            FileConfiguration file = YamlConfiguration.loadConfiguration(from);
            //中にあるアイテムIDでループ
            for (String i : file.getKeys(false)) {
                //IDの単位が違ったら除外
                int id;
                try {
                    id = Integer.parseInt(i);
                } catch (NumberFormatException e) {
                    Bukkit.getLogger().info("[DiveCore-Base]ID-" + i + "の取得に失敗しました");
                    continue;
                }

                Base base = new Base();
                base = (readBaseLoc(base, file.getString(i+".location")));
                if (base == null){
                    continue;
                }
                base.setTown(loadOrDefault("Base",file,i+".isTown",false));

                BaseMain.baseMap.put(id,base);
                count++;

                if (overrideMode) {
                    Location location = base.getLocation();
                    if (location.getBlock().getType() != Material.CAMPFIRE && location.getBlock().getType() != Material.SOUL_CAMPFIRE){
                        location.getBlock().setType(Material.CAMPFIRE);
                        ((Campfire)location.getBlock().getBlockData()).setFacing(base.getFace());
                    }
                }
                base.setPrepared(true);

                if (System.currentTimeMillis() > time + 1000L) {
                    Bukkit.getLogger().info("[DiveCore-Base]拠点情報の読み込み中...("+count+")");
                    time = System.currentTimeMillis();
                }
            }
        }

        Bukkit.getLogger().info("[DiveCore-Base]拠点情報の読み込みが完了しました("+count+")");
    }

    @Override
    public void save() {

    }

    public static Base readBaseLoc(Base base, String line){
        if (line == null){
            return null;
        }
        String[] split = line.split(",");
        int x,y,z;
        BlockFace face;
        try{
            x=Integer.parseInt(split[1]);
            y=Integer.parseInt(split[2]);
            z=Integer.parseInt(split[3]);
            face=BlockFace.valueOf(split[4]);
        }catch (ArrayIndexOutOfBoundsException |IllegalArgumentException e){
            Bukkit.getLogger().info("[DiveCore-Base]line["+line+"]の読み取りに失敗しました");
            return null;
        }
        World world = Bukkit.getWorld(split[0]);
        if (world == null){
            Bukkit.getLogger().info("[DiveCore-Base]ワールド["+split[0]+"]の読み取りに失敗しました");
            return null;
        }

        base.setLocation(world.getBlockAt(x,y,z).getLocation());
        base.setFace(face);

        return base;
    }

    public static void loadBaseData(String key, ConfigurationSection section){
        if (!section.isList("base.1")){
            Bukkit.getLogger().info("[DiveCore-Base]拠点レベル情報の読み込みに失敗しました(エリア: "+key+")");
            return;
        }
        List<String> requireList = section.getStringList("base.1");
        BaseRequire requireFirst = loadRequire(requireList, key, 1);
        if (requireFirst.getRequireMap().isEmpty()){
            Bukkit.getLogger().info("[DiveCore-Base]拠点レベル情報の読み込みに失敗しました(エリア: "+key+")");
            return;
        }

        BaseData data = new BaseData();
        data.getLevelMap().put(1,requireFirst);

        for (int i = 2; i <= 4;i++){
            BaseRequire require = loadRequire(section.getStringList("base."+i), key, i);

            if (require.getRequireMap().isEmpty()){
                data.getLevelMap().put(i,requireFirst);
            }
            else{
                data.getLevelMap().put(i,require);
            }
        }

        BaseMain.baseDataMap.put(key,data);
    }

    public static BaseRequire loadRequire(List<String> stringList, String layer, int level){
        BaseRequire require = new BaseRequire();
        for (String str : stringList){
            String[] loaded = str.split(",");
            try{
                int id = Integer.parseInt(loaded[0]);
                int amount = Integer.parseInt(loaded[1]);
                require.getRequireMap().put(id,amount);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e){
                Bukkit.getLogger().info("[DiveCore-Base]必要アイテムのロードに失敗しました（"+layer+" - "+level+" - ["+str+"]）");
            }
        }
        return require;
    }

    public static void save(String layer, int id){
        Base base = BaseMain.baseMap.get(id);
        if (base == null){
            Bukkit.getLogger().info("[DiveCore-Base]保存失敗(Null - "+id+")");
            return;
        }
        for (File from : files) {
            if (!from.getName().replace(".yml","").equals(layer)){
                continue;
            }
            FileConfiguration file = YamlConfiguration.loadConfiguration(from);

            file.set(id+".isTown",base.isTown());
            file.set(id+".location",composeBaseLoc(base));
            try{
                file.save(from);
                Bukkit.getLogger().info("[DiveCore-Base]保存成功("+id+")");
            } catch (IOException e){
                Bukkit.getLogger().info("[DiveCore-Base]保存失敗("+id+")");
            }
            return;
        }
        Bukkit.getLogger().info("[DiveCore-Base]保存失敗(ファイルなし - "+layer+","+id+")");
    }

    public static void remove(int targetId){
        for (File from : files) {
            FileConfiguration file = YamlConfiguration.loadConfiguration(from);
            //中にあるアイテムIDでループ
            for (String i : file.getKeys(false)) {
                //IDの単位が違ったら除外
                int id;
                try {
                    id = Integer.parseInt(i);
                } catch (NumberFormatException e) {
                    continue;
                }

                if (id == targetId){
                    file.set(""+targetId, null);
                    try{
                        file.save(from);
                        Bukkit.getLogger().info("[DiveCore-Base]削除成功("+targetId+")");
                    } catch (IOException e){
                        Bukkit.getLogger().info("[DiveCore-Base]削除失敗("+targetId+")");
                    }
                    return;
                }
            }
        }
        Bukkit.getLogger().info("[DiveCore-Base]削除に失敗しました(検索結果なし - "+targetId+")");
    }

    private static String composeBaseLoc(Base base){
        Location loc = base.getLocation();
        return loc.getWorld().getName()+","+loc.getBlockX()+","+loc.getBlockY()+","+loc.getBlockZ()+","+base.getFace();
    }
}
