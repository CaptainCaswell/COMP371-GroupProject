package com.airline;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Route {
    private int routeID;
    private String fromAirport;
    private String toAirport;

    public Route( String fromAirport, String toAirport) {
        this.fromAirport = fromAirport;
        this.toAirport = toAirport;
    }

    public void save() {
        try {
            // Get connection
            Connection conn = Database.getInstance().getConnection();

            String command = "INSERT INTO Routes(fromAirport, toAirport) VALUES (?,?)";

            // Prepare statement
            PreparedStatement stmt = conn.prepareStatement( command , Statement.RETURN_GENERATED_KEYS );

            stmt.setString( 1, fromAirport );
            stmt.setString( 2, toAirport );

            stmt.executeUpdate();

            // Get RouteID from autoincrement
            ResultSet keys = stmt.getGeneratedKeys();

            if ( keys.next() ) {
                this.routeID = keys.getInt( 1 );
            }

        } catch ( Exception e ) {
            System.out.println( "Error inserting route: " + e );
        }

    }
}
