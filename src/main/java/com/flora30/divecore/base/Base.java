package com.flora30.divecore.base;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;

import java.util.UUID;

public class Base {
    private int level = 0;
    private boolean isTown = false;
    private int remain = 0;
    private Location location;
    private boolean isPrepared = false;
    private BlockFace face;
    public UUID model;

    public boolean isPrepared() {
        return isPrepared;
    }

    public void setPrepared(boolean prepared) {
        isPrepared = prepared;
    }

    public BlockFace getFace() {
        return face;
    }

    public void setFace(BlockFace face) {
        this.face = face;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isTown() {
        return isTown;
    }

    public void setTown(boolean town) {
        isTown = town;
    }

    public int getRemain() {
        return remain;
    }

    public void setRemain(int remain) {
        this.remain = remain;
    }
}
