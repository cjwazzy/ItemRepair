/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noheroes.itemrepair;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 *
 * @author PIETER
 */
public class EditMode {
    private static RepairStationHandler stationHandler;
    private Player player;
    private String stationName;
    private EditStep editStep;
    private Location buttonLoc;
    private Location dispenserLoc;
    
    public enum EditStep {
        ADD_BUTTON, ADD_DISPENSER
    }
    
    public EditMode(Player player, String stationName, RepairStationHandler stationHandler) {
        this.player = player;
        this.stationName = stationName;
        this.stationHandler = stationHandler;
        this.editStep = EditStep.ADD_BUTTON;
        player.sendMessage(ChatColor.GREEN + "Left click the button for the repair station");
    }    
    
    public boolean leftClickEvent(Location loc) {
        if (editStep == EditStep.ADD_BUTTON) {
            if (loc.getBlock().getType().equals(Material.STONE_BUTTON)) {
                buttonLoc = loc;
                editStep = EditStep.ADD_DISPENSER;
                player.sendMessage(ChatColor.GREEN + "Now left click the dispenser for the repair station");
            }
        }
        if (editStep == EditStep.ADD_DISPENSER) {
            if (loc.getBlock().getType().equals(Material.DISPENSER)) {
                dispenserLoc = loc;
                player.sendMessage(ChatColor.YELLOW + "Repair station " + stationName + " added");
                stationHandler.createStation(buttonLoc, dispenserLoc, stationName);
                return true;
            }
        }
        return false;
    }
}
