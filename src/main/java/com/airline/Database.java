package com.airline;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static Database instance;
    private Connection conn;
    private static final String path = "jdbc:sqlite:airline.db";

    private Database() {
        // DB Connect success
        try {
            conn = DriverManager.getConnection( path );
            if ( conn != null ) {
                System.out.println( "Connected to SQLite successfully!" );
            }
        }

        // DB Connect failed
        catch ( SQLException e ) {
            System.out.println( "Connection failed: " + e.getMessage() );
        }
    }

    public static Database getInstance() {
        // Start instance if not already started
        if ( instance == null ) {
            instance = new Database();
        }

        return instance;
    }

    public Connection getConnection() {
        return conn;
    }

    public void seed() {
        createTables();

        new Route( "YVR", "YXX" ).save();

        System.out.println( "Done\n" );

    }

    private void dropTable( String tableName ) {
        try {
            String command = "DROP TABLE " + tableName;

            Statement stmt = conn.createStatement();
            stmt.execute( command );
        } catch ( Exception e ) {
            // Do nothing, table doesn't exist
        }
    }

    private void createTables() {
        dropTable( "Planes" );

        // Routes
        try {
            String command = "CREATE TABLE Routes (RouteID INTEGER KEY INCREMENT, toAirport TEXT, fromAirport)";

            Statement stmt = conn.createStatement();
            stmt.execute( command );
        } catch ( Exception e) {
            System.out.println( "Error while creating Routes table: " + e );
        }

        // Planes
        try {
            String command = "CREATE TABLE Planes (tailNumber TEXT KEY, firstClassSeats INTEGER, coachSeats INTEGER, economySeats INTEGER)";

            Statement stmt = conn.createStatement();
            stmt.execute( command );
        } catch ( Exception e) {
            System.out.println( "Error while creating Planes table: " + e );
        }
    }
}
