/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noheroes.itemrepair;

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
            
        }
        // Right clicked block
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            
        }
    }
}
