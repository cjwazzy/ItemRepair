/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noheroes.itemrepair;

import com.miniDC.Arguments;
import com.miniDC.Mini;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Location;

/**
 *
 * @author PIETER
 */
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
    
    public RepairStation getStation(String stationName) {
        return repairStations.get(stationName);
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
}
