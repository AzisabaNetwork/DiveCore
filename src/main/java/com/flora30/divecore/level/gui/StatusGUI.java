package com.flora30.divecore.level.gui;

import com.flora30.divelib.data.player.LevelData;
import com.flora30.divelib.data.player.PlayerDataObject;
import com.flora30.divelib.event.HelpEvent;
import com.flora30.divelib.event.HelpType;
import com.flora30.divelib.util.GuiItem;
import com.flora30.divecore.data.PlayerDataMain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public class StatusGUI {

    public static void open(Player player) {
        Inventory gui = create(player.getUniqueId());
        Bukkit.getPluginManager().callEvent(new HelpEvent(player, HelpType.StatusGUI));
        player.openInventory(gui);
    }

    private static Inventory create(UUID id) {
        LevelData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(id).getLevelData();
        Inventory gui = GuiItem.INSTANCE.grayBack(Bukkit.createInventory(null,36, "ステータス確認"));

        gui.setItem(4,SetPointGUI.getCurrentPoint(data.getRawPoint()));

        gui.setItem(19,SetPointGUI.getIconAtk(data.getPointAtk()));
        gui.setItem(21,SetPointGUI.getIconVit(data.getPointVit()));
        gui.setItem(23,SetPointGUI.getIconInt(data.getPointInt()));
        gui.setItem(25,SetPointGUI.getIconLuc(data.getPointLuc()));

        gui.setItem(35,GuiItem.INSTANCE.getReturn());

        return gui;
    }

}
