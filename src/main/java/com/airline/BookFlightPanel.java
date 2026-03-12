package com.airline;

import com.github.lgooddatepicker.components.DatePicker;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;

import com.airline.Flight.CapacityStatus;
import com.airline.Ticket.TicketType;

public class BookFlightPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<TicketType> typeDropdown;
    private JComboBox<String> fromDropdown;
    private JComboBox<String> toDropdown;
    private DatePicker dateFromPicker;
    private DatePicker dateToPicker;
    private JButton bookButton;
    private Passenger passenger;

    public BookFlightPanel( Passenger passenger ) {
        this.passenger = passenger;

        setLayout( new BorderLayout( 0,10 ) );

        add( buildFilterPanel(), BorderLayout.NORTH );
        add( buildTablePanel(), BorderLayout.CENTER );
        add( buildBookPanel(), BorderLayout.SOUTH );

        refresh();
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel( new BorderLayout() );

        String[] columns = {"Flight", "Route", "Departure", "Arrival", "First Class", "Coach", "Economy" };
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

    private JPanel buildFilterPanel() {
        JPanel panel = new JPanel( new GridLayout( 3, 1, 5, 5 ) );

        // Row 1
        JPanel datePanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
        dateFromPicker = new DatePicker();
        dateToPicker = new DatePicker();
        datePanel.add( new JLabel( "Departing after:" ) );
        datePanel.add( dateFromPicker );
        datePanel.add( new JLabel( "Departing before:" ) );
        datePanel.add( dateToPicker );

        

        // Row 2
        JPanel airportPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
        fromDropdown = new JComboBox<>();
        toDropdown = new JComboBox<>();
        airportPanel.add( new JLabel( "Departure Airport:" ) );
        airportPanel.add( fromDropdown );
        airportPanel.add( new JLabel( "Destination Airport:" ) );
        airportPanel.add( toDropdown );

        // Row 3
        JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
        JButton searchButton = new JButton( "Search" );
        JButton clearButton = new JButton( "Clear" );

        searchButton.addActionListener( e -> refresh() );

        clearButton.addActionListener( e -> {
            fromDropdown.setSelectedIndex( 0 );
            toDropdown.setSelectedIndex( 0 );
            // TODO Pickers
            refresh();
        });

        buttonPanel.add( searchButton );
        buttonPanel.add( clearButton );

        panel.add( datePanel );
        panel.add( airportPanel );
        panel.add( buttonPanel );

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
        

        // Current Selection
        String selectedFrom = (String) fromDropdown.getSelectedItem();
        String selectedTo = (String) toDropdown.getSelectedItem();

        // Empty and refill dropdowns
        fromDropdown.removeAllItems();
        toDropdown.removeAllItems();
        fromDropdown.addItem( "" );
        toDropdown.addItem( "" );

        ArrayList<Flight> flights = Flight.getAll();

        for ( Flight flight : flights ) {
            String from = flight.getFromAirport();
            String to = flight.getToAirport();

            if ( !containsItem( fromDropdown, from ) ) fromDropdown.addItem( from ); // TODO Needed?
            if ( !containsItem( toDropdown, to ) ) toDropdown.addItem( to );
        }

        // Restore selections
        fromDropdown.setSelectedItem( selectedFrom );
        toDropdown.setSelectedItem( selectedTo );

        // Filter and populate table
        String filterFrom = (String) fromDropdown.getSelectedItem();
        String filterTo = (String) toDropdown.getSelectedItem();

        LocalDate dateFrom = dateFromPicker.getDate();
        LocalDate dateTo = dateToPicker.getDate();

        for ( Flight flight : flights ) {
            // Skip filtered dates
            if ( dateFrom != null && flight.getDeptTime().toLocalDate().isBefore( dateFrom ) ) continue;
            if ( dateTo != null && flight.getDeptTime().toLocalDate().isAfter( dateTo ) ) continue;
            
            // Skip filtered airports
            if ( filterFrom != null && !filterFrom.isEmpty() && !flight.getFromAirport().equals ( filterFrom ) ) continue;
            if ( filterTo != null && !filterTo.isEmpty() && !flight.getToAirport().equals ( filterTo ) ) continue;

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

    private boolean containsItem( JComboBox<String> dropdown, String item ) {
        for ( int i = 0; i < dropdown.getItemCount(); i++ ) {
            if (dropdown.getItemAt( i ).equals( item ) ) return true;
        }
        return false;
    }

    private void bookFlight() {
        int selectedRow = table.getSelectedRow();

        int flightID = (int) tableModel.getValueAt( selectedRow, 0 );
        Flight flight = Flight.getByID( flightID );
        TicketType type = (TicketType) typeDropdown.getSelectedItem();

        String error = Ticket.book( flight, passenger, type );

        if ( error == null ) {
            JOptionPane.showMessageDialog( this, "Flight booked!", "Success", JOptionPane.INFORMATION_MESSAGE );
            refresh();
        } else {
            JOptionPane.showMessageDialog( this, error, "Booking Failed", JOptionPane.ERROR_MESSAGE );
        }
    }

    public void setPassenger( Passenger passenger ) {
        this.passenger = passenger;
    }
}
