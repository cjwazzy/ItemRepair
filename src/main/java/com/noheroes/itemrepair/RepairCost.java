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
    private Integer economyCost;
    private Integer expCost;
    private HashMap<Material, Integer> materialCost;
    
    public RepairCost(String price, Integer lineNr) {
        materialCost = new HashMap<Material, Integer>();
        economyCost = 0;
        expCost = 0;
        setCostFromString(price, lineNr);
    }
    
    public HashMap<Material, Integer> getHashMapCopy() {
        HashMap<Material, Integer> hashCopy = (HashMap<Material, Integer>)materialCost.clone();
        return hashCopy;
    }
    
    public HashMap<Material, Integer> getHashMap() {
        return materialCost;
    }
    
    public Integer getEconCost() {
        return economyCost;
    }
    
    public Integer getExpCost() {
        return expCost;
    }
    
    private void setCostFromString(String price, Integer lineNr) {
        String[] priceArray;
        // Split the line into each individual material:amount groupings and loop through them
        priceArray = price.split(Properties.costSplitter);
        for (String priceStr : priceArray) {
            // Split material and amount from each other
            String[] priceStrArray = priceStr.trim().split(Properties.costAmountSplitter);
            // We should get 2 results, one for the material, and one for the amount
            if (priceStrArray.length != 2) {
                ItemRepair.log(Level.WARNING, "Error reading material from line #" + lineNr + ", please ensure the cost is formatted as material" + Properties.costAmountSplitter + "amount, skipping this material...");
                continue;
            }
            // Grab amount from string and verify it is a correct number
            Integer amount = Utils.getIntFromString(priceStrArray[1].trim());
            if ((amount == null) || (amount <= 0)) {
                ItemRepair.log(Level.WARNING, "Error reading material from line #" + lineNr + ", amount is not a valid number, skipping...");
                continue;
            }
            // Check if material listed is the economy identifier
            if (priceStrArray[0].trim().equals(Properties.economyIdentifier)) {
                economyCost = amount;
            }
            else if (priceStrArray[0].trim().equals(Properties.expIdentifier)) {
                expCost = amount;
            }
            else {
                // Grab material from string and verify it exists
                Material mat = Utils.getMaterialFromString(priceStrArray[0].trim());
                if (mat == null) {
                    ItemRepair.log(Level.WARNING, "Error reading material from line #" + lineNr + ", material " + priceStrArray[0] + " does not exist, skipping this material...");
                    continue;
                }
                // Add material to hashmap, display a warning if it was already added once
                if (materialCost.put(mat, amount) != null) {
                    ItemRepair.log(Level.WARNING, "Material " + mat.toString() + " listed twice on line #" + lineNr + ", second value replaced first one...");
                }     
            }
        }
    }
}
