package com.flora30.divecore.tools;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;

public abstract class Config {

    //    Bukkit.getLogger().info(file.getName()+"はロード対象外です");
    public abstract void load();

    //    Bukkit.getLogger().info(file.getName()+"は更新対象外です");
    public abstract void save();

    public void fileCheck(File file){
        try {
            if(file.createNewFile()){
                Bukkit.getLogger().info(file.getName()+"を新規作成しました");
            }
            else{
                Bukkit.getLogger().info(file.getName()+"の存在を確認しました");
            }
        } catch (IOException e){
            Bukkit.getLogger().info(file.getName()+"の作成に失敗しました");
            e.printStackTrace();
        }
    }

    public void folderCheck(String path){
        File f = new File(path);
        try{
            if (f.mkdir()){
                Bukkit.getLogger().info(f.getName()+"を新規作成しました");
            }
            else{
                Bukkit.getLogger().info(f.getName()+"の存在を確認しました");
            }
        } catch (SecurityException e){
            Bukkit.getLogger().info(f.getName()+"の作成に失敗しました");
            e.printStackTrace();
        }
    }

    public void checkAndWrite(ConfigurationSection section, String path, Object item){
        if(!section.contains(path)){
            section.createSection(path);
        }
        section.set(path,item);
    }

    public int loadOrDefault(String pluginType, FileConfiguration file, String key, int def){
        if(file.isInt(key)){
            return file.getInt(key);
        }
        else{
            Bukkit.getLogger().info("[DiveCore-"+pluginType+"]"+key+"の読み込みに失敗したため、デフォルト値"+def+"を使います");
            return def;
        }
    }

    public int loadOrDefault(String pluginType, ConfigurationSection section, String key, int def){
        if(section != null && section.isInt(key)){
            return section.getInt(key);
        }
        else{
            Bukkit.getLogger().info("[DiveCore-"+pluginType+"]"+key+"の読み込みに失敗したため、デフォルト値"+def+"を使います");
            return def;
        }
    }

    public double loadOrDefault(String pluginType, FileConfiguration file, String key, double def){
        if(file != null && file.isDouble(key)){
            return file.getDouble(key);
        }
        else if(file != null && file.isInt(key)){
            return file.getInt(key);
        }
        else{
            Bukkit.getLogger().info("[DiveCore-"+pluginType+"]"+key+"の読み込みに失敗したため、デフォルト値"+def+"を使います");
            return def;
        }
    }

    public double loadOrDefault(String pluginType, ConfigurationSection file, String key, double def){
        if(file != null && file.isDouble(key)){
            return file.getDouble(key);
        }
        else if(file != null && file.isInt(key)){
            return file.getInt(key);
        }
        else{
            Bukkit.getLogger().info("[DiveCore-"+pluginType+"]"+key+"の読み込みに失敗したため、デフォルト値"+def+"を使います");
            return def;
        }
    }

    public String loadOrDefault(String pluginType, FileConfiguration file, String key, String def){
        if(file != null && file.isString(key)){
            return file.getString(key);
        }
        else{
            Bukkit.getLogger().info("[DiveCore-"+pluginType+"]"+key+"の読み込みに失敗したため、デフォルト値"+def+"を使います");
            return def;
        }
    }

    public static boolean loadOrDefault(String pluginType, FileConfiguration file, String key, Boolean def){
        if (file != null && file.isBoolean(key)){
            return file.getBoolean(key);
        }
        else{
            Bukkit.getLogger().info("[DiveCore-"+pluginType+"]"+key+"の読み込みに失敗したため、デフォルト値"+def+"を使います");
            return def;
        }
    }
}
