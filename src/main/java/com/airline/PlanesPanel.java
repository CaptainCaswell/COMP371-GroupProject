package com.airline;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.airline.Ticket.TicketType;

import java.awt.*;
import java.util.ArrayList;

public class PlanesPanel extends JPanel {
    private DefaultTableModel tableModel;
    private JTextField tailNumberField;
    private JTextField speedField;
    private JTextField firstClassField;
    private JTextField coachField;
    private JTextField economyField;

    // Constructor
    public PlanesPanel() {
        setLayout(new BorderLayout());

        add( buildFormPanel(), BorderLayout.NORTH );
        add( buildTablePanel(), BorderLayout.CENTER );

        refresh();
    }

    // Form for adding
    private JPanel buildFormPanel() {
        JPanel form = new JPanel( new GridLayout(2, 6, 5, 5 ));
        form.setBorder( BorderFactory.createEmptyBorder( 10,10,10,10 ) );

        // Labels
        form.add( new JLabel( "Tail Number" ) );
        form.add( new JLabel( "Speed" ) );
        form.add( new JLabel( "First Class Seats" ) );
        form.add( new JLabel( "Coach Seats" ) );
        form.add( new JLabel( "Economy Seats" ) );
        form.add( new JLabel( "" ) );

        // Fields
        tailNumberField = new JTextField();
        speedField = new JTextField();
        firstClassField = new JTextField();
        coachField = new JTextField();
        economyField = new JTextField();
        JButton addButton = new JButton( "Add Plane" );

        form.add( tailNumberField );
        form.add( speedField );
        form.add( firstClassField );
        form.add( coachField );
        form.add( economyField );
        form.add( addButton );

        addButton.addActionListener( e -> {
            // Check for empty fields
            if ( tailNumberField.getText().trim().isEmpty() ||
                    speedField.getText().trim().isEmpty() ||
                    firstClassField.getText().trim().isEmpty() ||
                    coachField.getText().trim().isEmpty() ||
                    economyField.getText().trim().isEmpty() ) {
                JOptionPane.showMessageDialog( this, "All fields must be filled in.", "Missing Fields", JOptionPane.WARNING_MESSAGE );
                return;
            }

            new Plane(
                tailNumberField.getText(),
                Integer.parseInt( speedField.getText() ),
                Integer.parseInt( firstClassField.getText() ),
                Integer.parseInt( coachField.getText() ),
                Integer.parseInt( economyField.getText() )
            );
            refresh();
        });

        return form;
    }

    // Display all current planes
    private JPanel buildTablePanel() {
        JPanel panel = new JPanel( new BorderLayout() );

        String[] columns = { "Tail Number", "Cruising Speed", "First Class Seats", "Coach Seats", "Economy Seats" };
        tableModel = new DefaultTableModel( columns, 0 );
        JTable table = new JTable( tableModel );

        JButton removeButton = new JButton( "Remove Selected" );
        removeButton.addActionListener( e -> {
            int selectedRow = table.getSelectedRow();
            if( selectedRow != -1 ) {
                String tailNumber = (String) tableModel.getValueAt( selectedRow, 0 );
                if ( Plane.remove( tailNumber ) ) {
                    JOptionPane.showMessageDialog( this, 
                        "Cannot remove plane - it is used in existing flights. Remove flights and try again.",
                        "Remove failed",
                        JOptionPane.ERROR_MESSAGE
                    );
                } else {
                    refresh();
                }
                
            }
        });

        panel.add( new JScrollPane(table), BorderLayout.CENTER );
        panel.add( removeButton, BorderLayout.SOUTH );
        return panel;
    }

    // Refresh
    private void refresh() {
        tableModel.setRowCount(0);
        ArrayList<Plane> planes = Plane.getAll();
        for ( Plane plane : planes ) {
            tableModel.addRow(new Object[]{
                plane.getID(),
                plane.getSpeed(),
                plane.getSeats( TicketType.FIRST ),
                plane.getSeats( TicketType.COACH ),
                plane.getSeats( TicketType.ECONOMY ),
            });
        }
    }
}