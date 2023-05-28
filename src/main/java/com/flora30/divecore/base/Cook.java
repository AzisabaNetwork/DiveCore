package com.flora30.divecore.base;

import com.flora30.divelib.ItemMain;
import com.flora30.divelib.data.player.PlayerData;
import com.flora30.divelib.data.player.PlayerDataObject;
import com.flora30.divelib.util.DMythicUtil;
import com.flora30.divelib.util.PlayerItem;
import com.flora30.diveconstant.data.item.ItemData;
import com.flora30.diveconstant.data.item.ItemDataObject;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Cook {

    // 料理のクールダウンはplayerdata - cooldownMap の中に入れて管理している
    public static final String coolDownID = "Cook";

    // 料理演出に使うMobの名前
    private static final String effectMobName = "CookFire";

    public static boolean cook(Player player, Location baseLoc) {
        // 焼くクールダウンを取得
        PlayerData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId());
        if (data == null) return false;
        if (data.getCoolDownMap().containsKey(coolDownID)) {
            int coolDown = data.getCoolDownMap().get(coolDownID);
            if (coolDown != 0) return false;
        }

        // 焼けるものIDを取得
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getItemMeta() == null) return false;
        int cookId = ItemMain.INSTANCE.getItemId(item);
        ItemData itemData = ItemDataObject.INSTANCE.getItemDataMap().get(cookId);

        // 焼いた後を取得
        if (itemData.getCookData() == null) return false;
        ItemStack cookedItem = ItemMain.INSTANCE.getItem(itemData.getCookData().getDishId());
        if (cookedItem == null) return false;

        // 交換
        item.setAmount(item.getAmount() - 1);
        PlayerItem.INSTANCE.giveItem(player,cookedItem);
        data.getCoolDownMap().put(coolDownID, 10);

        // 演出
        player.playSound(baseLoc, Sound.BLOCK_FIRE_EXTINGUISH,1,1);
        player.playSound(baseLoc,Sound.BLOCK_CAMPFIRE_CRACKLE,1,1);
        player.spawnParticle(Particle.CLOUD,baseLoc,20,0.5,0.5,0.1,0.1);
        // Mobを召喚する
        DMythicUtil.INSTANCE.spawnMob(effectMobName, baseLoc.clone().add(0.5, 0, 0.5));
        return true;
    }
}
