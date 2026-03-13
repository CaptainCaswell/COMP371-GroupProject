package com.airline;

import javax.swing.*;

public class App {
    public static void main( String[] args ) {
        Database database = Database.getInstance();

        database.seed(); // TODO Remove? Put in UI?

        SwingUtilities.invokeLater( () -> {
            new MainPanel();
        });
    }
}