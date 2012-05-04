/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noheroes.itemrepair;

/**
 *
 * @author PIETER
 */
public class Properties {
    
    private Properties () { };
    
    public final static String repairCostFileName = "repaircost.txt";
    public final static String locationFileName = "locations.mini";
    public final static String itemToCostSplitter = "=";
    public final static String costSplitter = ",";
    public final static String costAmountSplitter = ":";
    public final static String economyIdentifier = "ECON";
    public final static String expIdentifier = "XP";
    public final static String adminPermissions = "itemrepair.admin";
    public final static String userPermissions = "itemrepair.use";
    public static final int MAX_LEVEL = 1000;
    public static final int expTable[] = new int[MAX_LEVEL];
    // Set by config
    public static boolean useEconomy;
}
