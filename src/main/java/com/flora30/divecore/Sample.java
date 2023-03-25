package com.flora30.divecore;

public class Sample {
    /*落下強制空間用
    @EventHandler(ignoreCancelled = false)
    public void onMove(PlayerMoveEvent e) {
        Location from = e.getFrom(), to = e.getTo();
        if(from.getY() < to.getY()) { // OBS: care on what you do with this
            e.getPlayer().teleport(new Location(from.getWorld(), from.getX(), from.getY(), from.getZ(), to.getYaw(), to.getPitch());
            e.setCancelled(true); // I always have an issue with cancelling this, so I just teleport them
        }
    }
     */
}
