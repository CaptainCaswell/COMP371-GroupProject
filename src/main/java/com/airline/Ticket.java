package com.airline;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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

    public Ticket( Flight flight, Passenger passenger, TicketStatus status, TicketType type ) {
        this.flight = flight;
        this.passenger = passenger;
        this.status = status;
        this.type = type;
    }

    public abstract float getRefund();

    public void save() {
        try {
            // Get connection
            Connection conn = Database.getInstance().getConnection();

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
        if ( status == TicketStatus.CONFIRMED ) {
            refund = getRefund();
        }

        this.status = TicketStatus.CANCELLED;

        if ( update() || refund != 0 ) {
            passenger.updateMoney( refund );
        }
    }

    public boolean update() {
        try {
            Connection conn = Database.getInstance().getConnection();

            String command = "UPDATE Tickets SET flightID=?, passengerID=?, status=?, type=?, WHERE ticketID=?";

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
}
