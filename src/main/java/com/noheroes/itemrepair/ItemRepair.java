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
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author PIETER
 */
public class ItemRepair extends JavaPlugin {
    
    private HashMap<String, RepairCost> repairs = new HashMap<String, RepairCost>();
    private static ItemRepair instance;
    
    @Override 
    public void onDisable() {
        
    }
    
    @Override
    public void onEnable() {
        instance = this;
        loadRepairFile();
    }
    
    public static void log(String message) {
        instance.log(Level.INFO, message);
    }
    
    public static void log(Level level, String message) {
        instance.getLogger().log(level, message);
    }
    
    private void loadConfig(FileConfiguration config) {
        config.options().copyDefaults(true);
        
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
            HashMap<Material, Integer> map = rc.getHash();
            for (Material mat : map.keySet()) {
                this.log(mat.toString() + ":" + map.get(mat).toString());
            }
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
