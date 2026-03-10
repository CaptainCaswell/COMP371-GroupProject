package com.airline;

import javax.swing.*;

public class MainPanel extends JFrame {
    
    public MainPanel() {
        setTitle( "Airline Reservation System" );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        setSize( 800,600 );

        // Main Tab
        JTabbedPane mainTabs = new JTabbedPane();
        mainTabs.addTab( "Admin", new AdminPanel() );
        mainTabs.addTab( "Passenger", new PassengerPanel() );

        add( mainTabs );
        setLocationRelativeTo( null );
        setVisible( true );
    }
}
