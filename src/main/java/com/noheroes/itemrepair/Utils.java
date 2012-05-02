/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noheroes.itemrepair;

import org.bukkit.Material;

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
}