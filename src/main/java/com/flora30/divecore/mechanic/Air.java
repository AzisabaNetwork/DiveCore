package com.flora30.divecore.mechanic;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public class Air {
    public static void check(Player player){
        //一定までは許容
        if (player.getFallDistance() <= 25.0){
            return;
        }

        //落下ダメージ判定
        EntityDamageEvent event = new EntityDamageEvent(player, EntityDamageEvent.DamageCause.FALL,player.getFallDistance() - 3);
        Bukkit.getPluginManager().callEvent(event);
        player.damage(event.getDamage());
        player.playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK,1,1);
    }
}
