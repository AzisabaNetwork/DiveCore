package com.flora30.divecore;

import com.flora30.diveapi.event.HelpEvent;
import com.flora30.diveapi.event.LayerChangeEvent;
import com.flora30.diveapi.event.LayerLoadEvent;
import com.flora30.divecore.api.event.RegisterSideBarEvent;
import com.flora30.divecore.base.BaseTrigger;
import com.flora30.divecore.base.gui.trigger.BaseGUITrigger;
import com.flora30.divecore.base.gui.trigger.BaseUpgradeGUITrigger;
import com.flora30.divecore.data.DataTrigger;
import com.flora30.divecore.display.DisplayTrigger;
import com.flora30.divecore.help.HelpGUI;
import com.flora30.divecore.help.HelpMain;
import com.flora30.divecore.level.gui.trigger.SetPointGUITrigger;
import com.flora30.divecore.level.gui.trigger.StatusGUITrigger;
import com.flora30.divecore.mechanic.MechanicTrigger;
import com.flora30.divecore.menu.DeathGUI;
import com.flora30.divecore.menu.MenuGUITrigger;
import com.flora30.divecore.menu.MenuTrigger;
import com.flora30.divecore.tools.AFKDetector;
import com.flora30.divecore.tools.deprecated.Slide;
import com.flora30.divecore.level.LevelTrigger;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Objects;

public class Listeners implements Listener, CommandExecutor {
    //final Slide slide = new Slide();

    public static void callEvent(Event event){
        Bukkit.getPluginManager().callEvent(event);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            //コマンドの実行者がプレイヤーだった時

            Player player = (Player) sender;
            //Player型変数playerに今の実行者を代入する

            String subCommand = args.length == 0 ? "" : args[0];
            String[] subCommands = new String[10];
            for (int i = 1; i <= 10; i++) {
                try{
                    subCommands[i-1] = args[i];
                } catch (IllegalArgumentException| ArrayIndexOutOfBoundsException | NullPointerException e){
                    subCommands[i-1] = "";
                }
            }
            //subCommandに引数を入れる（null対応）

            switch (command.getName()) {
                case "level" -> {
                    LevelTrigger.onCommand(player, subCommand, subCommands[0]);
                    return true;
                }
                case "adm" -> {
                    DataTrigger.onCommand(subCommand, subCommands[0]);
                    return true;
                }
                case "death" -> {
                    player.damage(200);
                    return true;
                }
            }
        }
        if (sender instanceof ConsoleCommandSender){
            String subCommand = args.length == 0 ? "" : args[0];
            String[] subCommands = new String[10];
            for (int i = 1; i <= 10; i++) {
                try{
                    subCommands[i-1] = args[i];
                } catch (IllegalArgumentException| ArrayIndexOutOfBoundsException | NullPointerException e){
                    subCommands[i-1] = "";
                }
            }
            //subCommandに引数を入れる（null対応）

            switch (command.getName()) {
                case "adm":
                    DataTrigger.onCommand(subCommand,subCommands[0]);
                    return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        LevelTrigger.onInteract(e);
        switch(e.getAction()){
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
                return;
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                MenuTrigger.onInteract(e);
                if(Objects.isNull(e.getClickedBlock())){
                    return;
                }
                switch(e.getClickedBlock().getType()){
                    case CAMPFIRE:
                    case SOUL_CAMPFIRE:
                        BaseTrigger.onRightClick(e);
                        return;
                    default:
                }
            default:
        }
    }
    @EventHandler
    public void onHelp(HelpEvent e) {
        HelpMain.onHelpTrigger(e);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e){
        BaseTrigger.onPlace(e);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e){
        BaseTrigger.onBreak(e);
    }

    @EventHandler
    public void onAnimation(PlayerAnimationEvent e){
        MenuTrigger.onAnimation(e);
    }

    @EventHandler
    public void onRegain(EntityRegainHealthEvent e) {
        if (e.getEntityType() != EntityType.PLAYER) return;

        // 満腹度のHP自動回復を無効化
        if (e.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        LevelTrigger.onInventoryClick(e);
        MenuTrigger.onClick(e);
        //キャンセル判定
        if(e.isCancelled()){
            //Bukkit.getLogger().info("[DiveCore-InventoryClick]cancelled");
            return;
        }
        switch (e.getView().getTitle()) {
            case "ステータス確認" -> StatusGUITrigger.onInventoryClick(e);
            case "ステータス強化" -> SetPointGUITrigger.onClick(e);
            case "拠点" -> BaseGUITrigger.onClick(e);
            case "拠点強化" -> BaseUpgradeGUITrigger.onClick(e);
            case "メニュー" -> MenuGUITrigger.onClick(e);
            case "ヘルプ一覧" -> HelpGUI.onClick(e);
            case "死亡する？" -> DeathGUI.onClick(e);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e){
        LevelTrigger.onInventoryDrag(e);
        MenuTrigger.onDrag(e);
        switch(e.getView().getTitle()){
            case "拠点強化":
                BaseUpgradeGUITrigger.onDrag(e);
        }
    }

    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent e){
        MenuTrigger.onItemMove(e);
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent e){
        MenuTrigger.onDrop(e);
    }

    @EventHandler
    public void onSwapItem(PlayerSwapHandItemsEvent e){
        MenuTrigger.onSwap(e);
    }

    public static int moveTick = 4;
    public static int afkMoveTick = 20;

    @EventHandler
    public void onMove(PlayerMoveEvent e){
        MechanicTrigger.onMove(e);
        if (count % afkMoveTick == 0) {
            AFKDetector.onMove(e);
        }
    }

    @EventHandler
    public void onLayerLoad(LayerLoadEvent e) {
        LevelTrigger.onLayerLoad(e);
        BaseTrigger.onLayerLoad(e);
    }

    @EventHandler
    public void onLayerChange(LayerChangeEvent e){
        LevelTrigger.onLayerChange(e);
    }

    @EventHandler
    public void onMythicMobDeath(MythicMobDeathEvent e){
        LevelTrigger.onMobDeath(e);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e){
        DisplayTrigger.onFoodChange(e);
    }

    @EventHandler
    public void onConsumeItem(PlayerItemConsumeEvent e){
        DisplayTrigger.onEat(e);
    }

    @EventHandler
    public void onRegisterSideBar(RegisterSideBarEvent e){
        DisplayTrigger.onRegisterSide(e);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        DataTrigger.onJoin(e);
        DiveCore.plugin.delayedTask(1, () -> LevelTrigger.onJoin(e));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        // メッセージを表示しない
        e.setQuitMessage(null);
        DataTrigger.onLogout(e);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        e.setDeathMessage(null);
        MenuTrigger.onDeath(e);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e){
        MenuTrigger.onRespawn(e);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e){
        DisplayTrigger.onFall(e);
    }

    public static int mechanicTick = 6;
    public static int levelTick = 2;
    public static int displayShowTick = 3;
    public static int displayScoreTick = 10;
    public static int itemLimitTick = 5;
    public static int baseGuardTick = 10;
    public static int baseUpdateTick = 5;
    public static int menuTick = 10;

    //とりあえずshiftキーで
    //1Tickごとに送られている
    public static int count = 0;
    public void onTimer(){

        // 処理時間を計測する
        long firstTime = System.currentTimeMillis();

        count++;
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        if (count % mechanicTick == 0){
            MechanicTrigger.on6Tick();
        }
        BaseTrigger.onTick();
        if (count % baseGuardTick == 0){
            BaseTrigger.onTickBaseGuard();
        }
        if (count % 1200 == 0) {
            AFKDetector.on1200Tick();
        }
        for(Player player : players){

            //サバイバル絶許システム
            if(count % 5 == 0){
                if(player.getGameMode().equals(GameMode.SURVIVAL)){
                    Bukkit.getLogger().info("サバイバル禁止！");
                    player.setGameMode(GameMode.ADVENTURE);
                }
            }

            if (count % levelTick == 0){
                LevelTrigger.onTickDisplay(player);
            }
            if (count % displayShowTick == 0){
                DisplayTrigger.onTickShow(player);
            }
            if (count % itemLimitTick == 0){
                DataTrigger.onTickLimit(player);
            }
            if (count % displayScoreTick == 0){
                DisplayTrigger.onTickScore(player);
            }
            if (count % DisplayTrigger.foodInterval == 0){
                DisplayTrigger.onTickFood(player);
            }
            if (count % baseUpdateTick == 0){
                BaseGUITrigger.onTickUpdate(player);
                BaseUpgradeGUITrigger.onTickUpdate(player);
            }
            if (count % menuTick == 0){
                MenuTrigger.onTick(player);
            }
        }

        // 処理時間を計測する
        if (count % 10 == 0) {
            long lastTime = System.currentTimeMillis();
            long taskTime = lastTime - firstTime;
            //Bukkit.getLogger().info("[DiveCore] TimerTask = " + taskTime + "ms");
        }

    }
            /*
                        //slide = 封印
            if(player.getGameMode().equals(GameMode.ADVENTURE)){
                if(player.isSprinting()){
                    if(!player.isFlying()){
                        //slide.slide(player);
                   }
                }
                // slide.test(player);
            }
             */
}

