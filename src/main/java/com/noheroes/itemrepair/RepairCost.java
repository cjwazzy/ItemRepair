/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noheroes.itemrepair;

import java.util.HashMap;
import java.util.logging.Level;
import org.bukkit.Material;

/**
 *
 * @author PIETER
 */
public class RepairCost {
    private float economyCost;
    private HashMap<Material, Integer> materialCost;
    private Integer lineNr;
    
    public RepairCost(String price, Integer lineNr) {
        this.lineNr = lineNr;
        materialCost = new HashMap<Material, Integer>();
        setCostFromString(price);
    }
    
    public HashMap<Material, Integer> getHash() {
        return materialCost;
    }
    
    private void setCostFromString(String price) {
        String[] priceArray;
        priceArray = price.split(Properties.costSplitter);
        for (String priceStr : priceArray) {
            String[] priceStrArray = priceStr.trim().split(Properties.costAmountSplitter);
            if (priceStrArray.length != 2) {
                ItemRepair.log(Level.WARNING, "Error reading material from line #" + lineNr + ", please ensure the cost is formatted as material" + Properties.costAmountSplitter + "amount, skipping this material...");
                continue;
            }
            Material mat = this.getMaterialFromString(priceStrArray[0].trim());
            Integer amount = this.getIntFromString(priceStrArray[1].trim());
            if (mat == null) {
                ItemRepair.log(Level.WARNING, "Error reading material from line #" + lineNr + ", material " + priceStrArray[0] + " does not exist, skipping this material...");
                continue;
            }
            if ((amount == null) || (amount <= 0)) {
                ItemRepair.log(Level.WARNING, "Error reading material from line #" + lineNr + ", amount is not a valid number, skipping...");
                continue;
            }
            if (materialCost.put(mat, amount) != null) {
                ItemRepair.log(Level.WARNING, "Material " + mat.toString() + " listed twice on line #" + lineNr + ", second value replaced first one...");
            }          
        }
    }
    
    private Material getMaterialFromString(String mat) {
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
    
    private Integer getIntFromString(String intStr) {
        Integer returnInt;
        try {
            returnInt = Integer.valueOf(intStr);
        } catch (NumberFormatException ex) {
            returnInt = null;
        }
        return returnInt;
    }
}
