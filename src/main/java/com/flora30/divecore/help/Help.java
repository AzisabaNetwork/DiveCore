package com.flora30.divecore.help;

import com.flora30.diveapi.tools.HelpType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Help {
    public HelpType trigger;
    private Material material;
    private String title;
    private List<String> lore;

    public ItemStack getItem(){
        ItemStack item = new ItemStack(material);
        if (item.getItemMeta() == null){
            return item;
        }
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(title);
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    public String getTitle() {
        return title;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLore(List<String> lore) {

        List<String> newLore = new ArrayList<>();

        //白色に統一する
        if (lore == null){
            this.lore = newLore;
            return;
        }

        newLore.add("");

        for (String s : lore) {
            newLore.add(ChatColor.WHITE + s);
        }

        this.lore = newLore;
    }
}
