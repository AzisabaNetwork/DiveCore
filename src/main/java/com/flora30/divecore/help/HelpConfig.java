package com.flora30.divecore.help;

import com.flora30.diveapin.event.HelpType;
import com.flora30.divecore.DiveCore;
import com.flora30.divecore.tools.Config;
import com.flora30.divenew.data.Help;
import com.flora30.divenew.data.HelpObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class HelpConfig extends Config {
    private static File[] helpFiles = new File[100];

    public HelpConfig(){
        folderCheck(DiveCore.plugin.getDataFolder().getAbsolutePath() + "/help");
        helpFiles = new File(DiveCore.plugin.getDataFolder().getAbsolutePath() + "/help").listFiles();
    }


    @Override
    public void load() {

        //npcフォルダ内のファイルを検索
        for (File separated : helpFiles) {
            FileConfiguration file2 = YamlConfiguration.loadConfiguration(separated);
            for (String key : file2.getKeys(false)) {
                int helpID;
                try {
                    helpID = Integer.parseInt(key);
                } catch (NumberFormatException e) {
                    Bukkit.getLogger().info("[DiveCore-Help]「" + key + "」は数字ではありません");
                    continue;
                }
                //読込み
                Material material = getMaterial(loadOrDefault("help",file2,key+".material",""));
                if (material == null){
                    Bukkit.getLogger().info("[DiveCore-Help]「" + key + "」のMaterialが間違っています");
                    continue;
                }

                HelpType trigger;

                try{
                    trigger = HelpType.valueOf(file2.getString(key+".trigger"));
                } catch (IllegalArgumentException|NullPointerException e) {
                    Bukkit.getLogger().info("[DiveCore-Help]条件の取得に失敗しました("+file2.getName()+", "+key+")");
                    continue;
                }


                String title;
                List<String> lore;

                try{
                    title = replaceColorCode(loadOrDefault("help", file2, key + ".title", ""));
                    lore = file2.getStringList(key + ".lore");
                    lore.replaceAll(this::replaceColorCode);
                } catch (IllegalArgumentException e){
                    Bukkit.getLogger().info("[DiveCore-Help]色の取得に失敗しました("+file2.getName()+", "+key+")");
                    continue;
                }

                Help help = new Help(trigger,material,title,lore);

                HelpObject.INSTANCE.getHelpMap().put(helpID,help);
                Bukkit.getLogger().info("[DiveCore-Help]「"+helpID+"」をロードしました");
            }
        }
        Bukkit.getLogger().info("[DiveCore-Help]ヘルプのロードが完了しました");
    }

    @Override
    public void save() {

    }

    private Material getMaterial(String str){
        return Material.getMaterial(str);
    }

    private String replaceColorCode(String str){
        if (str == null){
            return null;
        }
        return ChatColor.translateAlternateColorCodes('&',str);
    }
}
