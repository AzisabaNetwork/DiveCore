package com.flora30.divecore.tools;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ClearTrigger implements Listener {

    //ブロック右クリック時の動作
    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        if (event.getClickedBlock() == null){
            return;
        }
        Material material = event.getClickedBlock().getType();
        switch (material){
            case CRAFTING_TABLE:
            case FURNACE:
            case BLAST_FURNACE:
            case SMOKER:
            case BARREL:
            case GRINDSTONE:
            case LOOM:
            case COMPOSTER:
            case CARTOGRAPHY_TABLE:
            case STONECUTTER:
            case SMITHING_TABLE:
            case NOTE_BLOCK:
            case JUKEBOX:
            case DISPENSER:
            case DROPPER:
            case HOPPER:
            case LECTERN:
            case ITEM_FRAME:
            case OAK_TRAPDOOR:
            case IRON_TRAPDOOR:
            case BIRCH_TRAPDOOR:
            case ACACIA_TRAPDOOR:
            case JUNGLE_TRAPDOOR:
            case SPRUCE_TRAPDOOR:
            case WARPED_TRAPDOOR:
            case CRIMSON_TRAPDOOR:
            case OAK_FENCE_GATE:
            case BIRCH_FENCE_GATE:
            case ACACIA_FENCE_GATE:
            case JUNGLE_FENCE_GATE:
            case SPRUCE_FENCE_GATE:
            case WARPED_FENCE_GATE:
            case CRIMSON_FENCE_GATE:
            case DARK_OAK_FENCE_GATE:
                event.setCancelled(true);
        }
    }

    //バニラのクラフト無効化
    @EventHandler
    public void onCraft(CraftItemEvent event){
        event.setCancelled(true);
    }
}
