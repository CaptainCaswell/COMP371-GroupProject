package com.airline;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import com.airline.Flight.CapacityStatus;

public abstract class Ticket {
    private int ticketID;
    private TicketStatus status;
    private Flight flight;
    private Passenger passenger;
    private TicketType type;

    // enum for status
    public enum TicketStatus {
        BOOKED, CONFIRMED, CANCELLED
    }

    public enum TicketType {
        FIRST, COACH, ECONOMY
    }

    // New Tickets
    public Ticket( Flight flight, Passenger passenger, TicketStatus status, TicketType type ) {
        this.flight = flight;
        this.passenger = passenger;
        this.status = status;
        this.type = type;
        save();
    }

    // Tickets from DB
    public Ticket( int ticketID, Flight flight, Passenger passenger, TicketStatus status, TicketType type ) {
        this.ticketID = ticketID;
        this.flight = flight;
        this.passenger = passenger;
        this.status = status;
        this.type = type;
    }

    public abstract float getRefund();

    public void save() {
        try {
            // Get connection
            Connection conn = Database.getConnection();

            String command = "INSERT INTO Tickets(flightID,passengerID,status,type) VALUES (?,?,?,?)";

            // Prepare statement
            PreparedStatement stmt = conn.prepareStatement( command , Statement.RETURN_GENERATED_KEYS );

            stmt.setInt( 1, flight.getID() );
            stmt.setInt( 2, passenger.getID() );
            stmt.setString( 3, status.toString() );
            stmt.setString( 4, type.toString() );

            stmt.executeUpdate();

            // Get RouteID from autoincrement
            ResultSet keys = stmt.getGeneratedKeys();

            if ( keys.next() ) {
                this.ticketID = keys.getInt( 1 );
            }

        } catch ( Exception e ) {
            System.out.println( "Error inserting plane: " + e );
        }

    }

    public int getID() {
        return ticketID;
    }

    public String getRoute() {
        return flight.getRoute();
    }

    public LocalDateTime getDeptTime() {
        return flight.getDeptTime();
    }

    public TicketType getType() {
        return type;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public int daysTillFlight() {
        int days = (int) ChronoUnit.DAYS.between( LocalDateTime.now(), flight.getDeptTime() ); 

        return days;
    }

    public float getPrice() {
        return flight.getPrice( type );
    }

    public void cancel() {
        // Check if ticket has been paid
        float refund = 0;

        System.out.println( "DEBUG: canceling" );

        if ( status == TicketStatus.CONFIRMED ) {
            refund = getRefund();
            System.out.println( "DEBUG: refund = " + refund );
        }

        this.status = TicketStatus.CANCELLED;

        // Update money if there was a refund
        if ( refund != 0 ) {
            passenger.updateMoney( refund );
        }

        System.out.println( "DEBUG: updating" );

        update();
    }

    public String confirm() {
        // Errors if not BOOKED
        if ( status == TicketStatus.CONFIRMED ) {
            return "Ticket already confirmed.";
        }

        if ( status == TicketStatus.CANCELLED ) {
            return "Cannot confirm cancelled tickets.";
        }

        // Check availability
        if ( flight.getCapacity( type ) == CapacityStatus.FULL ) {
            return "Flight already full.";
        }

        // Charge passenger
        if ( !passenger.updateMoney( flight.getPrice( type ) * -1 ) ) {
            return "Insufficient funds.";
        }

        // Change status
        this.status = TicketStatus.CONFIRMED;

        // Update database
        if ( !update() ) {
            return "Unable to confirm ticket.";
        }

        // Remove 

        return null;
    }

    public boolean update() {
        // Update database
        try {
            Connection conn = Database.getConnection();

            String command = "UPDATE Tickets SET flightID=?, passengerID=?, status=?, type=? WHERE ticketID=?";

            PreparedStatement stmt = conn.prepareStatement( command );

            stmt.setInt(1, flight.getID());
            stmt.setInt(2, passenger.getID());
            stmt.setString(3, status.toString());
            stmt.setString(4, type.toString());
            stmt.setInt(5, ticketID);

            stmt.executeUpdate();

            return true;
        } catch ( Exception e) {
            System.out.println( "Error updating ticket: " + e );

            return false;
        }
    }

    public String getAlert() {
        // Check if flight full
        if ( getStatus() == TicketStatus.BOOKED && flight.getCapacity( type ) == CapacityStatus.FULL ) return "Flight full, wait for cancelation.";

        // Check if tickets overbooked
        if ( getStatus() == TicketStatus.BOOKED && flight.getCapacity( type ) == CapacityStatus.SCARCE ) return "Confirm soon!";

        // No alerts
        return "";
    }

    public static ArrayList<Ticket> getAll() {
        ArrayList<Ticket> tickets = new ArrayList<>();

        try {
            Connection conn = Database.getConnection();

            String command = "SELECT ticketID FROM Tickets";
            PreparedStatement stmt = conn.prepareStatement( command );

            ResultSet results = stmt.executeQuery();

            while ( results.next() ) {
                Ticket temp = getByID( results.getInt( "ticketID" ) );

                if ( temp != null ) tickets.add( temp );
            }

        } catch ( Exception e ) {
            System.out.println ( "Error getting tickets: " + e );
        }

        return tickets;
    }

    public static ArrayList<Ticket> getForPassenger( int passengerID ) {
        ArrayList<Ticket> tickets = new ArrayList<>();

        try {
            Connection conn = Database.getConnection();

            String command = "SELECT ticketID FROM Tickets WHERE passengerID=?";
            PreparedStatement stmt = conn.prepareStatement( command );

            stmt.setInt( 1, passengerID );

            ResultSet results = stmt.executeQuery();

            while ( results.next() ) {
                Ticket temp = getByID( results.getInt( "ticketID" ) );

                if ( temp != null ) tickets.add( temp );
            }

        } catch ( Exception e ) {
            System.out.println ( "Error getting tickets: " + e );
        }

        return tickets;
    }

    public static Ticket getByID( int ticketID ) {
        try {
            Connection conn = Database.getConnection();

            String command = "SELECT * FROM Tickets WHERE ticketID = ?";
            PreparedStatement stmt = conn.prepareStatement( command );

            stmt.setInt( 1, ticketID );

            ResultSet result = stmt.executeQuery();

            if ( result.next() ) {
                Flight flight = Flight.getByID( result.getInt( "flightID" ) );
                Passenger passenger = Passenger.getByID( result.getInt( "passengerID" ) );
                TicketStatus status = TicketStatus.valueOf( result.getString( "status" ) );

                switch( TicketType.valueOf( result.getString( "type" ) ) ) {
                    case FIRST: return new FirstClassTicket( ticketID, flight, passenger, status );
                    case COACH: return new CoachTicket( ticketID, flight, passenger, status );
                    case ECONOMY: return new EconomyTicket( ticketID, flight, passenger, status );
                }
            }

            return null;

        } catch ( Exception e ) {
            System.out.println ( "Error getting tickets: " + e );
            return null;
        }
    }

    public static int getCount( int flightID, TicketType type, TicketStatus status ) {
        try {
            Connection conn = Database.getConnection();

            String command = "SELECT COUNT(*) FROM Tickets WHERE flightID=? AND type=? AND status=?";
            PreparedStatement stmt = conn.prepareStatement(command);

            stmt.setInt( 1, flightID );
            stmt.setString( 2, type.toString() );
            stmt.setString( 3, status.toString() );

            ResultSet result = stmt.executeQuery();

            // Return if result returned
            if ( result.next() ) return result.getInt( 1 );
        } catch ( Exception e ) {
            System.out.println("Error getting ticket count: " + e);
        }
        
        return 0;
    }

    public static String book( Flight flight, Passenger passenger, TicketType type ) {
        if ( flight == null ) return "Invalid flight selected.";
        if ( passenger == null ) return "Invalid passenger selcted.";
        if ( flight.getCapacity( type ) == CapacityStatus.FULL ) return "This flight is full.";

        switch ( type ) {
            case FIRST: new FirstClassTicket( flight, passenger ); break;
            case COACH: new CoachTicket( flight, passenger ); break;
            case ECONOMY: new EconomyTicket( flight , passenger ); break;
            default: return "Invalid ticket type.";
        }

        return null;
    }
}
