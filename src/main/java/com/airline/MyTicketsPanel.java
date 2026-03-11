package com.airline;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

public class MyTicketsPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private Passenger currentPassenger;
    private PassengerPanel passengerPanel;

    public MyTicketsPanel( PassengerPanel passengerPanel) {
        this.passengerPanel = passengerPanel;
        setLayout( new BorderLayout() );

        add( buildTablePanel(), BorderLayout.CENTER );
        add( buildButtonPanel(), BorderLayout.SOUTH );
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout() );

        String[] columns = { "ticketID", "Route", "Departure", "Type", "Status", "Cost", "Days Until Flight", };
        tableModel = new DefaultTableModel( columns, 0 );
        table = new JTable( tableModel );
        
        // Hide ticketID
        table.getColumnModel().getColumn( 0 ).setMinWidth(0 );
        table.getColumnModel().getColumn( 0 ).setMaxWidth(0 );

        panel.add( new JScrollPane( table ), BorderLayout.CENTER );

        return panel;
    }

    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel( new FlowLayout() );

        JButton confirmButton = new JButton( "Confirm Selected" );
        JButton cancelButton = new JButton( "Cancel Selected" );

        confirmButton.addActionListener( e -> confirmTicket() );
        cancelButton.addActionListener( e -> cancelTicket() );

        panel.add( confirmButton );
        panel.add( cancelButton );

        return panel;
    }

    private void confirmTicket() {
        int selectedRow = table.getSelectedRow();

        if ( selectedRow == -1 ) {
            JOptionPane.showMessageDialog( this, "Please select a ticket.", "No Ticket Selected", JOptionPane.WARNING_MESSAGE );
            return;
        }

        int ticketID = (int) tableModel.getValueAt( selectedRow, 0 );

        Ticket ticket = Ticket.getByID( ticketID );

        String error = ticket.confirm();

        if ( error == null ) {
            JOptionPane.showMessageDialog( this, "Selected Ticket has been paid and is now confirmed.", "Ticket Confirmed", JOptionPane.INFORMATION_MESSAGE );
            refresh( currentPassenger );
            passengerPanel.refreshPassengers();
        } else {
            JOptionPane.showMessageDialog( this, error, "Ticket not confirmed", JOptionPane.WARNING_MESSAGE );
        }
    }

    private void cancelTicket() {
    int selectedRow = table.getSelectedRow();
    int ticketID = (int) tableModel.getValueAt( selectedRow, 0 );

    if ( selectedRow == -1 ) {
        JOptionPane.showMessageDialog( this, "Please select a ticket.", "No Ticket Selected", JOptionPane.WARNING_MESSAGE );
        return;
    }

    Ticket ticket = Ticket.getByID( ticketID );
    ticket.cancel();
    JOptionPane.showMessageDialog( this, "Selected Ticket has been canceled.", "Ticket Cancelled", JOptionPane.INFORMATION_MESSAGE );
    refresh( currentPassenger );
    passengerPanel.refreshPassengers();
}

    public void refresh( Passenger passenger ) {
        currentPassenger = passenger;
        // Clear
        tableModel.setRowCount( 0 );

        ArrayList<Ticket> tickets = Ticket.getForPassenger( passenger.getID() );

        for ( Ticket ticket : tickets ) {
            tableModel.addRow( new Object[] {
                ticket.getID(),
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
