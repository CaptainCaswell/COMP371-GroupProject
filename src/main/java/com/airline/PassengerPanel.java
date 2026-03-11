package com.airline;

import javax.swing.*;
import java.awt.BorderLayout;
import java.util.ArrayList;

public class PassengerPanel extends JPanel {
    private JComboBox<Passenger> passengerDropdown;
    private MyTicketsPanel myTicketsPanel;
    private BookFlightPanel bookFlightPanel;

    public PassengerPanel() {
        setLayout( new BorderLayout() );
        
        // Dropdown
        passengerDropdown = new JComboBox<>();
        loadPassengers();

        // Get initial Passenger
        Passenger initial = (Passenger) passengerDropdown.getSelectedItem();

        JTabbedPane tabs = new JTabbedPane();
        myTicketsPanel = new MyTicketsPanel();
        bookFlightPanel = new BookFlightPanel( initial );
        tabs.addTab( "My Tickets", myTicketsPanel );
        tabs.addTab( "Book Flights", bookFlightPanel );

        // Refresh pages when changing tabs
        tabs.addChangeListener( e-> {
            Passenger selected = (Passenger) passengerDropdown.getSelectedItem();
            if ( selected != null ) {
                myTicketsPanel.refresh( selected );
                bookFlightPanel.refresh();
            }
        });

        passengerDropdown.addActionListener( e -> {
            Passenger selected = ( Passenger ) passengerDropdown.getSelectedItem();
            if ( selected != null ) {
                myTicketsPanel.refresh( selected );
                bookFlightPanel.setPassenger( selected );
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