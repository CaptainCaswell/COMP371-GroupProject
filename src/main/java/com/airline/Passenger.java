package com.airline;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Passenger {
    private int passengerID;
    private String name;
    private float money;

    public Passenger( String name, float money ) {
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

    public int updateMoney( float change ) {
        // Check sufficient funds, or if refund
        if ( change + money > 0 || change > 0 ) {
            money += change;

            // TODO update SQL

            return 1;
        }

        // Return error
        return -1;
    }
}
