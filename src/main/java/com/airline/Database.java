package com.airline;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
        
    }
}
