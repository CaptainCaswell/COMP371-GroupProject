package com.airline;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Route {
    private int routeID;
    private String fromAirport;
    private String toAirport;
    private int distance;

    // New
    public Route( String fromAirport, String toAirport, int distance) {
        this.fromAirport = fromAirport;
        this.toAirport = toAirport;
        this.distance = distance;
        save();
    }

    // From DB
    public Route( int routeID, String fromAirport, String toAirport, int distance) {
        this.routeID = routeID;
        this.fromAirport = fromAirport;
        this.toAirport = toAirport;
        this.distance = distance;
    }

    public void save() {
        try {
            // Get connection
            Connection conn = Database.getInstance().getConnection();

            String command = "INSERT INTO Routes(fromAirport, toAirport, distance) VALUES (?,?,?)";

            // Prepare statement
            PreparedStatement stmt = conn.prepareStatement( command , Statement.RETURN_GENERATED_KEYS );

            stmt.setString( 1, fromAirport );
            stmt.setString( 2, toAirport );
            stmt.setInt( 3, distance );

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

    public int getID() {
        return routeID;
    }

    public int getDistance() {
        return distance;
    }

    public String getFromAirport() {
        return fromAirport;
    }

    public String getToAirport() {
        return toAirport;
    }

    @Override
    public String toString() {
        return fromAirport + " → " + toAirport;
    }

    public static Route getByID( int routeID ) {
        try {
            Connection conn = Database.getInstance().getConnection();

            String command = "SELECT * FROM Routes WHERE routeID = ?";
            PreparedStatement stmt = conn.prepareStatement( command );

            stmt.setInt( 1, routeID );

            ResultSet result = stmt.executeQuery();

            if ( result.next() ) {
                String fromAirport = result.getString( "fromAirport" );
                String toAirport = result.getString( "toAirport" );
                int distance = result.getInt( "distance" );

                return new Route( routeID, fromAirport, toAirport, distance );
            }

            return null;

        } catch ( Exception e ) {
            System.out.println ( "Error getting tickets: " + e );
            return null;
        }
    }

    public static ArrayList<Route> getAll() {
        ArrayList<Route> routes = new ArrayList<>();

        try {
            Connection conn = Database.getInstance().getConnection();

            String command = "SELECT routeID FROM Routes";
            PreparedStatement stmt = conn.prepareStatement( command );

            ResultSet results = stmt.executeQuery();

            while ( results.next() ) {
                Route temp = getByID( results.getInt( "routeID" ) );

                if ( temp != null ) routes.add( temp );
            }

        } catch ( Exception e ) {
            System.out.println ( "Error getting routes: " + e );
        }

        return routes;
    }

    public static boolean remove( int routeID ) {
        // Try removing plane
        try {
            Connection conn = Database.getInstance().getConnection();

            String command = "DELETE FROM Routes WHERE routeID = ?";
            PreparedStatement stmt = conn.prepareStatement(command);

            stmt.setInt( 1, routeID );
            stmt.executeUpdate();

            return true;
        }
        
        // Remove failed, likely forign key issue
        catch ( Exception e ) {
            return false;
        }
    }
}
