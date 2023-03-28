package com.flora30.divecore.base;

import com.flora30.data.Base;
import com.flora30.data.BaseObject;
import com.flora30.divecore.DiveCore;
import com.flora30.divecore.tools.Config;
import data.BaseDataObject;
import data.BaseLayer;
import data.BaseLocation;
import data.BaseRequire;
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

                BaseLocation baseLocation = loadBaseLoc(file,id);
                if (baseLocation == null) continue;

                BaseDataObject.INSTANCE.getBaseLocationMap().put(id,baseLocation);
                Base base = new Base();
                BaseObject.INSTANCE.getBaseMap().put(id,base);
                count++;

                if (overrideMode) {
                    Location location = baseLocation.getLocation();
                    if (location.getBlock().getType() != Material.CAMPFIRE && location.getBlock().getType() != Material.SOUL_CAMPFIRE){
                        location.getBlock().setType(Material.CAMPFIRE);
                        ((Campfire)location.getBlock().getBlockData()).setFacing(baseLocation.getFace());
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

    public static BaseLocation loadBaseLoc(FileConfiguration config, int id){
        String line = config.getString(id+".location");
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

        return new BaseLocation(
                world.getBlockAt(x,y,z).getLocation(),
                face,
                loadOrDefault("Base",config,id+".isTown",false)
        );
    }

    public static void loadBaseData(String key, ConfigurationSection section){
        // 拠点の設定ymlにbase.1のセクションが書かれている時だけ読み込む
        if (!section.isList("base.1")){
            Bukkit.getLogger().info("[DiveCore-Base]拠点レベル情報の読み込みに失敗しました(エリア: "+key+")");
            return;
        }
        List<String> requireList = section.getStringList("base.1");
        BaseRequire require = loadRequire(requireList, key, 1);

        BaseLayer baseLayer = new BaseLayer();
        baseLayer.getLevelMap().put(1,require);

        BaseDataObject.INSTANCE.getBaseLayerMap().put(key,baseLayer);
    }

    public static BaseRequire loadRequire(List<String> stringList, String layer, int level){
        int require1Id = -1;
        int require1Amount = 0;
        int require2Id = -1;
        int require2Amount = 0;

        int count = 1;
        for (String str : stringList){
            String[] loaded = str.split(",");
            try{
                switch (count) {
                    case 1 -> {
                        require1Id = Integer.parseInt(loaded[0]);
                        require1Amount = Integer.parseInt(loaded[1]);
                        count++;
                    }
                    case 2 -> {
                        require2Id = Integer.parseInt(loaded[0]);
                        require2Amount = Integer.parseInt(loaded[1]);
                        count++;
                    }
                    default -> Bukkit.getLogger().info("[DiveCore-Base]必要アイテムの3つ目以降は読み込みません（"+layer+" - "+level+" - ["+str+"]）");
                }
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e){
                Bukkit.getLogger().info("[DiveCore-Base]必要アイテムのロードに失敗しました（"+layer+" - "+level+" - ["+str+"]）");
            }
        }

        return new BaseRequire(require1Id,require1Amount,require2Id,require2Amount);
    }

    public static void save(String layer, int id){
        BaseLocation baseLocation = BaseDataObject.INSTANCE.getBaseLocationMap().get(id);
        if (baseLocation == null){
            Bukkit.getLogger().info("[DiveCore-Base]保存失敗(Null - "+id+")");
            return;
        }

        for (File from : files) {
            if (!from.getName().replace(".yml","").equals(layer)){
                continue;
            }
            FileConfiguration file = YamlConfiguration.loadConfiguration(from);

            file.set(id+".isTown",baseLocation.isTown());
            file.set(id+".location",composeBaseLoc(baseLocation));
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

    private static String composeBaseLoc(BaseLocation baseLocation){
        Location loc = baseLocation.getLocation();
        return loc.getWorld().getName()+","+loc.getBlockX()+","+loc.getBlockY()+","+loc.getBlockZ()+","+baseLocation.getFace();
    }
}
