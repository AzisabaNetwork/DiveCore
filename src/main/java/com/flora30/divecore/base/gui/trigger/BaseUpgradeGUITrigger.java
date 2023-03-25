package com.flora30.divecore.base.gui.trigger;

import com.flora30.diveapi.plugins.RegionAPI;
import com.flora30.diveapi.tools.DMythicUtil;
import com.flora30.diveapi.tools.PlayerItem;
import com.flora30.divecore.base.Base;
import com.flora30.divecore.base.BaseMain;
import com.flora30.divecore.base.BaseRequire;
import com.flora30.divecore.base.gui.BaseGUI;
import com.flora30.divecore.tools.SoundUtil;
import com.flora30.divecore.tools.type.DiveSound;
import io.lumine.xikage.mythicmobs.MythicMobs;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BaseUpgradeGUITrigger {

    public static void onDrag(InventoryDragEvent event) {
        event.setCancelled(true);
    }

    public static void onClick(InventoryClickEvent event){
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        Inventory inv = event.getClickedInventory();

        if (inv == null){
            return;
        }
        if (event.getSlot() != 22){
            return;
        }
        ItemStack item = inv.getItem(4);
        if (item == null){
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null){
            return;
        }

        int id = meta.getCustomModelData();

        int nextLevel;
        try{
            nextLevel = Integer.parseInt(meta.getDisplayName().replace(ChatColor.GREEN+"拠点レベル ‣ "+ChatColor.WHITE,""));
        } catch (NumberFormatException e){
            return;
        }

        Location location = BaseMain.baseUpgradeInstantList.get(id);
        int baseId =BaseMain.getBaseId(location);
        if (baseId == -1){
            return;
        }
        Base base = BaseMain.baseMap.get(baseId);
        BaseRequire require = BaseMain.baseDataMap.get(RegionAPI.getLayerName(location)).getLevelMap().get(nextLevel);

        //クリック音
        SoundUtil.playSound(player, DiveSound.GuiClick,1.0);

        if (itemCheck(player, require)){
            for (int itemId : require.getRequireMap().keySet()) {
                int amount = require.getRequireMap().get(itemId);
                PlayerItem.takeItem(player,itemId,amount);
            }

            base.setLevel(nextLevel);
            if (base.model != null) {
                Entity beforeModel = Bukkit.getEntity(base.model);
                if(beforeModel != null) beforeModel.remove();
                base.model = null;
            }
            switch (nextLevel) {
                case 1 -> {
                    base.setRemain(base.getRemain() + 600);
                    Entity mob = DMythicUtil.spawnMob("BonFire1", base.getLocation().clone().add(0.5,0,0.5));
                    base.model = mob.getUniqueId();
                }
                case 2 -> {
                    base.setRemain(base.getRemain() + 1200);
                    Entity mob = DMythicUtil.spawnMob("BonFire2", base.getLocation().clone().add(0.5,0,0.5));
                    base.model = mob.getUniqueId();
                }
                case 3 -> {
                    base.setRemain(base.getRemain() + 1200);
                    Entity mob = DMythicUtil.spawnMob("BonFire3", base.getLocation().clone().add(0.5,0,0.5));
                    base.model = mob.getUniqueId();
                }
                case 4 -> {
                    base.setRemain(base.getRemain() + 2400);
                    Entity mob = DMythicUtil.spawnMob("BonFire4", base.getLocation().clone().add(0.5,0,0.5));
                    base.model = mob.getUniqueId();
                }
            }
            player.sendMessage("拠点レベルが"+nextLevel+"に上昇しました！");
            player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST,1,1);
            BaseGUI.open(player, baseId);
            onUpgrade(location);
        }
        else{
            player.sendMessage("素材が足りないようだ...");
        }
    }

    private static void onUpgrade(Location location){
        World world = location.getWorld();
        if (world == null){
            return;
        }
        for (Player player : world.getPlayers()){
            if (player.getLocation().distance(location) >= 15){
                continue;
            }
            InventoryView view = player.getOpenInventory();
            if (view.getTitle().equals("拠点強化")){
                player.sendMessage("他の人によって拠点レベルが上昇しました！");
                player.closeInventory();
            }
        }
    }


    public static void onTickUpdate(Player player) {
        InventoryView view = player.getOpenInventory();
        if (view.getTitle().equals("拠点強化")) {
            //更新
            Inventory inv = view.getTopInventory();
            ItemStack baseItem = inv.getItem(4);
            if (baseItem == null || baseItem.getItemMeta() == null) {
                return;
            }
            int locId = baseItem.getItemMeta().getCustomModelData();
            int baseId = BaseMain.getBaseId(BaseMain.baseUpgradeInstantList.get(locId));

            Base base = BaseMain.baseMap.get(baseId);

            //Bukkit.getLogger().info("baseId = "+baseId);

            if (base.getLevel() > 0 && base.getRemain() == 0) {
                player.closeInventory();
            }
        }
    }



    private static boolean itemCheck(Player player, BaseRequire require){
        for (int itemId : require.getRequireMap().keySet()){
            int amount = require.getRequireMap().get(itemId);

            if (PlayerItem.countItem(player,itemId,true) < amount){
                return false;
            }
        }

        return true;
    }
}
