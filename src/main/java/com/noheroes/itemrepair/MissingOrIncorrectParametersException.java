/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noheroes.itemrepair;

/**
 *
 * @author PIETER
 */
public class MissingOrIncorrectParametersException extends Exception {
    private String message;
    
    public MissingOrIncorrectParametersException(String message) {
        this.message = message;
    }
    
    @Override
    public String getMessage() {
        return message;
    }
}