package com.airline;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;

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

    public Flight( Route route, Plane plane, LocalDateTime deptTime, float firstClassCost, float coachCost, float economyCost ) {
        this.route = route;
        this.plane = plane;
        this.deptTime = deptTime;
        this.arriveTime = deptTime.plusHours( ( route.getDistance() * plane.getSpeed() ) );
        this.firstClassCost = firstClassCost;
        this.coachCost = coachCost;
        this.economyCost = economyCost;
    }

    public void save() {
        try {
            // Get connection
            Connection conn = Database.getInstance().getConnection();

            String command = "INSERT INTO Flights(routeID, planeID, deptTime, arriveTime, firstClassCost, coachCost, enonomyCost) VALUES (?,?,?,?,?,?,?)";

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

    public float getPrice( TicketType type ) {
        switch( type ) {
            case FIRST: return firstClassCost;
            case COACH: return coachCost;
            case ECONOMY: return economyCost;
            default: return 0;
        }
    }
}
