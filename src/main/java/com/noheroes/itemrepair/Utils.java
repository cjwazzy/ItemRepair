/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noheroes.itemrepair;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

/**
 *
 * @author PIETER
 */
public class Utils {

    private Utils () {};
    
    public static Material getMaterialFromString(String mat) {
        Integer matID;
        Material material;
        try {
            matID = Integer.valueOf(mat);
        } catch (NumberFormatException ex) {
            matID = null;
        }
        if (matID == null) {
            material = Material.getMaterial(mat.toUpperCase());
        }
        else {
            material = Material.getMaterial(matID);
        }
        return material;
    }
    
    public static Integer getIntFromString(String intStr) {
        Integer returnInt;
        try {
            returnInt = Integer.valueOf(intStr);
        } catch (NumberFormatException ex) {
            returnInt = null;
        }
        return returnInt;
    }
    
    public static String[] locToString(Location location) {
        String[] locStr = new String[4];
        locStr[0] = location.getWorld().getName();
        locStr[1] = String.valueOf(location.getBlockX());
        locStr[2] = String.valueOf(location.getBlockY());
        locStr[3] = String.valueOf(location.getBlockZ());
        return locStr;
    }
    
    public static Location stringToLoc(String locStr[]) {
        // locStr being equal to null is not uncommon, it happens any time there is no secondary location
        if (locStr == null) {
            return null;
        }
        // Location string should be length 4 or it is invalid
        if (locStr.length != 4) {
            return null;
        }
        World world = Bukkit.getWorld(locStr[0]);
        if (world == null) {
            return null;
        }
        Integer xLoc;
        Integer yLoc;
        Integer zLoc;
        try {
            xLoc = Integer.valueOf(locStr[1]);
            yLoc = Integer.valueOf(locStr[2]);
            zLoc = Integer.valueOf(locStr[3]);
        } catch (NumberFormatException ex) {
            return null;
        }       
        return new Location(world, xLoc, yLoc, zLoc);
    }
}