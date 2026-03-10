package com.airline;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Passenger {
    private int passengerID;
    private String name;
    private float money;

    // New
    public Passenger( String name, float money ) {
        this.name = name;
        this.money = money;
        save();
    }

    // From DB
    public Passenger( int passengerID, String name, float money ) {
        this.passengerID = passengerID;
        this.name = name;
        this.money = money;
    }

    public void save() {
        try {
            // Get connection
            Connection conn = Database.getInstance().getConnection();

            String command = "INSERT INTO Passengers(name,money) VALUES (?,?)";

            // Prepare statement
            PreparedStatement stmt = conn.prepareStatement( command , Statement.RETURN_GENERATED_KEYS );

            stmt.setString( 1, name );
            stmt.setFloat( 2, money );

            stmt.executeUpdate();

            // Get RouteID from autoincrement
            ResultSet keys = stmt.getGeneratedKeys();

            if ( keys.next() ) {
                this.passengerID = keys.getInt( 1 );
            }

        } catch ( Exception e ) {
            System.out.println( "Error inserting plane: " + e );
        }

    }

    public int getID() {
        return passengerID;
    }

    public boolean updateMoney( float change ) {
        // Check sufficient funds, or if refund
        if ( change + money > 0 || change > 0 ) {
            money += change;

            // TODO update SQL

            return true;
        }

        // Return error
        return false;
    }
    
    public String getName() {
        return name;
    }

    public static ArrayList<Passenger> getAll() {
        ArrayList<Passenger> passengers = new ArrayList<>();

        try {
            Connection conn = Database.getInstance().getConnection();

            String command = "SELECT passengerID FROM Passengers";
            PreparedStatement stmt = conn.prepareStatement( command );

            ResultSet results = stmt.executeQuery();

            while ( results.next() ) {
                Passenger temp = getByID( results.getInt( "passengerID" ) );

                if ( temp != null ) passengers.add( temp );
            }

        } catch ( Exception e ) {
            System.out.println ( "Error getting tickets: " + e );
        }

        return passengers;
    }

    public static Passenger getByID( int passengerID ) {
        try {
            Connection conn = Database.getInstance().getConnection();

            String command = "SELECT * FROM Passengers WHERE passengerID = ?";
            PreparedStatement stmt = conn.prepareStatement( command );

            stmt.setInt( 1, passengerID );

            ResultSet result = stmt.executeQuery();

            if ( result.next() ) {
                String name = result.getString( "name" );
                float money = result.getFloat( "money" );

                return new Passenger( passengerID, name, money );
            }

            return null;

        } catch ( Exception e ) {
            System.out.println ( "Error getting tickets: " + e );
            return null;
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
