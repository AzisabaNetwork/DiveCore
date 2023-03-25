package com.flora30.divecore.display;

import com.flora30.diveapi.data.ItemData;
import com.flora30.diveapi.data.PlayerData;
import com.flora30.diveapi.plugins.ItemAPI;
import com.flora30.divecore.data.PlayerDataMain;
import com.flora30.divecore.tools.Mathing;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FoodLevel {

    public static void show(Player player){
        if (PlayerDataMain.getPlayerData(player.getUniqueId()) == null){
            return;
        }
        int food = PlayerDataMain.getPlayerData(player.getUniqueId()).food;
        player.setFoodLevel(food);

        applySpeed(player);
    }

    public static void applySpeed(Player player){
        if (player.getFoodLevel() >= 16){
            PotionEffect effect = new PotionEffect(PotionEffectType.SPEED,5,0,true,false,true);
            player.addPotionEffect(effect);
        }
        else if(player.getFoodLevel() <= 4){
            PotionEffect effect = new PotionEffect(PotionEffectType.SLOW,5,0,true,false,true);
            player.addPotionEffect(effect);
        }
    }

    public static void decrease(Player player){
        PlayerData data = PlayerDataMain.getPlayerData(player.getUniqueId());
        double current = data.food;
        //初期町は減らない
        if (data.layerData.layer.equals("oldOrth")){
            return;
        }
        //（減少判定）最小30% - 最大100%
        if (Mathing.getRandomInt(100) < (current * 5) * 0.7 + 30){
            // 隠し満腹度の消費
            /*
            if (player.getSaturation() >= 1) {
                player.setSaturation(player.getSaturation() - 1);
                return;
            }
             */

            // 満腹度の消費
            int decreased = data.food - 1;
            if (decreased < 0){
                decreased = 0;
            }
            data.food = (decreased);
        }
    }

    public static void onEat(Player player, ItemStack item){
        PlayerData data = PlayerDataMain.getPlayerData(player.getUniqueId());
        ItemData iData = ItemAPI.getItemData(ItemAPI.getItemID(item));
        if (iData == null){
            return;
        }
        int food = iData.food;
        //Bukkit.getLogger().info("Food: "+food);

        int ate = data.food + food;
        if (ate > 20){
            ate = 20;
        }
        data.food = (ate);


        // 回復
        double healAmount = food / 2.0F;
        double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        double health = player.getHealth();

        //死んでいる場合は回復しない
        if (health <= 0) return;

        if (maxHealth > health) {
            health = Math.min(health + healAmount, maxHealth);
            player.setHealth(health);
            player.spawnParticle(Particle.HEART,player.getLocation().add(0,1,0),5,0.3,0.3,0.3);
        }
        //player.setSaturation(player.getSaturation() + (food / 3.0F));
    }
}
