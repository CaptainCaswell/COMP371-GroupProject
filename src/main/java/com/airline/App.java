package com.airline;

import javax.swing.*;

public class App {
    public static void main( String[] args ) {
        Database database = Database.getInstance();
        
        // TODO Keep this? Button in UI?
        database.seed();

        SwingUtilities.invokeLater( () -> {
            new MainPanel();
        });
    }
}