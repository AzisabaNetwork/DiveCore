package com.flora30.divecore.mechanic;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Water {

    public static void check(Player player, BlockData next){
        //水中判定
        if (isWaterBlock(next)){
            //Bukkit.getLogger().info("water fall listened");

            if (next instanceof Levelled levelled) {
                //1以上＝水流系
                if (levelled.getLevel() > 0){
                    player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER,10,2));
                    player.playSound(player.getLocation(),Sound.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT,1,1);
                }
            }


            //落下ダメージを受けない場合
            if (player.getFallDistance() <= 3.0){
                //Bukkit.getLogger().info("water fall distance = "+player.getFallDistance());
                return;
            }

            //落下ダメージ判定
            EntityDamageEvent event = new EntityDamageEvent(player, EntityDamageEvent.DamageCause.FALL,player.getFallDistance() - 3);
            Bukkit.getPluginManager().callEvent(event);
            player.damage(event.getDamage());
            player.playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK,1,1);
        }
    }

    private static boolean isWaterBlock(BlockData data) {
        if (data instanceof Waterlogged waterlogged) {
            return waterlogged.isWaterlogged();
        }

        switch (data.getMaterial()) {
            case WATER,LAVA,SEAGRASS,TALL_SEAGRASS -> {return true;}
        }

        return false;
    }
}
