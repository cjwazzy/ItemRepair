/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noheroes.itemrepair;

import java.util.HashMap;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author PIETER
 */
public class IRListener implements Listener {
    ItemRepair ir;
    
    public IRListener(ItemRepair ir) {
        this.ir = ir;
    }
    
    @EventHandler (priority = EventPriority.MONITOR)
    public void onPlayerClick(PlayerInteractEvent event) {
        // Left clicked block
        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            Player player = event.getPlayer();
            Material mat = event.getMaterial();
            HashMap<Material, Integer> temp = ir.playerCanAfford(player, mat);
            if (temp == null) {
                ir.log("You have the materials");
            }
            else {
                for (Material material : temp.keySet()) {
                    ir.log("Missing: " + material.toString() + ":" + temp.get(material).toString());
                }
            }
        }
        // Right clicked block
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            
        }
    }
}
