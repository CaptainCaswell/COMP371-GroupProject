package com.airline;

import javax.swing.*;

public class App {
    public static void main( String[] args ) {
        Database database = Database.getInstance();
        
        // TODO Keep automatic seed? Button in UI?
        database.seed();

        SwingUtilities.invokeLater( () -> {
            new MainPanel();
        });
    }
}