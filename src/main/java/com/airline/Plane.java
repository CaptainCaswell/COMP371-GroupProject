package com.airline;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;

public class Plane {
    private String tailNumber;
    private int speed;
    private int firstClassSeats;
    private int coachSeats;
    private int economySeats;

    public Plane( String tailNumber, int speed, int firstClassSeats, int coachSeats, int economySeats ) {
        this.tailNumber = tailNumber;
        this.speed = speed;
        this.firstClassSeats = firstClassSeats;
        this.coachSeats = coachSeats;
        this.economySeats = economySeats;
        save();
    }

    public Plane( String tailNumber, int speed, int firstClassSeats, int coachSeats, int economySeats, boolean fromDB ) {
        this.tailNumber = tailNumber;
        this.speed = speed;
        this.firstClassSeats = firstClassSeats;
        this.coachSeats = coachSeats;
        this.economySeats = economySeats;
    }

    public void save() {
        try {
            // Get connection
            Connection conn = Database.getInstance().getConnection();

            String command = "INSERT INTO Planes(tailNumber,speed,firstClassSeats,coachSeats,economySeats) VALUES (?,?,?,?,?)";

            // Prepare statement
            PreparedStatement stmt = conn.prepareStatement( command );

            stmt.setString( 1, tailNumber );
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
        return tailNumber;
    }

    public int getSpeed() {
        return speed;
    }

    public static Plane getByID( String tailNumber ) {
        try {
            Connection conn = Database.getInstance().getConnection();

            String command = "SELECT * FROM Planes WHERE tailNumber = ?";
            PreparedStatement stmt = conn.prepareStatement( command );

            stmt.setString( 1, tailNumber );

            ResultSet result = stmt.executeQuery();

            if ( result.next() ) {
                int speed = result.getInt( "speed" );
                int firstClassSeats = result.getInt( "firstClassSeats" );
                int coachSeats = result.getInt( "coachSeats" );
                int economySeats = result.getInt( "economySeats" );

                return new Plane( tailNumber, speed, firstClassSeats, coachSeats, economySeats, true );
            }

            return null;

        } catch ( Exception e ) {
            System.out.println ( "Error getting tickets: " + e );
            return null;
        }
    }
}
