package com.airline;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import com.airline.Ticket.TicketType;

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

    public int getSeats( TicketType type ) {
        switch( type ) {
            case FIRST: return firstClassSeats;
            case COACH: return coachSeats;
            case ECONOMY: return economySeats;
            default: return 0;
        }
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

    public static ArrayList<Plane> getAll() {
        ArrayList<Plane> planes = new ArrayList<>();

        try {
            Connection conn = Database.getInstance().getConnection();

            String command = "SELECT tailNumber FROM Planes";
            PreparedStatement stmt = conn.prepareStatement( command );

            ResultSet results = stmt.executeQuery();

            while ( results.next() ) {
                Plane temp = getByID( results.getString( "tailNumber" ) );

                if ( temp != null ) planes.add( temp );
            }

        } catch ( Exception e ) {
            System.out.println ( "Error getting planes: " + e );
        }

        return planes;
    }

    public static boolean remove( String tailNumber ) {
        // Try removing plane
        try {
            Connection conn = Database.getInstance().getConnection();

            String command = "DELETE FROM Planes WHERE tailNumber = ?";
            PreparedStatement stmt = conn.prepareStatement(command);

            stmt.setString( 1, tailNumber );
            stmt.executeUpdate();

            return true;
        }
        
        // Remove failed, likely forign key issue
        catch ( Exception e ) {
            return false;
        }
    }
}
