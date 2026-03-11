package com.airline;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

import com.airline.Flight.CapacityStatus;
import com.airline.Ticket.TicketType;

public class BookFlightPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<TicketType> typeDropdown;
    private JButton bookButton;
    private Passenger passenger;

    public BookFlightPanel( Passenger passenger ) {
        this.passenger = passenger;

        setLayout( new BorderLayout( 0,10 ) );

        add( buildTablePanel(), BorderLayout.CENTER );
        add( buildBookPanel(), BorderLayout.SOUTH );

        refresh();
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel( new BorderLayout() );

        String[] columns = {"Flights", "Route", "Departure", "Arrival", "First Class", "Coach", "Economy" };
        tableModel = new DefaultTableModel( columns, 0 );
        table = new JTable( tableModel );

        panel.add( new JScrollPane( table ), BorderLayout.CENTER );
        return panel;
    }

    private JPanel buildBookPanel() {
        JPanel panel = new JPanel( new FlowLayout() );

        typeDropdown = new JComboBox<>( TicketType.values() );
        bookButton = new JButton( "Book" );

        bookButton.addActionListener( e -> bookFlight() );
        
        panel.add( new JLabel( "Ticket Type:" ) );
        panel.add( typeDropdown );
        panel.add( bookButton );

        return panel;
    }

    private String formatCapacity( CapacityStatus status, float price ) {
        String capacityString;

        switch( status ) {
            case FULL: capacityString = "Full"; break;
            case SCARCE: capacityString = "Almost Full"; break;
            case OPEN: capacityString = "Open"; break;
            default: capacityString = "---";
        }

        return "$" + price + " (" + capacityString + ")";
    }

    public void refresh() {
        tableModel.setRowCount( 0 );
        ArrayList<Flight> flights = Flight.getAll();
        for ( Flight flight : flights ) {
            tableModel.addRow( new Object[] {
                flight.getID(),
                flight.getRoute(),
                flight.getDeptTime(),
                flight.getDeptTime(), //TODO Make arrival time
                formatCapacity( flight.getCapacity( TicketType.FIRST ), flight.getPrice( TicketType.FIRST ) ),
                formatCapacity( flight.getCapacity( TicketType.COACH ), flight.getPrice( TicketType.COACH ) ),
                formatCapacity( flight.getCapacity( TicketType.ECONOMY ), flight.getPrice( TicketType.ECONOMY ) )
            });
        }
    }

    private void bookFlight() {
        int selectedRow = table.getSelectedRow();

        if ( selectedRow == -1 ) {
            JOptionPane.showMessageDialog( this, "Please select a flight.", "No Flight selected", JOptionPane.WARNING_MESSAGE );
            return;
        }

        int flightID = (int) tableModel.getValueAt( selectedRow, 0 );
        Flight flight = Flight.getByID( flightID );
        TicketType type = (TicketType) typeDropdown.getSelectedItem();

        if ( flight.getCapacity( type ) == CapacityStatus.FULL ) {
            JOptionPane.showMessageDialog( this, "This flight is full.", "Full", JOptionPane.WARNING_MESSAGE );
            return;
        }

        // TODO Add reminder to confirm soon if scarce?

        switch ( type ) {
            case FIRST: new FirstClassTicket( flight, passenger ); break;
            case COACH: new CoachTicket( flight, passenger ); break;
            case ECONOMY: new EconomyTicket( flight , passenger ); break;
        }

        JOptionPane.showMessageDialog( this, "Flight booked!", "Success", JOptionPane.INFORMATION_MESSAGE );
        refresh();
    }

    public void setPassenger( Passenger passenger ) {
        this.passenger = passenger;
    }
}
