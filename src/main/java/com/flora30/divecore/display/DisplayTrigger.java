package com.flora30.divecore.display;


import com.flora30.diveapi.data.PlayerData;
import com.flora30.divecore.data.PlayerDataMain;
import com.flora30.divecore.display.sidebar.*;
import com.flora30.divecore.api.event.RegisterSideBarEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class DisplayTrigger {
    public static int foodInterval = 300;

    public static void onTickShow(Player player){
        if (PlayerDataMain.getPlayerData(player.getUniqueId()) == null){
            return;
        }
        Stamina.show(player);
        FoodLevel.show(player);
    }

    public static void onTickScore(Player player){
        if (PlayerDataMain.getPlayerData(player.getUniqueId()) == null){
            return;
        }
        SideBar.sendScoreBoard(player);
    }
    public static void onTickFood(Player player){
        if (PlayerDataMain.getPlayerData(player.getUniqueId()) == null){
            return;
        }
        FoodLevel.decrease(player);
    }

    public static void onRegisterSide(RegisterSideBarEvent e){
        e.addOption(new ExpSide());
        e.addOption(new MoneySide());
        e.addOption(new AreaSide());
        e.addOption(new MissionStorySide());
        e.addOption(new MissionMobSide());
        e.addOption(new MissionItemSide());
    }

    //デフォルトの減少方法を無効化
    public static void onFoodChange(FoodLevelChangeEvent e){
        if (!(e.getEntity() instanceof Player player)){
            return;
        }
        PlayerData data = PlayerDataMain.getPlayerData(player.getUniqueId());
        if (data == null) {
            e.setCancelled(true);
            return;
        }
        e.setFoodLevel(data.food);
    }

    public static void onEat(PlayerItemConsumeEvent e){
        ItemStack item = e.getItem();
        //edible=食用
        if (!item.getType().isEdible()){
            return;
        }
        FoodLevel.onEat(e.getPlayer(), item);
    }



    public static void onFall(EntityDamageEvent e){
        if (e.getEntity() instanceof Player){
            Player player = (Player) e.getEntity();
            if (e.getCause() == EntityDamageEvent.DamageCause.FALL){
                //落下ダメージを受けた時
                // 俵nerf
                if (player.getLocation().add(0,-1,0).getBlock().getType() == Material.HAY_BLOCK) {
                    e.setDamage(e.getDamage() * 4);
                }

                // スタミナによる軽減処理
                int currentST = PlayerDataMain.getPlayerData(player.getUniqueId()).currentST;
                if (currentST >= 30){
                    e.setDamage(e.getDamage()-0.9);
                    currentST -= 30;
//                    player.sendMessage("スタミナで衝撃を抑えた……");
                }
                if (currentST >= 30 && e.getDamage() >= 1){
                    e.setDamage(e.getDamage()-0.9);
                    currentST -= 30;
                }
                if (currentST >= 30 && e.getDamage() >= 1){
                    e.setDamage(e.getDamage()-0.9);
                    currentST -= 30;
                }
                if (currentST >= 30 && e.getDamage() >= 1){
                    e.setDamage(e.getDamage()-0.9);
                    currentST -= 30;
                }
                if (currentST >= 30 && e.getDamage() >= 1){
                    e.setDamage(e.getDamage()-0.9);
                    currentST -= 30;
                }
                PlayerDataMain.getPlayerData(player.getUniqueId()).currentST = currentST;
            }
        }
    }
}
