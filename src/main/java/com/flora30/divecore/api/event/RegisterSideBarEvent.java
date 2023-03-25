package com.flora30.divecore.api.event;

import com.flora30.divecore.display.sidebar.SideOption;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RegisterSideBarEvent extends Event {
    private final List<SideOption> lineList = new ArrayList<>();

    public void addOption(SideOption option){
        lineList.add(option);
    }

    public void setOption(int i, SideOption option){
        if (i < lineList.size()){
            lineList.set(i,option);
        }
    }

    public List<SideOption> getLineList() {
        return lineList;
    }

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList(){
        return HANDLERS_LIST;
    }
}
