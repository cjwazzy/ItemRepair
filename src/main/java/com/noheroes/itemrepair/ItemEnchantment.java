/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noheroes.itemrepair;

/**
 *
 * @author PIETER
 */
public class ItemEnchantment {
    private String enchantName;
    private int enchantLevel;
    private boolean isValid;
    
    public ItemEnchantment(String enchantName, int enchantLevel) {
        this.enchantLevel = enchantLevel;
        this.enchantName = enchantName.toLowerCase().trim();
    }
    
    public ItemEnchantment(String enchantName, String enchantLevel) {
        this.enchantName = enchantName.toLowerCase().trim();
        try {
            this.enchantLevel = Integer.valueOf(enchantLevel);
        } catch (NumberFormatException ex) {
            this.isValid = false;
            return;
        }
        this.isValid = true;
    }
    
    public boolean isValid() {
        return this.isValid;
    }
    
    public String getName() {
        return this.enchantName;
    }
    
    public int getLevel() {
        return this.enchantLevel;
    }
        
    // hashCode() and equals() are needed for the HashMap to be able to match this object
    @Override
    public int hashCode() {
        return this.enchantLevel + this.enchantName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ItemEnchantment other = (ItemEnchantment) obj;
        if ((this.enchantName == null) ? (other.enchantName != null) : !this.enchantName.equals(other.enchantName)) {
            return false;
        }
        if (this.enchantLevel != other.enchantLevel) {
            return false;
        }
        return true;
    }
}
