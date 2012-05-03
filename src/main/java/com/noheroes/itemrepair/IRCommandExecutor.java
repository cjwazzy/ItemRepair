/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noheroes.itemrepair;

import java.awt.Color;
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
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to use this command");
            return true;
        }
        // We hide this command entirely if the user does not have permission
        if (!sender.hasPermission(Properties.adminPermissions) && !sender.isOp()) {
            sender.sendMessage("Unknown command. Type \"help\" for help.");
            return true;
        }
        String com;
        if (args.length == 0) {
            com = "help";
        }
        else {
            com = args[0];
        }
        if (com.equalsIgnoreCase("create")) {
            if (args.length < 2) {
                sender.sendMessage(Color.RED + "You must specify a name for the station");
            }
            else {
                ir.addPlayerToEditMode((Player)sender, args[1]);
            }
        }
        return true;
    }
}
