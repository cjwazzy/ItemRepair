/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noheroes.itemrepair;

import org.bukkit.Location;


/**
 *
 * @author PIETER
 */
public class RepairStation {
    private Location buttonLoc;
    private Location dispenserLoc;
    private String stationName;
    
    public RepairStation(Location buttonLoc, Location dispenserLoc, String stationName) {
        this.buttonLoc = buttonLoc;
        this.dispenserLoc = dispenserLoc;
        this.stationName = stationName;
    }
    
    public String getName() {
        return this.stationName;
    }
    
    public Location getButtonLoc() {
        return this.buttonLoc;
    }
    
    public Location getDispenserLoc() {
        return this.dispenserLoc;
    }
}
