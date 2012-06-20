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
    private Integer multiplier;
    private boolean isValid;
    // If this flag is set this holds the full cost for a single unique item/enchant combo
    private boolean uniqueCost;
    
   
    public RepairCost(String price, Integer lineNr, boolean uniqueCost) {
        materialCost = new HashMap<Material, Integer>();
        economyCost = 0;
        expCost = 0;
        multiplier = 1;
        isValid = true;
        this.uniqueCost = uniqueCost;
        setCostFromString(price, lineNr);
    }
    
    public RepairCost(String price, Integer lineNr) {
        this(price, lineNr, false);
    }
    
    public RepairCost(Integer economyCost, Integer expCost, Integer multiplier, HashMap<Material, Integer> materialCost) {
        this.economyCost = economyCost;
        this.expCost = expCost;
        this.materialCost = materialCost;
        this.multiplier = multiplier;
        isValid = true;
        uniqueCost = false;
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
    
    public Integer getMultiplier() {
        return multiplier;
    }
    
    public void applyMultiplier(Integer mult) {
        economyCost *= mult;
        expCost *= mult;
    }
    
    public boolean isValid() {
        return this.isValid;
    }
    
    public void setUniqueCost(boolean value) {
        this.uniqueCost = value;
    }
    
    public boolean isUniqueCost() {
        return this.uniqueCost;
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
                if (!ItemRepair.getInstance().isEconEnabled() && amount > 0) {
                    isValid = false;
                    return;
                }
                economyCost = amount;
            }
            else if (priceStrArray[0].trim().equals(Properties.expIdentifier)) {
                expCost = amount;
            }
            else if (priceStrArray[0].trim().equals(Properties.multiplierIdentifier)) {
                multiplier = amount;
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
