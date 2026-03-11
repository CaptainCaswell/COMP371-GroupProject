package com.airline;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import com.github.lgooddatepicker.components.DateTimePicker;

public class FlightsPanel extends JPanel {
    private DefaultTableModel tableModel;
    private JComboBox<Route> routeDropdown;
    private JComboBox<Plane> planeDropdown;
    private DateTimePicker deptTimePicker;
    private JTextField firstClassCostField;
    private JTextField coachCostField;
    private JTextField economyCostField;
    private JTable table;

    // Constructor
    public FlightsPanel() {
        setLayout( new BorderLayout() );

        add( buildFormPanel(), BorderLayout.NORTH );
        add( buildTablePanel(), BorderLayout.CENTER );

        refresh();
    }

    // Form for adding
    private JPanel buildFormPanel() {
        JPanel form = new JPanel( new GridLayout( 2, 7, 5, 5 ) );
        form.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );

        // Labels
        form.add( new JLabel( "Route" ) );
        form.add( new JLabel( "Plane" ) );
        form.add( new JLabel( "Departure Time" ) );
        form.add( new JLabel( "First Class Cost" ) );
        form.add( new JLabel( "Coach Cost" ) );
        form.add( new JLabel( "Economy Cost" ) );
        form.add( new JLabel( "" ) );

        // Fields
        routeDropdown = new JComboBox<>();
        planeDropdown = new JComboBox<>();
        deptTimePicker = new DateTimePicker();
        firstClassCostField = new JTextField();
        coachCostField = new JTextField();
        economyCostField = new JTextField();
        JButton addButton = new JButton( "Add Flight" );

        // Populate dropdowns
        for ( Route route : Route.getAll() ) routeDropdown.addItem( route );
        for ( Plane plane : Plane.getAll() ) planeDropdown.addItem( plane );

        form.add( routeDropdown );
        form.add( planeDropdown );
        form.add( deptTimePicker );
        form.add( firstClassCostField );
        form.add( coachCostField );
        form.add( economyCostField );
        form.add( addButton );

        addButton.addActionListener( e -> {
            // Check for empty fields
            if ( routeDropdown.getSelectedItem() == null ||
                    planeDropdown.getSelectedItem() == null ||
                    deptTimePicker.getDateTimePermissive() == null ||
                    firstClassCostField.getText().trim().isEmpty() ||
                    coachCostField.getText().trim().isEmpty() ||
                    economyCostField.getText().trim().isEmpty() ) {
                JOptionPane.showMessageDialog( this, "All fields must be filled in.", "Missing Fields", JOptionPane.WARNING_MESSAGE );
                return;
            }

            new Flight(
                (Route) routeDropdown.getSelectedItem(),
                (Plane) planeDropdown.getSelectedItem(),
                deptTimePicker.getDateTimePermissive(),
                Float.parseFloat( firstClassCostField.getText().trim() ),
                Float.parseFloat( coachCostField.getText().trim() ),
                Float.parseFloat( economyCostField.getText().trim() )
            );
            refresh();
        });

        return form;
    }

    // Display all current flights
    private JPanel buildTablePanel() {
        JPanel panel = new JPanel( new BorderLayout() );

        String[] columns = { "Flight ID", "Route", "Plane", "Departure", "Arrival", "First Class", "Coach", "Economy" };
        tableModel = new DefaultTableModel( columns, 0 );
        table = new JTable( tableModel );

        JButton removeButton = new JButton( "Remove Selected" );
        removeButton.addActionListener( e -> {
            int selectedRow = table.getSelectedRow();
            if ( selectedRow != -1 ) {
                int flightID = (int) tableModel.getValueAt( selectedRow, 0 );
                if ( !Flight.remove( flightID ) ) {
                    JOptionPane.showMessageDialog( this,
                        "Cannot remove flight - it has existing tickets. Remove tickets and try again.",
                        "Remove Failed",
                        JOptionPane.ERROR_MESSAGE
                    );
                } else {
                    refresh();
                }
            }
        });

        panel.add( new JScrollPane( table ), BorderLayout.CENTER );
        panel.add( removeButton, BorderLayout.SOUTH );
        return panel;
    }

    // Refresh
    private void refresh() {
        tableModel.setRowCount( 0 );
        ArrayList<Flight> flights = Flight.getAll();
        for ( Flight flight : flights ) {
            tableModel.addRow( new Object[] {
                flight.getID(),
                flight.getRoute(),
                flight.getTailNumber(),
                flight.getDeptTime(),
                flight.getArriveTime(),
                flight.getPrice( Ticket.TicketType.FIRST ),
                flight.getPrice( Ticket.TicketType.COACH ),
                flight.getPrice( Ticket.TicketType.ECONOMY )
            });
        }
    }
}