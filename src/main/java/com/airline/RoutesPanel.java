package com.airline;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class RoutesPanel extends JPanel {
    private DefaultTableModel tableModel;
    private JTextField fromField;
    private JTextField toField;
    private JTextField distanceField;
    private JTable table;

    public RoutesPanel() {
        setLayout( new BorderLayout() );

        add( buildFormPanel(), BorderLayout.NORTH );
        add( buildTablePanel(), BorderLayout.CENTER );

        refresh();
    }

    // Form for adding
    private JPanel buildFormPanel() {
        JPanel form = new JPanel( new GridLayout( 2, 4, 5, 5 ) );
        form.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );

        // Labels
        form.add( new JLabel( "From Airport" ) );
        form.add( new JLabel( "To Airport" ) );
        form.add( new JLabel( "Distance (km)" ) );
        form.add( new JLabel( "" ) );

        // Fields
        fromField = new JTextField();
        toField = new JTextField();
        distanceField = new JTextField();
        JButton addButton = new JButton( "Add Route" );

        form.add( fromField );
        form.add( toField );
        form.add( distanceField );
        form.add( addButton );

        addButton.addActionListener( e -> {
            // Check for empty fields
            if ( fromField.getText().trim().isEmpty() ||
                    toField.getText().trim().isEmpty() ||
                    distanceField.getText().trim().isEmpty() ) {
                JOptionPane.showMessageDialog( this, "All fields must be filled in.", "Missing Fields", JOptionPane.WARNING_MESSAGE );
                return;
            }

            new Route(
                fromField.getText().trim().toUpperCase(),
                toField.getText().trim().toUpperCase(),
                Integer.parseInt( distanceField.getText().trim() )
            );
            refresh();
        });

        return form;
    }

    // Display all current routes
    private JPanel buildTablePanel() {
        JPanel panel = new JPanel( new BorderLayout() );

        String[] columns = { "Route ID", "From", "To", "Distance (km)" };
        tableModel = new DefaultTableModel( columns, 0 );
        table = new JTable( tableModel );

        JButton removeButton = new JButton( "Remove Selected" );
        removeButton.addActionListener( e -> {
            int selectedRow = table.getSelectedRow();
            if ( selectedRow != -1 ) {
                int routeID = (int) tableModel.getValueAt( selectedRow, 0 );
                if ( !Route.remove( routeID ) ) {
                    JOptionPane.showMessageDialog( this,
                        "Cannot remove route - it is used in existing flights. Remove flights and try again.",
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
        ArrayList<Route> routes = Route.getAll();
        for ( Route route : routes ) {
            tableModel.addRow( new Object[] {
                route.getID(),
                route.getFromAirport(),
                route.getToAirport(),
                route.getDistance()
            });
        }
    }
}