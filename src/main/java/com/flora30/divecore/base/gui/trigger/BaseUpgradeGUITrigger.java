package com.flora30.divecore.base.gui.trigger;

import com.flora30.divelib.data.Base;
import com.flora30.divelib.data.BaseObject;
import com.flora30.divelib.util.DMythicUtil;
import com.flora30.divelib.util.PlayerItem;
import com.flora30.divecore.base.BaseMain;
import com.flora30.divecore.base.gui.BaseGUI;
import com.flora30.divecore.tools.SoundUtil;
import com.flora30.divecore.tools.type.DiveSound;
import com.flora30.diveconstant.data.*;
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


        Base base = BaseObject.INSTANCE.getBaseMap().get(baseId);
        BaseLocation baseLocation = BaseDataObject.INSTANCE.getBaseLocationMap().get(baseId);
            // 次のレベルの必要素材を取得
        BaseLayer baseLayer = BaseDataObject.INSTANCE.getBaseLayerMap().get(LayerObject.INSTANCE.getLayerName(location));
        BaseRequire baseRequire = baseLayer.getLevelMap().get(nextLevel);

        //クリック音
        SoundUtil.playSound(player, DiveSound.GuiClick,1.0);

        if (itemCheck(player, baseRequire)){
            PlayerItem.INSTANCE.takeItem(player,baseRequire.getRequire1Id(),baseRequire.getRequire1Amount());
            PlayerItem.INSTANCE.takeItem(player,baseRequire.getRequire2Id(),baseRequire.getRequire2Amount());

            base.setLevel(nextLevel);
            if (base.getModel() != null) {
                Entity beforeModel = Bukkit.getEntity(base.getModel());
                if(beforeModel != null) beforeModel.remove();
                base.setModel(null);
            }
            switch (nextLevel) {
                case 1 -> {
                    base.setRemain(base.getRemain() + 600);
                    Entity mob = DMythicUtil.INSTANCE.spawnMob("BonFire1", baseLocation.getLocation().clone().add(0.5,0,0.5));
                    assert mob != null;
                    base.setModel(mob.getUniqueId());
                }
                case 2 -> {
                    base.setRemain(base.getRemain() + 1200);
                    Entity mob = DMythicUtil.INSTANCE.spawnMob("BonFire2", baseLocation.getLocation().clone().add(0.5,0,0.5));
                    assert mob != null;
                    base.setModel(mob.getUniqueId());
                }
                case 3 -> {
                    base.setRemain(base.getRemain() + 1200);
                    Entity mob = DMythicUtil.INSTANCE.spawnMob("BonFire3", baseLocation.getLocation().clone().add(0.5,0,0.5));
                    assert mob != null;
                    base.setModel(mob.getUniqueId());
                }
                case 4 -> {
                    base.setRemain(base.getRemain() + 2400);
                    Entity mob = DMythicUtil.INSTANCE.spawnMob("BonFire4", baseLocation.getLocation().clone().add(0.5,0,0.5));
                    assert mob != null;
                    base.setModel(mob.getUniqueId());
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

            Base base = BaseObject.INSTANCE.getBaseMap().get(baseId);

            //Bukkit.getLogger().info("baseId = "+baseId);

            if (base.getLevel() > 0 && base.getRemain() == 0) {
                player.closeInventory();
            }
        }
    }


    private static boolean itemCheck(Player player, BaseRequire require){
        if (PlayerItem.INSTANCE.countItem(player,require.getRequire1Id(),true) < require.getRequire1Amount()) return false;
        if (PlayerItem.INSTANCE.countItem(player,require.getRequire2Id(),true) < require.getRequire2Amount()) return false;
        return true;
    }
}
