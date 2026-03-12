package com.airline;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.airline.Ticket.TicketStatus;
import com.airline.Ticket.TicketType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Flight {
    private int flightID;
    private Route route;
    private Plane plane;
    private LocalDateTime deptTime;
    private LocalDateTime arriveTime;
    private float firstClassCost;
    private float coachCost;
    private float economyCost;

    public enum CapacityStatus {
        OPEN, SCARCE, FULL
    }

    // New
    public Flight( Route route, Plane plane, LocalDateTime deptTime, float firstClassCost, float coachCost, float economyCost ) {
        this.route = route;
        this.plane = plane;
        this.deptTime = deptTime;
        this.arriveTime = deptTime.plusHours( ( route.getDistance() / plane.getSpeed() ) );
        this.firstClassCost = firstClassCost;
        this.coachCost = coachCost;
        this.economyCost = economyCost;
        save();
    }

    // From DB
    public Flight( int flightID, Route route, Plane plane, LocalDateTime deptTime, LocalDateTime arriveTime, float firstClassCost, float coachCost, float economyCost ) {
        this.flightID = flightID;
        this.route = route;
        this.plane = plane;
        this.deptTime = deptTime;
        this.arriveTime = arriveTime;
        this.firstClassCost = firstClassCost;
        this.coachCost = coachCost;
        this.economyCost = economyCost;
    }

    public void save() {
        try {
            // Get connection
            Connection conn = Database.getInstance().getConnection();

            String command = "INSERT INTO Flights(routeID, tailNumber, deptTime, arriveTime, firstClassCost, coachCost, economyCost) VALUES (?,?,?,?,?,?,?)";

            // Prepare statement
            PreparedStatement stmt = conn.prepareStatement( command , Statement.RETURN_GENERATED_KEYS );

            stmt.setInt( 1, route.getID() );
            stmt.setString( 2, plane.getID() );
            stmt.setString( 3, this.deptTime.toString() );
            stmt.setString( 4, this.arriveTime.toString() );
            stmt.setFloat( 5, firstClassCost );
            stmt.setFloat( 6, coachCost );
            stmt.setFloat( 7, economyCost );

            stmt.executeUpdate();

            // Get RouteID from autoincrement
            ResultSet keys = stmt.getGeneratedKeys();

            if ( keys.next() ) {
                this.flightID = keys.getInt( 1 );
            }

        } catch ( Exception e ) {
            System.out.println( "Error inserting flight: " + e );
        }

    }

    public int getID() {
        return flightID;
    }

    public LocalDateTime getDeptTime() {
        return deptTime;
    }

    public LocalDateTime getArriveTime() {
        return deptTime;
    }

    public float getPrice( TicketType type ) {
        switch( type ) {
            case FIRST: return firstClassCost;
            case COACH: return coachCost;
            case ECONOMY: return economyCost;
            default: return 0;
        }
    }

    public String getRoute() {
        return route.toString();
    }

    public String getFromAirport() {
        return route.getFromAirport();
    }

    public String getToAirport() {
        return route.getToAirport();
    }

    public String getTailNumber() {
        return plane.getID();
    }

    public CapacityStatus getCapacity( TicketType type ) {
        // Get total seats
        int capacity = plane.getSeats( type );

        // Get booked and confirmed seats
        int booked = Ticket.getCount( flightID, type, TicketStatus.BOOKED );
        int confirmed = Ticket.getCount( flightID, type, TicketStatus.CONFIRMED );

        // Return based on if seats are available
        if ( confirmed >= capacity ) return CapacityStatus.FULL;
        if ( confirmed + booked >= capacity ) return CapacityStatus.SCARCE;
        return CapacityStatus.OPEN;
    }

    public static Flight getByID( int flightID ) {
        try {
            Connection conn = Database.getInstance().getConnection();

            String command = "SELECT * FROM Flights WHERE flightID = ?";
            PreparedStatement stmt = conn.prepareStatement( command );

            stmt.setInt( 1, flightID );

            ResultSet result = stmt.executeQuery();

            if ( result.next() ) {
                Route route = Route.getByID( result.getInt( "routeID" ) );
                Plane plane = Plane.getByID( result.getString( "tailNumber" ) );
                LocalDateTime deptTime = LocalDateTime.parse( result.getString( "deptTime" ) );
                LocalDateTime arriveTime = LocalDateTime.parse( result.getString( "arriveTime" ) );
                float firstClassCost = result.getFloat( "firstClassCost" );
                float coachCost = result.getFloat( "coachCost" );
                float economyCost = result.getFloat( "economyCost" );

                return new Flight( flightID, route, plane, deptTime, arriveTime, firstClassCost, coachCost, economyCost );
            }

            return null;

        } catch ( Exception e ) {
            System.out.println ( "Error getting tickets: " + e );
            return null;
        }
    }

    public static ArrayList<Flight> getAll() {
        ArrayList<Flight> flights = new ArrayList<>();

        try {
            Connection conn = Database.getInstance().getConnection();

            String command = "SELECT flightID FROM Flights";
            PreparedStatement stmt = conn.prepareStatement( command );

            ResultSet results = stmt.executeQuery();

            while ( results.next() ) {
                Flight temp = getByID( results.getInt( "flightID" ) );

                if ( temp != null ) flights.add( temp );
            }

        } catch ( Exception e ) {
            System.out.println ( "Error getting flights: " + e );
        }

        return flights;
    }

    public String remove() {
        // Try removing flight
        try {
            Connection conn = Database.getInstance().getConnection();

            String command = "DELETE FROM Flights WHERE flightID = ?";
            PreparedStatement stmt = conn.prepareStatement(command);

            stmt.setInt( 1, flightID );
            stmt.executeUpdate();

            return null;
        }
        
        // Remove failed, likely forign key issue
        catch ( Exception e ) {
            return "Cannot remove flight - it has existing tickets. Remove tickets and try again.";
        }
    }
}
