/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noheroes.itemrepair;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author PIETER
 */
public class IRCommandExecutor implements CommandExecutor {
    private static ItemRepair ir;
    
    public IRCommandExecutor(ItemRepair ir) {
        this.ir = ir;
    }
    
    public boolean onCommand(CommandSender cs, Command command, String label, String[] args) {
        String com;
        if (args.length == 0) {
            com = "help";
        }
        else {
            com = args[0];
        }
        if (com.equalsIgnoreCase("create")) {
            if (!(cs instanceof Player)) {
                cs.sendMessage("You must be a player to use this command");
                return true;
            }
            if (!cs.hasPermission(Properties.adminPermissions) && !cs.isOp()) {
                cs.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                return true;
            }
            if (args.length < 2) {
                cs.sendMessage(ChatColor.RED + "You must specify a name for the station");
            }
            else {
                if (ir.getHandler().stationExists(args[1])) {
                    cs.sendMessage(ChatColor.RED + "A station with this name already exists");
                }
                else {
                    ir.addPlayerToEditMode((Player)cs, args[1]);
                }
            }
        }
        else if (com.equalsIgnoreCase("delete") || com.equalsIgnoreCase("remove")) {
            if (!cs.hasPermission(Properties.adminPermissions) && !cs.isOp()) {
                cs.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                return true;
            }
            if (args.length < 2) {
                cs.sendMessage(ChatColor.RED + "You must specify a name for the station");
            }
            else {
                try {
                    ir.getHandler().deleteStation(args[1]);
                } catch (MissingOrIncorrectParametersException ex) {
                    cs.sendMessage(ex.getMessage());
                }
            }
        }
        else if (com.equalsIgnoreCase("reload")) {
            if (!cs.hasPermission(Properties.adminPermissions) && !cs.isOp()) {
                cs.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                return true;
            }
            ir.reloadRepairFile();
            cs.sendMessage(ChatColor.YELLOW + "Repair file reloaded");
        }
        else if (com.equalsIgnoreCase("cost")) {
            if (!(cs instanceof Player)) {
                cs.sendMessage("You must be a player to use this command");
                return true;
            }
            if (!cs.hasPermission(Properties.userPermissions) && !cs.hasPermission(Properties.adminPermissions) && !cs.isOp()) {
                cs.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                return true;
            }
            RepairCost cost = ir.getHandler().getTotalRepaircost(((Player)cs).getItemInHand());
            if (cost == null) {
                cs.sendMessage(ChatColor.RED + "This item cannot be repaired");
            }
            else {
                cs.sendMessage(Utils.getCostString(cost));
            }
        }
        return true;
    }
}
