/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noheroes.itemrepair;

import com.miniDC.Arguments;
import com.miniDC.Mini;
import java.util.HashMap;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author PIETER
 */

// ************************** TODO: CHARGE EXP/MONEY ********************************
public class RepairStationHandler {
    private ItemRepair ir;
    private HashMap<String, RepairStation> repairStations = new HashMap<String, RepairStation>();
    private HashMap<Location, String> buttonLocToName = new HashMap<Location, String>();
    private HashMap<Location, String> dispenserLocToName = new HashMap<Location, String>();
    private Mini miniDb;
    // Mini db keys
    private static final String buttonLocKey = "buttonloc";
    private static final String dispenserLocKey = "dispenserloc";
    
    public RepairStationHandler(ItemRepair ir, String folder) {
        this.ir = ir;
        miniDb = new Mini(folder, Properties.locationFileName);
        this.loadStations();
    }
    
    public void createStation(Location buttonLoc, Location dispenserLoc, String stationName) {
        if (stationName == null) {
            return; //  TODO: EXCEPTION
        }
        if (repairStations.containsKey(stationName)) {
            return; //  TODO: EXCEPTION
        }
        RepairStation station = new RepairStation(buttonLoc, dispenserLoc, stationName);
        repairStations.put(stationName, station);
        buttonLocToName.put(buttonLoc, stationName);
        dispenserLocToName.put(dispenserLoc, stationName);
        this.saveStation(station);
    }
    
    public void deleteStation(String stationName) {
        if (stationName == null) {
            return;  // EX
        }
        if (!repairStations.containsKey(stationName)) {
            return; // EX
        }
        buttonLocToName.remove(this.getStation(stationName).getButtonLoc());
        dispenserLocToName.remove(this.getStation(stationName).getDispenserLoc());
        repairStations.remove(stationName);
        this.removeStation(stationName);
    }
    
    public boolean clickEvent(Player player, Location loc) {
        // If the button is not part of a repair station, ignore the event
        if (!buttonLocToName.containsKey(loc)) {
            return false;
        }
        boolean success = true;
        RepairStation rs = this.getStationFromButton(loc);
        Location dispenserLoc = rs.getDispenserLoc();
        // No dispenser at dispenser location
        if (!dispenserLoc.getBlock().getType().equals(Material.DISPENSER)) {
            ir.log(Level.WARNING, "Error - Dispenser does not exist at location " + dispenserLoc.toString() + " removing station");
            this.deleteStation(rs.getName());
        }
        // Grab dispenser inventory and try to find a repairable item inside
        Inventory inv = ((Dispenser)dispenserLoc.getBlock().getState()).getInventory();
        ItemStack is = this.findRepairItem(inv);
        // No repairable item found
        if (is == null) {
            return true; // EXCEPTION
        }
        // Display repair cost
        player.sendMessage(ChatColor.AQUA + "The cost to repair " + ChatColor.GREEN + is.getType().toString() + ChatColor.AQUA + " is:");
        player.sendMessage(Utils.getCostString(ir.getRepairCost(is.getType())));
        // Player balance calculation
        Double cost = this.playerEconCheck(player, is.getType());
        // Spare/missing items check
        HashMap<Material, Integer> leftover = this.repairMatCheck(inv, is);
        // Player exp calculation
        Integer expMissing = this.playerExpCheck(player, is.getType());
        // Incorrect materials
        if (leftover != null) {
            //player.sendMessage(ChatColor.YELLOW + "There are " + ChatColor.RED + "missing" + ChatColor.YELLOW + " or " + ChatColor.GREEN + "extra" + ChatColor.YELLOW + " items in the dispenser");
            String missMsg = ChatColor.RED + "Missing: ";
            String extraMsg = ChatColor.GREEN + "Extra: ";
            for (Material mat : leftover.keySet()) {
                Integer amount = leftover.get(mat);
                if (amount > 0) {
                    extraMsg += ChatColor.GREEN + amount.toString() + ChatColor.YELLOW + "x" + mat.toString() + ",";
                }
                else {
                    missMsg += ChatColor.RED + String.valueOf(-amount) + ChatColor.YELLOW + "x" + mat.toString() + ",";
                }
                
            }
            if (missMsg.length() > 14) {
                player.sendMessage(missMsg);
            }
            if (extraMsg.length() > 12) {
                player.sendMessage(extraMsg);
            }
            success = false;
        }
        // Player balance too low
        if (cost != 0d) {
            player.sendMessage(ChatColor.AQUA + "You cannot afford this repair, you are missing " + ChatColor.RED + 
                    cost + ChatColor.AQUA + " " + Properties.currencyName);
            success = false;
        }
        // Player does not have enough exp
        if (expMissing != 0) {
            player.sendMessage(ChatColor.AQUA + "You are missing " + ChatColor.RED + expMissing + ChatColor.AQUA + " exp for this repair");
            success = false;
        }
        if (success) {
            //this.clearInventory(dispenserLoc.getBlock().getState(), is);
            ItemStack copy = is.clone();
            Dispenser disp = (Dispenser)dispenserLoc.getBlock().getState();
            disp.getInventory().clear();
            Short maxDura = 0;
            copy.setDurability(maxDura);
            disp.getInventory().addItem(copy);
            disp.update(true);
            player.sendMessage(ChatColor.AQUA + "Your " + is.getType().toString() + " has been repaied");
            return false;
        }
        return true;
    }
    
    public RepairStation getStation(String stationName) {
        return (stationName == null) ? null : repairStations.get(stationName);
    }
    
    public RepairStation getStationFromButton(Location buttonLoc) {
        return (buttonLoc == null) ? null : getStation(buttonLocToName.get(buttonLoc));
    }
    
    private void saveStation(RepairStation station) {
        Arguments arg = new Arguments(station.getName());
        arg.setValue(buttonLocKey, Utils.locToString(station.getButtonLoc()));
        arg.setValue(dispenserLocKey, Utils.locToString(station.getDispenserLoc()));
        miniDb.addIndex(station.getName(), arg);
        miniDb.update();
    }
    
    private void removeStation(String stationName) {
        miniDb.removeIndex(stationName);
        miniDb.update();
    }
    
    private ItemStack findRepairItem(Inventory inv) {
        Material mat;
        for (ItemStack is : inv.getContents()) {
            if (is == null) {
                continue;
            }
            mat = is.getType();
            // Return first repairable item found
            if (ir.getRepairCost(mat) != null) {
                return is;
            }
        }
        return null;
    }
    
    private HashMap<Material, Integer> repairMatCheck(Inventory inv, ItemStack repairIs) {
        Material mat = repairIs.getType();
        RepairCost rc = ir.getRepairCost(mat);
        if (rc == null) {
            // TODO: Throw exception -- Item can't be repaired
            ir.log("Can't repair");
            return null;
        }
        // Grab a clone of the total material cost so we can manipulate the values in it without changing the original
        HashMap<Material, Integer> totalCost = rc.getHashMapCopy();
        HashMap<Material, Integer> extraMats = new HashMap<Material, Integer>();
        // Loop over each inventory slot to check for materials needed.  None of the bukkit contains methods can deal with items spread out over stacks
        for (ItemStack is : inv.getContents()) {
            // Skip empty slots
            if (is == null) {
                continue;
            }
            // Skip slot of item being repaired
            if ((is.getType() == repairIs.getType()) && (is.getDurability() == repairIs.getDurability())) {
                continue;
            }
            // Check if material is one of the ones needed to repair
            Material curMat = is.getType();
            if (totalCost.containsKey(curMat)) {
                Integer amount = totalCost.get(curMat);
                // If the stack is large enough to cover the total cost, remove it from hashmap
                if (is.getAmount() == amount) {
                    totalCost.remove(curMat);
                }
                // Not enough to fully cover cost, remove the amount covered from the cost
                else if (is.getAmount() < amount) {
                    totalCost.put(curMat, (amount - is.getAmount()));
                }
                // More materials than cost required present
                else {
                    totalCost.remove(curMat);
                    // Calculate leftover materials and add them to extraMats
                    Integer leftover = is.getAmount() - amount;
                    if (extraMats.containsKey(curMat)) {
                        leftover += extraMats.get(curMat);
                    }
                    extraMats.put(curMat, leftover);
                }
            }
            // Material isn't needed, add it to extra mats
            else {
                Integer leftover;
                leftover = extraMats.get(curMat);
                if (leftover == null) {
                    leftover = 0;
                }
                leftover += is.getAmount();
                extraMats.put(curMat, leftover);
            }
        }
        // Add any missing materials to hashmap
        if ((totalCost != null) && (!totalCost.isEmpty())) {
            for (Material curMat : totalCost.keySet()) {
                extraMats.put(curMat, -totalCost.get(curMat));
            }
        }
        // Return null if hashmap is empty (player has all materials needed) or hashmap with missing materials otherwise
        return (extraMats.isEmpty() ? null : extraMats);
    }
    
    private Double playerEconCheck(Player player, Material mat) {
        Integer price = ir.getRepairCost(mat).getEconCost();
        // No cost associated with recipe, automatically return true
        if (price == null) {
            return 0d;
        }
        // Economy is not loaded, recipes with a cost do not work
        if (!ir.isEconEnabled()) {
            return 123d; // Exception
        }
        Double balance = ir.econ.getBalance(player.getName());
        return (balance >= price) ? 0d : (price - balance);
    }
    
    private Integer playerExpCheck(Player player, Material mat) {
        Integer expCost = ir.getRepairCost(mat).getExpCost();
        Integer playerExp = Utils.getTotalExp(player);
        return ((playerExp >= expCost) ? 0 : (expCost - playerExp));
    }
    
    private void loadStations() {
        HashMap<String, Arguments> stations = miniDb.getIndices();
        for (Arguments arg : stations.values()) {
            RepairStation rs = this.argToStation(arg);
            if (rs != null) {
                repairStations.put(rs.getName(), rs);
                buttonLocToName.put(rs.getButtonLoc(), rs.getName());
                dispenserLocToName.put(rs.getDispenserLoc(), rs.getName());
            }
        }
    }
    
    private RepairStation argToStation(Arguments arg) {
        Location buttonLoc = Utils.stringToLoc(arg.getArray(buttonLocKey));
        Location dispenserLoc = Utils.stringToLoc(arg.getArray(dispenserLocKey));
        String stationName = arg.getKey();
        if (stationName == null) {
            return null;
        }
        if ((dispenserLoc == null) || (buttonLoc == null)) {
            this.removeStation(stationName);
            ir.log(Level.WARNING, "Error loading station file, failed to read location, removing this station...");
            return null;
        }
        if (!dispenserLoc.getBlock().getType().equals(Material.DISPENSER) 
                || !buttonLoc.getBlock().getType().equals(Material.STONE_BUTTON)) {
            this.removeStation(stationName);
            ir.log(Level.WARNING, "Error loading station file, button or dispenser from station missing, removing this station...");
            return null;
        }
        return new RepairStation(buttonLoc, dispenserLoc, stationName);
    }
}
