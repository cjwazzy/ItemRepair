/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noheroes.itemrepair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author PIETER
 */
public class ItemRepair extends JavaPlugin {
    public static Economy econ = null;
    
    private HashMap<Material, RepairCost> repairs = new HashMap<Material, RepairCost>();
    private HashMap<Player, EditMode> playerEditMode = new HashMap<Player, EditMode>();
    private IRListener listener;
    private RepairStationHandler stationHandler;
    private static ItemRepair instance;
    
    // ************  PUBLIC METHODS *************
    
    @Override 
    public void onDisable() {
        listener = null;
    }
    
    @Override
    public void onEnable() {
        instance = this;
        this.loadConfig(this.getConfig());
        if (Properties.useEconomy && !this.setupEconomy()) {
            this.log(Level.SEVERE, "Vault failed to hook into any economy plugin, disabled any recipes using economy.  If you do not use an economy plugin, disable UseEconomy in the config file");
        }
        getCommand("itemrepair").setExecutor(new IRCommandExecutor(this));
        listener = new IRListener(this);
        this.getServer().getPluginManager().registerEvents(listener, this);
        loadRepairFile();
        stationHandler = new RepairStationHandler(this, this.getDataFolder().getPath());
    }
    
    public static void log(String message) {
        instance.log(Level.INFO, message);
    }
    
    public static void log(Level level, String message) {
        instance.getLogger().log(level, message);
    }
    
    public void addPlayerToEditMode(Player player, String stationName) {
        if (this.playerEditMode.containsKey(player)) {
            return; // EX
        }
        EditMode editMode = new EditMode(player, stationName, stationHandler);
        playerEditMode.put(player, editMode);
    }
    
    public boolean leftClickEvent(Player player, Location loc) {
        if (!playerEditMode.containsKey(player)) {
            return false;
        }
        boolean done;
        done = playerEditMode.get(player).leftClickEvent(loc);
        if (done) {
            playerEditMode.remove(player);
        }
        return true;
    }
    
    public boolean rightClickEvent(Player player) {
        if (!playerEditMode.containsKey(player)) {
            return false;
        }
        playerEditMode.remove(player);
        player.sendMessage(ChatColor.YELLOW + "Cancelled adding repair station");
        return true;
    }
    
    public HashMap<Material, Integer> repairMatCheck(Inventory inv, Material mat) {
        if (!repairs.containsKey(mat)) {
            // TODO: Throw exception -- Item can't be repaired
            this.log("Can't repair");
            return null;
        }
        // Grab a clone of the total material cost so we can manipulate the values in it without changing the original
        HashMap<Material, Integer> totalCost = (HashMap<Material, Integer>)repairs.get(mat).getHash().clone();
        // Loop over each inventory slot to check for materials needed.  None of the bukkit contains methods can deal with items spread out over stacks
        for (ItemStack is : inv.getContents()) {
            // Skip empty slots
            if (is == null) {
                continue;
            }
            // Check if material is one of the ones needed to repair
            Material curMat = is.getType();
            if (totalCost.containsKey(curMat)) {
                Integer amount = totalCost.get(curMat);
                // If the stack is large enough to cover the total cost, remove it from hashmap, otherwise remove as many items as are available from the amount stored in hashmap
                if (is.getAmount() >= amount) {
                    totalCost.remove(curMat);
                }
                else {
                    totalCost.put(curMat, (amount - is.getAmount()));
                }
            }
        }
        // Return null if hashmap is empty (player has all materials needed) or hashmap with missing materials otherwise
        return (totalCost.isEmpty() ? null : totalCost);
    }
    
    public boolean playerEconCheck(Player player, Integer price) {
        // No cost associated with recipe, automatically return true
        if (price == null) {
            return true;
        }
        if (player == null) {
            return false;
        }
        // Economy is not loaded, recipes with a cost do not work
        if (!this.isEconEnabled()) {
            return false;
        }
        return (econ.getBalance(player.getName()) >= price);
    }
    
    public boolean isEconEnabled() {
        return (econ != null);
    }
    
    public RepairStationHandler getHandler() {
        return stationHandler;
    }
    
    // ************* PRIVATE METHODS *****************
    
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        econ = rsp.getProvider();
        return econ != null;
    }
    
    private void loadConfig(FileConfiguration config) {
        config.options().copyDefaults(true);
        Properties.useEconomy = config.getBoolean("UseEconomy");
        this.saveConfig();
    }
    
    private boolean loadRepairFile() {
        File repairFile = new File(this.getDataFolder(), Properties.repairCostFileName);
        if (!repairFile.exists()) {
            try {
                repairFile.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                return false;
            }
        }
        List<String> strList;
        strList = readFile(repairFile);
        if (strList == null) {
            return false;
        }
        int lineCount = 0;
        for (String line : strList) {
            lineCount++;
            // Ignore comments
            if (line.charAt(0) == '#') {
                continue;
            }
            // Split line into item and cost
            String[] splitLine = line.split(Properties.itemToCostSplitter);
            // We should get 2 lines out of this, if not the line was not in the correct format
            if (splitLine.length != 2) {
                this.log(Level.WARNING, "Error reading line #" + lineCount + " from file, skipping...");
                continue;
            }
            RepairCost rc = new RepairCost(splitLine[1].trim(), lineCount);
            Material mat = Material.getMaterial(splitLine[0].trim().toUpperCase());
            if (mat == null) {
                this.log(Level.WARNING, "Error reading line #" + lineCount + " material " + splitLine[0].trim() + " does not exist, skipping...");
                continue;
            }
            repairs.put(mat, rc);
        }
        return true;
    }
    
    private List<String> readFile(File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            List<String> strList = new ArrayList<String>();
            line = reader.readLine();
            while ( line != null) {
                strList.add(line.trim());
                line = reader.readLine();
            }
            return (strList.isEmpty() ? null : strList);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
