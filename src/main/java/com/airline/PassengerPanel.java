package com.airline;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;

public class PassengerPanel extends JPanel {
    private JComboBox<Passenger> passengerDropdown;
    private MyTicketsPanel myTicketsPanel;
    private BookFlightPanel bookFlightPanel;

    public PassengerPanel() {
        setLayout( new BorderLayout() );
        
        // Dropdown
        passengerDropdown = new JComboBox<>();
        refreshPassengers();

        JButton addPassengerButton = new JButton( "New Passenger" );
        addPassengerButton.addActionListener( e -> {
            String name = JOptionPane.showInputDialog( this, "Enter Passenger name:", "New Passenger", JOptionPane.PLAIN_MESSAGE );
            if ( name != null && !name.trim().isEmpty() ) {
                new Passenger( name.trim(), 0 );
                refreshPassengers();
            }
        });

        JButton addBalanceButton = new JButton( "Add balance" );
        addBalanceButton.addActionListener( e -> {
            String input = JOptionPane.showInputDialog( this, "Enter amount to increase balance:", "Passenger Balance", JOptionPane.PLAIN_MESSAGE );

            if ( input != null && !input.trim().isEmpty() ) {
                Passenger selected = (Passenger) passengerDropdown.getSelectedItem();
                if( selected != null ) {
                    selected.updateMoney( Float.parseFloat( input.trim() ) );
                    refreshPassengers();
                }
            }
        });

        JPanel topPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
        topPanel.add( passengerDropdown );
        topPanel.add( addPassengerButton );
        topPanel.add( addBalanceButton );

        // Get initial Passenger
        Passenger initial = (Passenger) passengerDropdown.getSelectedItem();

        JTabbedPane tabs = new JTabbedPane();
        myTicketsPanel = new MyTicketsPanel( this );
        bookFlightPanel = new BookFlightPanel( initial );
        tabs.addTab( "My Tickets", myTicketsPanel );
        tabs.addTab( "Book Flights", bookFlightPanel );

        // Refresh pages when changing tabs
        tabs.addChangeListener( e-> {
            refreshPassengers();
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

        add( topPanel, BorderLayout.NORTH );
        add( tabs, BorderLayout.CENTER );

        if ( passengerDropdown.getItemCount() > 0 ) {
            myTicketsPanel.refresh( ( Passenger ) passengerDropdown.getSelectedItem() );
        }
    }

    public void refreshPassengers() {
        // Save current selection
        Passenger selected = (Passenger) passengerDropdown.getSelectedItem();

        // Clear and reload
        passengerDropdown.removeAllItems();
        for (Passenger passenger : Passenger.getAll()) {
            passengerDropdown.addItem( passenger );
        }

        // Check if there was a selection
        if (selected != null) {
            // Restore previous selection
            for (int i = 0; i < passengerDropdown.getItemCount(); i++) {
                if (passengerDropdown.getItemAt(i).getID() == selected.getID()) {
                    passengerDropdown.setSelectedIndex(i);
                    break;
                }
            }
        }
    }
}