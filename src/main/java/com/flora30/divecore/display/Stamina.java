package com.flora30.divecore.display;

import com.flora30.diveapi.data.PlayerData;
import com.flora30.diveapi.plugins.RegionAPI;
import com.flora30.divecore.data.PlayerDataMain;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class Stamina {
    //アクションバーを利用

    //3tickごとの反映
    public static void show(Player player){
        PlayerData data = PlayerDataMain.getPlayerData(player.getUniqueId());
        if (data == null){
            return;
        }

        int current = data.currentST;

        //自動回復（地上のみ）
        if(((Entity)player).isOnGround() && current < data.maxST){
            if (player.isSneaking() && !player.isSprinting()){
                data.currentST = current+3;
            }
            else{
                data.currentST++;
            }
        }

        display(player);
    }

    private static void display(Player player){
        StringBuilder str = new StringBuilder();

        //いろいろ追加
        str.append(getMeterDisplay(player));
        str.append(getStaminaBar(player));
        str.append(getCoolDown(player));

        try{
            TextComponent text = new TextComponent();
            text.setText(String.valueOf(str));
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,text);
        } catch (NoClassDefFoundError e){
            Bukkit.getLogger().info("[DiveCore-Story]BungeeCord未導入です");
            Bukkit.getLogger().info(player.getDisplayName() + " " + str);
        }
        resetCoolDownDisplay(player);
    }

    private static String getMeterDisplay(Player player){
        PlayerData data = PlayerDataMain.getPlayerData(player.getUniqueId());
        if (data == null || data.layerData == null || data.layerData.layer == null || data.layerData.layer.equals("no")){
            return "";
        }
        if (data.layerData.layer.equals("oldOrth")){
            return "";
        }
        double fallPlus = 200 - player.getLocation().getY();
        double fallLayer = RegionAPI.getLayer(data.layerData.layer).fall;
        double fall = fallPlus+fallLayer;

        // 境界付近で黄色にする（猶予10m）
        if (player.getLocation().getY() < 60 || 220 < player.getLocation().getY()) {
            return (ChatColor.GREEN+"深度 >> "+ChatColor.YELLOW+String.format("%7.1f",fall)+"m  ");
        }

        return (ChatColor.GREEN+"深度 >> "+String.format("%7.1f",fall)+"m  ");
    }

    private static String getStaminaBar(Player player){
        PlayerData data = PlayerDataMain.getPlayerData(player.getUniqueId());
        if (data == null){
            return "";
        }
        int max = data.maxST;
        int current = data.currentST;

        int i_max = max / 20;
        int i_current = current / 20;

        //表示の作成
        String bar = "¦";
        StringBuilder str = new StringBuilder();
        str.append(ChatColor.WHITE).append("スタミナ[ ");

        //スニーク回復中は色変更
        if (player.isSneaking() && !player.isSprinting()){
            str.append(ChatColor.LIGHT_PURPLE);
        }
        else{
            str.append(ChatColor.GREEN);
        }

        for (int i = 0; i < i_current; i++){
            str.append(bar);
        }
        str.append(ChatColor.GRAY);
        for (int i = 0; i < i_max - i_current; i++){
            str.append(bar);
        }
        str.append(ChatColor.WHITE);
        str.append(" ]");

        return String.valueOf(str);
    }

    private static String getCoolDown(Player player){
        PlayerData data = PlayerDataMain.getPlayerData(player.getUniqueId());
        if (data == null){
            return "";
        }

        String name = data.displayCoolDownName;
        if (name == null){
            return "";
        }
        if (data.coolDownMap.get(name) == null){
            return " "+ChatColor.WHITE+"再使用 > ok";
        }

        double coolDown = data.coolDownMap.get(name);

        if (coolDown == 0){
            return " "+ChatColor.WHITE+"再使用 > ok";
        }

        coolDown /= 20.0;

        return " "+ChatColor.WHITE+"再使用 > "+String.format("%5.2f",coolDown)+"秒";
    }

    private static void resetCoolDownDisplay(Player player){
        PlayerData data = PlayerDataMain.getPlayerData(player.getUniqueId());
        if (data == null){
            return;
        }

        data.displayCoolDownName = null;
    }
}
