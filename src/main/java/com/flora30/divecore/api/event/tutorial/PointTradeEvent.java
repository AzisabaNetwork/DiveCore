package com.flora30.divecore.api.event.tutorial;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PointTradeEvent extends Event {
    //新規作成タイミング（Configライン追加）
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }
    public static HandlerList getHandlerList(){
        return HANDLERS_LIST;
    }

    private final Player player;

    public PointTradeEvent(Player player){
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
