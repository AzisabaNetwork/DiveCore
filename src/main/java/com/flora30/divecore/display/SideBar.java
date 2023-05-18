package com.flora30.divecore.display;

import com.flora30.divecore.api.event.RegisterSideBarEvent;
import com.flora30.divecore.data.PlayerDataMain;
import com.flora30.divecore.display.sidebar.SideOption;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.List;

public class SideBar {
    private static List<SideOption> optionList;

    static int uniqueId = 0;
    public static int getUniqueSideBarID() {
        uniqueId++;
        return uniqueId;
    }

    public static void load(){
        RegisterSideBarEvent event = new RegisterSideBarEvent();
        Bukkit.getPluginManager().callEvent(event);
        optionList = event.getLineList();
    }

    /**
     * Newを使うとアイテムの発光に使っているMainScoreboardが使えなくなるため、どちらか片方
     */
    public static void sendScoreBoard(Player player){
        /*
        if (PlayerDataMain.getPlayerData(player.getUniqueId()) == null){
            return;
        }
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if(manager == null){
            return;
        }
        Scoreboard board = manager.getNewScoreboard();

        //諸々の登録
        applySideBar(player, board);

        //playerに反映
        player.setScoreboard(board);

         */
    }

    private static void applySideBar(Player player, Scoreboard board){
        String top = ChatColor.WHITE +"- "+ ChatColor.GOLD +"Dive RPG"+ ChatColor.WHITE +" -";

        Objective objective = board.getObjective("diveBar");
        if (objective == null) {
            objective = board.registerNewObjective("diveBar", "dummy",top);
        }

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        for (int i = 0; i < optionList.size(); i++){
            String line = optionList.get(i).getLine(player);
            if (line == null){
                continue;
            }
            objective.getScore(line).setScore(optionList.size()-i);
        }
    }
}
