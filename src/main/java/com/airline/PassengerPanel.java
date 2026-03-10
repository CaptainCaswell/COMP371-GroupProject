package com.airline;

import javax.swing.*;
import java.awt.BorderLayout;
import java.util.ArrayList;

public class PassengerPanel extends JPanel {
    private JComboBox<Passenger> passengerDropdown;
    private MyTicketsPanel myTicketsPanel;

    public PassengerPanel() {
        setLayout( new BorderLayout() );
        
        // Dropdown
        passengerDropdown = new JComboBox<>();
        loadPassengers();

        JTabbedPane tabs = new JTabbedPane();
        myTicketsPanel = new MyTicketsPanel();
        tabs.addTab( "My Tickets", myTicketsPanel );

        passengerDropdown.addActionListener( e -> {
            Passenger selected = ( Passenger ) passengerDropdown.getSelectedItem();
            if ( selected != null ) {
                myTicketsPanel.refresh( selected );
            }
        });

        add( passengerDropdown, BorderLayout.NORTH );
        add( tabs, BorderLayout.CENTER );

        if ( passengerDropdown.getItemCount() > 0 ) {
            myTicketsPanel.refresh( ( Passenger ) passengerDropdown.getSelectedItem() );
        }
    }

    private void loadPassengers() {
        ArrayList<Passenger> passengers = Passenger.getAll();
        for (Passenger passenger : passengers ) {
            passengerDropdown.addItem( passenger );
        }
    }
}