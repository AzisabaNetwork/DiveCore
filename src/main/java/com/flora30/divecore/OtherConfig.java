package com.flora30.divecore;

import com.flora30.divecore.base.BaseConfig;
import com.flora30.divecore.tools.Config;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class OtherConfig extends Config {
    private static File configFile;

    public OtherConfig(){
        configFile = new File(DiveCore.plugin.getDataFolder(),"config.yml");
    }

    @Override
    public void load() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        BaseConfig.overrideMode = config.getBoolean("baseOverride",false);
        /*
        Triggers.mechanicTick = specificLoadInt("Other",config,"mechanicTick",6);
        Triggers.levelTick = specificLoadInt("Other",config,"levelTick",2);
        Triggers.displayShowTick = specificLoadInt("Other",config,"displayShowTick",3);
        Triggers.displayScoreTick = specificLoadInt("Other",config,"displayScoreTick",10);
        Triggers.itemLimitTick = specificLoadInt("Other",config,"itemLimitTick",5);
        Triggers.npcParticleTick = specificLoadInt("Other",config,"npcParticleTick",7);
        Triggers.lootSendTick = specificLoadInt("Other",config,"lootSendTick",10);
        Triggers.baseGuardTick = specificLoadInt("Other",config,"baseGuardTick",10);
        Triggers.baseUpdateTick = specificLoadInt("Other",config,"baseUpdateTick",5);
        Triggers.compassTick = specificLoadInt("Other",config,"compassTick",5);
        Triggers.menuTick = specificLoadInt("Other",config,"menuTick",10);

        Triggers.moveTick = specificLoadInt("Other",config,"moveTick",4);

        Tutorial.helpId = specificLoadInt("Other",config,"helpId",0);
        Tutorial.ropeId = specificLoadInt("Other",config,"ropeId",0);
        Tutorial.pointTradeId = specificLoadInt("Other",config,"pointTradeId",0);
        Tutorial.pointLookId = specificLoadInt("Other",config,"pointLookId",0);
        Tutorial.baseId = specificLoadInt("Other",config,"baseId",0);
        Tutorial.craftId = specificLoadInt("Other",config,"craftId",0);

        //ItemE.releaseTick = specificLoadInt("Other",config,"releaseTick", 0);

         */
    }

    @Override
    public void save() {

    }
}
