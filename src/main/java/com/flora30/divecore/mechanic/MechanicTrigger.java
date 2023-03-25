package com.flora30.divecore.mechanic;

import com.flora30.divecore.DiveCore;
import org.bukkit.event.player.PlayerMoveEvent;

import static com.flora30.divecore.Listeners.count;
import static com.flora30.divecore.Listeners.moveTick;

public class MechanicTrigger {

    public static void onMove(PlayerMoveEvent e){
        if (e.getTo() == null){
            return;
        }
        // 水のチェックは毎回ちゃんとやらないと意味が無い
        Water.check(e.getPlayer(), e.getTo().getBlock().getBlockData());
        if (count % moveTick == 0){
            Air.check(e.getPlayer());
        }
    }

    public static void on6Tick(){
        DiveCore.plugin.asyncTask(Light::lightCheck);
    }
}
