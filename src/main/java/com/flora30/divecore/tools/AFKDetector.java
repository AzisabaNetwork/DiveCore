package com.flora30.divecore.tools;

import com.flora30.diveapi.data.PlayerData;
import com.flora30.divecore.DiveCore;
import com.flora30.divecore.data.PlayerDataMain;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;

public class AFKDetector {
    static List<Integer> messageTimes = List.of(5,10,14);

    // 1分に1回走る
    public static void on1200Tick() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerData data = PlayerDataMain.getPlayerData(player.getUniqueId());
            if (data == null) continue;

            if (data.afkLocation == null) {
                data.afkLocation = player.getLocation().clone();
                continue;
            }
            if (data.afkLocation.getWorld() != player.getWorld()) {
                data.afkLocation = player.getLocation().clone();
                continue;
            }
            // AFK判定（成功するとポイント）
            if (data.afkLocation.distance(player.getLocation()) > 1) {
                data.afkLocation = player.getLocation().clone();
                continue;
            }


            data.afkTime += 1;
            Bukkit.getLogger().info("[AFK]"+player.getDisplayName()+"のAFKポイント -> " + data.afkTime);

            // メッセージ表示
            if (messageTimes.contains(data.afkTime)) {
                player.sendMessage(ChatColor.GRAY + "現在AFK状態です。動くと解除されます。（AFK鯖への移動まで残り"+(15-data.afkTime)+"分）");
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK,1,1);
            }

            // 条件を満たしたとき
            if (data.afkTime >= 15) {
                // afk鯖に送る
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Connect");
                out.writeUTF("afk");
                player.sendPluginMessage(DiveCore.plugin,"BungeeCord",out.toByteArray());
            }
        }
    }

    public static void onMove(PlayerMoveEvent e) {
        PlayerData data = PlayerDataMain.getPlayerData(e.getPlayer().getUniqueId());

        if (data.afkTime >= 5) {
            e.getPlayer().sendMessage(ChatColor.GRAY + "AFK状態を解除しました");
        }
        data.afkTime = 0;
    }
}
