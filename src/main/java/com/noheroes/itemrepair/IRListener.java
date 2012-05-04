/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noheroes.itemrepair;

import java.util.HashMap;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author PIETER
 */
public class IRListener implements Listener {
    ItemRepair ir;
    
    public IRListener(ItemRepair ir) {
        this.ir = ir;
    }
    
    @EventHandler (priority = EventPriority.NORMAL)
    public void onPlayerClick(PlayerInteractEvent event) {
        // Left clicked block
        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            if (ir.leftClickEvent(event.getPlayer(), event.getClickedBlock().getLocation())) {
                event.setCancelled(true);
            }
        }
        // Right clicked block
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (ir.rightClickEvent(event.getPlayer(), event.getClickedBlock().getLocation())) {
                event.setCancelled(true);
            }
        }
        // Right clicked air
        if (event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            if (ir.rightClickEvent(event.getPlayer(), null)) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler (priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        ir.removePlayerFromEditMode(event.getPlayer());
    }
    
    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        ir.getHandler().blockBreakEvent(event.getBlock().getLocation());
    }
    
    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        List<Block> blockList = event.blockList();
        for (Block block : blockList) {
            ir.getHandler().blockBreakEvent(block.getLocation());
        }
    }
}
