package com.flora30.divecore.tools.deprecated;

import com.flora30.diveapi.data.PlayerData;
import com.flora30.divecore.data.PlayerDataMain;
import com.flora30.divecore.tools.Mathing;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;

public class Slide {
    //坂が無いとき現在の速度を判定して減速する
    //落ちているとき「加速」を貯めて、落下後に反映する
    private static final Map<UUID, Double> stackVec = new HashMap<>();

    final Mathing mathing = new Mathing();

    /*public void test(Player player) {
        double relSimpleX = mathing.getSimpleXFromYaw(player.getLocation().getYaw()) * 100;
        double relSimpleZ = mathing.getSimpleZFromYaw(player.getLocation().getYaw()) * 100;
        int iRelSimpleX = (int) relSimpleX;
        int iRelSimpleZ = (int) relSimpleZ;
        int yaw = (int) player.getLocation().getYaw();
        Bukkit.getLogger().info("Yaw = " + yaw + " X = " + iRelSimpleX + " Z = " + iRelSimpleZ);
    }

     */

    public void slide(Player player) {
        UUID id = player.getUniqueId();
        double relSimpleX = mathing.getSimpleXFromYaw(player.getLocation().getYaw());
        double relSimpleZ = mathing.getSimpleZFromYaw(player.getLocation().getYaw());
        

        if (!((LivingEntity) player).isOnGround()) {
            //落ちているとき
            Vector vector = player.getVelocity().clone();
            if(vector.getY()>=0){
                return;
            }
            //スタミナを消費
            PlayerData playerData = PlayerDataMain.getPlayerData(player.getUniqueId());
            if (playerData.currentST > 0){
                playerData.currentST--;
            }
            else{
                return;
            }

            //めっちゃ下に行く
            vector.setY(vector.getY() - 0.4);
            //下に落ちた後、前に加速する数値を加算
            if (stackVec.containsKey(id)) {
                stackVec.put(player.getUniqueId(), stackVec.get(id) + 1.0);
            } else {
                stackVec.put(player.getUniqueId(), 1.0);
            }
            Bukkit.getLogger().info("落下判定：" + vector.getY() + " - 1.0");
            player.setVelocity(vector);
        } else {
            //地面にいるとき
            //ジャンプキャンセル
            //player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 6, 250, false, false, false));

            /*if (checkLoc.getBlock().getType().isAir()) {
                //引っかかりを超える
                Bukkit.getLogger().info("引っかかり判定：" + checkLoc.getX() + " + " + relSimpleX + " +Yaw " + player.getLocation().getYaw());
                checkLoc.setX(relSimpleX * 0.15 + checkLoc.getX());
                checkLoc.setY(checkLoc.getY() + 0.9);
                checkLoc.setZ(relSimpleZ * 0.15 + checkLoc.getZ());
                player.teleport(checkLoc);
                return;
            }
             */

            if (stackVec.containsKey(id)) {
                if(stackVec.get(id) > 4){
                    stackVec.put(id,4.0);
                }

                Bukkit.getLogger().info(player.getDisplayName() + " : " + stackVec.get(id) + "で落下しました");
                //落下直後
                double adding = stackVec.get(id) * 0.15 + 0.3;
                double x = relSimpleX * adding;
                double z = relSimpleZ * adding;

                Vector vec2 = player.getVelocity().clone();
                vec2.setX(x);
                vec2.setZ(z);

                player.setVelocity(vec2);
                if (stackVec.get(id) <= 0) {
                    stackVec.remove(id);
                } else if(stackVec.get(id) > 2) {
                    stackVec.put(id, stackVec.get(id) - 2.0);
                } else {
                    stackVec.put(id, stackVec.get(id) - 1.0);
                }
            }
        }
    }
}