package com.airline;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class App {
    public static void main( String[] args ) {
        // Filename of database
        String filename = "airline.db";

        // Prepend info
        String path = "jdbc:sqlite:" + filename;
        
        // DB Connect success
        try ( Connection conn = DriverManager.getConnection( path ) ) {
            if ( conn != null ) {
                System.out.println( "Connected to SQLite successfully!" );
            }
        }
        
        // DB Connect failed
        catch ( SQLException e ) {
            System.out.println( "Connection failed: " + e.getMessage() );
        }
    }
}