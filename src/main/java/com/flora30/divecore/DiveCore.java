package com.flora30.divecore;

import com.flora30.divelib.Ready;
import com.flora30.divecore.tools.ClearTrigger;
import com.flora30.divecore.display.SideBar;
import com.flora30.divecore.mechanic.Light;
import com.flora30.divecore.menu.MenuMain;
import com.flora30.divecore.data.PlayerDataConfig;
import com.flora30.divecore.data.PlayerConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.concurrent.atomic.AtomicInteger;

public final class DiveCore extends JavaPlugin {

    public static FileConfiguration config = null;
    public static DiveCore plugin;
    public static PluginManager pluginManager;
    final AtomicInteger count = new AtomicInteger();
    final BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
    private final Listeners listeners = new Listeners();
    public static boolean disabling = false;

    // 毎tick更新される現在時間（ms単位、ラグ検出用）
    public static long lagTime = 0;


    @Override
    public void onEnable() {
        // Plugin startup logic

        saveDefaultConfig();
        //デフォルトのconfigを作成

        config = getConfig();
        //FileConfiguration型変数configにデフォルトを代入

        plugin = this;
        //DiveCore型変数instanceにこの場所を設定
        pluginManager = plugin.getServer().getPluginManager();

        MenuMain.createIcon();

        getCommand("level").setExecutor(listeners);
        getCommand("cook").setExecutor(listeners);
        getCommand("adm").setExecutor(listeners);
        getCommand("death").setExecutor(listeners);
        //command: ga　でmain起動

        getServer().getPluginManager().registerEvents(listeners, this);
        getServer().getPluginManager().registerEvents(new ClearTrigger(),this);
        Ready.INSTANCE.setCoreEventReady(true);

        //セーブ系Mainで全機能のロードを行う
        ConfigMain configMain = new ConfigMain();
        configMain.loadAll();

        // ItemStack(ItemAPI) -> playerConfig
        PlayerConfig playerConfig = new PlayerConfig();
        playerConfig.loadAll();
        PlayerDataConfig playerDataConfig = new PlayerDataConfig();
        playerDataConfig.loadAll();

        SideBar.load();

        // AFKDetector用
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        //Timer起動
        onTimer();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        disabling = true;

        Light.lightStop();

        PlayerConfig playerConfig = new PlayerConfig();
        playerConfig.saveAll();

        PlayerDataConfig playerDataConfig = new PlayerDataConfig();
        playerDataConfig.saveAll();

    }


    private void onTimer(){
        int time = 1;
        if(count.intValue() == 0){
            Bukkit.getLogger().info("Timer Started");
        }

        scheduler.scheduleSyncDelayedTask(this, () -> {
            count.getAndIncrement();
            onTimer();
            //ここでやりたいことを入れる
            lagTime = System.currentTimeMillis();
            listeners.onTimer();
        }, time);
    }

    public void delayedTask(int delay,Runnable task){
        scheduler.scheduleSyncDelayedTask(this,task,delay);
    }

    public void asyncTask(Runnable task){
        scheduler.runTaskAsynchronously(this,task);
    }

    public void syncTask(Runnable task) {
        scheduler.scheduleSyncDelayedTask(this,task);
    }
}
