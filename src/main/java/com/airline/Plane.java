package com.airline;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class Plane {
    private String planeID;
    private int speed;
    private int firstClassSeats;
    private int coachSeats;
    private int economySeats;

    public Plane( String planeID, int speed, int firstClassSeats, int coachSeats, int economySeats ) {
        this.planeID = planeID;
        this.speed = speed;
        this.firstClassSeats = firstClassSeats;
        this.coachSeats = coachSeats;
        this.economySeats = economySeats;
    }

    public void save() {
        try {
            // Get connection
            Connection conn = Database.getInstance().getConnection();

            String command = "INSERT INTO Planes(placeID,speed,firstClassSeats,coachSeats,economySeats) VALUES (?,?,?,?,?)";

            // Prepare statement
            PreparedStatement stmt = conn.prepareStatement( command );

            stmt.setString( 1, planeID );
            stmt.setInt( 2, speed );
            stmt.setInt( 3, firstClassSeats );
            stmt.setInt( 4, coachSeats );
            stmt.setInt( 5, economySeats );

            stmt.executeUpdate();
        } catch ( Exception e ) {
            System.out.println( "Error inserting plane: " + e );
        }

    }

    public String getID() {
        return planeID;
    }

    public int getSpeed() {
        return speed;
    }
}
