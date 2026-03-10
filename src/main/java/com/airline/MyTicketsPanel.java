package com.airline;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.awt.BorderLayout;

public class MyTicketsPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;

    public MyTicketsPanel() {
        setLayout( new BorderLayout() );

        String[] columns = { "Route", "Departure", "Type", "Status", "Cost", "Days Until Flight", };
        tableModel = new DefaultTableModel( columns, 0 );
        table = new JTable( tableModel );

        add( new JScrollPane( table ), BorderLayout.CENTER );
    }

    public void refresh( Passenger passenger ) {
        // Clear
        tableModel.setRowCount( 0 );

        ArrayList<Ticket> tickets = Ticket.getForPassenger( passenger.getID() );

        for ( Ticket ticket : tickets ) {
            tableModel.addRow( new Object[] {
                ticket.getRoute(),
                ticket.getDeptTime(),
                ticket.getType(),
                ticket.getStatus(),
                ticket.getPrice(),
                ticket.daysTillFlight()
            });
        }
    }
}
