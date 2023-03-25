package com.flora30.divecore;

import com.flora30.divecore.base.BaseConfig;
import com.flora30.divecore.data.PlayerDataConfig;
import com.flora30.divecore.help.HelpConfig;
import com.flora30.divecore.level.LevelConfig;

public class ConfigMain {
    private final LevelConfig levelLS = new LevelConfig();
    private final PlayerDataConfig playerDataConfig = new PlayerDataConfig();
    private final BaseConfig baseLS = new BaseConfig();
    private final OtherConfig otherLS = new OtherConfig();
    private final HelpConfig helpLS = new HelpConfig();

    public void loadAll(){
        otherLS.load();
        levelLS.load();
        baseLS.load();
        helpLS.load();
    }
}
