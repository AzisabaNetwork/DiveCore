package com.flora30.divecore.level.gui;

import com.flora30.diveapi.data.player.LevelData;
import com.flora30.diveapi.event.HelpEvent;
import com.flora30.diveapi.tools.GuiItem;
import com.flora30.diveapi.tools.HelpType;
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
        LevelData data = PlayerDataMain.getPlayerData(id).levelData;
        Inventory gui = GuiItem.grayBack(Bukkit.createInventory(null,36, "ステータス確認"));

        gui.setItem(4,SetPointGUI.getCurrentPoint(data.rawPoint));

        gui.setItem(19,SetPointGUI.getIconAtk(data.pointAtk));
        gui.setItem(21,SetPointGUI.getIconVit(data.pointVit));
        gui.setItem(23,SetPointGUI.getIconInt(data.pointInt));
        gui.setItem(25,SetPointGUI.getIconLuc(data.pointLuc));

        gui.setItem(35,GuiItem.getReturn());

        return gui;
    }

}
