package com.flora30.divecore.display;

import com.flora30.divelib.data.player.PlayerData;
import com.flora30.divelib.data.player.PlayerDataObject;
import com.flora30.divecore.data.PlayerDataMain;
import com.flora30.divelib.data.LayerObject;
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
        PlayerData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId());
        if (data == null){
            return;
        }

        int current = data.getCurrentST();

        //自動回復（地上のみ）
        if(((Entity)player).isOnGround() && current < data.getMaxST()){
            if (player.isSneaking() && !player.isSprinting()){
                data.setCurrentST(current+3);
            }
            else{
                data.setCurrentST(current+1);
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
        PlayerData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId());
        if (data == null || data.getLayerData().getLayer() == null || data.getLayerData().getLayer().equals("no")){
            return "";
        }
        if (data.getLayerData().getLayer().equals("oldOrth")){
            return "";
        }
        double fallPlus = 200 - player.getLocation().getY();
        double fallLayer = LayerObject.INSTANCE.getLayerMap().get(data.getLayerData().getLayer()).getFall();
        double fall = fallPlus+fallLayer;

        // 境界付近で黄色にする（猶予10m）
        if (player.getLocation().getY() < 60 || 220 < player.getLocation().getY()) {
            return (ChatColor.GREEN+"深度 >> "+ChatColor.YELLOW+String.format("%7.1f",fall)+"m  ");
        }

        return (ChatColor.GREEN+"深度 >> "+String.format("%7.1f",fall)+"m  ");
    }

    private static String getStaminaBar(Player player){
        PlayerData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId());
        if (data == null){
            return "";
        }
        int max = data.getMaxST();
        int current = data.getCurrentST();

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

        str.append(bar.repeat(Math.max(0, i_current)));
        str.append(ChatColor.GRAY);
        str.append(bar.repeat(Math.max(0, i_max - i_current)));
        str.append(ChatColor.WHITE);
        str.append(" ]");

        return String.valueOf(str);
    }

    private static String getCoolDown(Player player){
        PlayerData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId());
        if (data == null){
            return "";
        }

        String name = data.getDisplayCoolDownName();
        if (name == null){
            return "";
        }
        if (data.getCoolDownMap().get(name) == null){
            return " "+ChatColor.WHITE+"再使用 > ok";
        }

        double coolDown = data.getCoolDownMap().get(name);

        if (coolDown == 0){
            return " "+ChatColor.WHITE+"再使用 > ok";
        }

        coolDown /= 20.0;

        return " "+ChatColor.WHITE+"再使用 > "+String.format("%5.2f",coolDown)+"秒";
    }

    private static void resetCoolDownDisplay(Player player){
        PlayerData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId());
        if (data == null){
            return;
        }

        data.setDisplayCoolDownName(null);
    }
}
