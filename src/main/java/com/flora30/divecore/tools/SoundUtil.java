package com.flora30.divecore.tools;

import com.flora30.divecore.tools.type.DiveSound;
import org.bukkit.entity.Player;

public class SoundUtil {

    public static void playSound(Player player, DiveSound sound, double pitch){
        player.playSound(player.getLocation(),sound.getSound(),1, (float)pitch);
    }

}
