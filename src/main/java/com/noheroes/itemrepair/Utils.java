/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noheroes.itemrepair;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 *
 * @author PIETER
 */
public class Utils {

    private Utils () {};
    
    public enum ReadType {
        MATERIAL, ENCHANT
    }
    
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
    
    public RepairCost addCosts(RepairCost rc1, RepairCost rc2) {
        return addCosts(rc1, rc2, 1);
    }
    
    public static RepairCost addCosts(RepairCost rc1, RepairCost rc2, Integer multiplier) {
        Integer finalEconCost = rc1.getEconCost() + (rc2.getEconCost() * multiplier);
        Integer finalExpCost = rc1.getExpCost() + (rc2.getExpCost() * multiplier);
        HashMap<Material, Integer> addMap = rc1.getHashMap();
        HashMap<Material, Integer> finalMap = rc2.getHashMapCopy();
        for (Material mat : addMap.keySet()) {
            Integer amount = addMap.get(mat);
            if (finalMap.get(mat) != null) {
                amount += finalMap.get(mat);
            }
            finalMap.put(mat, amount);
        }
        return new RepairCost(finalEconCost, finalExpCost, rc1.getMultiplier(), finalMap);
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
    
    public static String getCostString(RepairCost rc) {
        String msg = "";
        Integer amount;
        if (!rc.getHashMap().isEmpty()) {
            msg += ChatColor.AQUA + "Materials: ";
            for (Material mat : rc.getHashMap().keySet()) {
                amount = rc.getHashMap().get(mat);
                msg += "" + ChatColor.GREEN + amount + ChatColor.YELLOW + " " + MaterialNames.getItemName(mat.getId()) + ",";
            }
        }
        if (rc.getEconCost() != 0) {
            msg += " " + ChatColor.AQUA + ItemRepair.econ.currencyNamePlural() + ":" + ChatColor.YELLOW + rc.getEconCost() + ",";
        }
        if (rc.getExpCost() != 0) {
            msg += ChatColor.AQUA + " Exp:" + ChatColor.YELLOW + rc.getExpCost();
        }
        return msg;
    }
    
    public static int getTotalExp(Player player) {
        if (player == null)
            return 0;
        
        int level = player.getLevel();
        if (level >= Properties.MAX_LEVEL) {
            ItemRepair.log("Level overflow.  Player " + player.getName() + " is level " + level + " while max supported is " + Properties.MAX_LEVEL);
            return 0;
        }
        int currentLevelProgress = Math.round(player.getExp() * (Properties.expTable[level + 1] - Properties.expTable[level])); // Amount of exp earned in current level
        
        return Properties.expTable[level] + currentLevelProgress;
    }
    
    public static void resetExp(Player player){
        if(player == null)
            return;
        player.setTotalExperience(0);
        player.setLevel(0);
        player.setExp(0);
    }
}