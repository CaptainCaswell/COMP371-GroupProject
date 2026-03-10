package com.airline;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

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

        // Planes
        Plane a350 = new Plane("C-GAUP", 900, 20, 40, 80);
        Plane b737 = new Plane("C-FDCA", 850, 10, 30, 60);
        Plane b777 = new Plane("C-FIUA", 950, 30, 60, 120);
        Plane a220 = new Plane("C-GJVK", 820, 8, 24, 48);
        Plane e175 = new Plane("C-FEKC", 780, 6, 18, 36);

        // Routes
        Route yvrYYZ = new Route("YVR", "YYZ", 3400);
        Route yyzYUL = new Route("YYZ", "YUL", 500);
        Route yulYHZ = new Route("YUL", "YHZ", 1200);
        Route yvrYYC = new Route("YVR", "YYC", 600);
        Route yycYWG = new Route("YYC", "YWG", 1200);
        Route yyzYOW = new Route("YYZ", "YOW", 400);

        // Flights
        Flight f1 = new Flight(yvrYYZ, a350, LocalDateTime.parse("2026-05-15T08:00"), 800, 400, 200);
        Flight f2 = new Flight(yyzYUL, b737, LocalDateTime.parse("2026-05-15T14:00"), 300, 150, 75);
        Flight f3 = new Flight(yulYHZ, a220, LocalDateTime.parse("2026-05-16T09:00"), 400, 200, 100);
        Flight f4 = new Flight(yvrYYC, e175, LocalDateTime.parse("2026-05-16T11:00"), 250, 125, 60);
        Flight f5 = new Flight(yycYWG, b737, LocalDateTime.parse("2026-05-17T07:00"), 350, 175, 85);
        Flight f6 = new Flight(yyzYOW, a220, LocalDateTime.parse("2026-05-17T16:00"), 200, 100, 50);

        // Passengers
        Passenger ryan = new Passenger("Ryan Caswell", 2000);
        Passenger alice = new Passenger("Alice Chen", 1500);
        Passenger bob = new Passenger("Bob Smith", 3000);
        Passenger carol = new Passenger("Carol Jones", 1000);
        Passenger dave = new Passenger("Dave Wilson", 5000);

        // Tickets
        new FirstClassTicket(f1, ryan);
        new CoachTicket(f2, alice);
        new EconomyTicket(f3, bob);
        new FirstClassTicket(f4, dave);
        new CoachTicket(f5, carol);
        new EconomyTicket(f6, ryan);
        new CoachTicket(f1, alice);

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
        // Drop in order
        dropTable( "Tickets" );
        dropTable( "Passengers" );
        dropTable( "Flights" );
        dropTable( "Planes" );
        dropTable( "Routes" );
        
        // Routes
        try {
            String command = "CREATE TABLE Routes (routeID INTEGER PRIMARY KEY AUTOINCREMENT, fromAirport TEXT, toAirport TEXT, distance INTEGER)";

            Statement stmt = conn.createStatement();
            stmt.execute( command );
        } catch ( Exception e) {
            System.out.println( "Error while creating Routes table: " + e );
        }

        // Planes
        try {
            String command = "CREATE TABLE Planes (tailNumber TEXT PRIMARY KEY, speed INTEGER, firstClassSeats INTEGER, coachSeats INTEGER, economySeats INTEGER)";

            Statement stmt = conn.createStatement();
            stmt.execute( command );
        } catch ( Exception e) {
            System.out.println( "Error while creating Planes table: " + e );
        }

        // Flights
        try {
            String command = "CREATE TABLE Flights (flightID INTEGER PRIMARY KEY AUTOINCREMENT, routeID INTEGER, tailNumber TEXT, deptTime TEXT, arriveTime TEXT, firstClassCost REAL, coachCost REAL, economyCost REAL, FOREIGN KEY(routeID) REFERENCES Routes(routeID), FOREIGN KEY(tailNumber) REFERENCES Planes(tailNumber))";

            Statement stmt = conn.createStatement();
            stmt.execute( command );
        } catch ( Exception e) {
            System.out.println( "Error while creating Flights table: " + e );
        }

        // Passenger
        try {
            String command = "CREATE TABLE Passengers (passengerID INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, money REAL)";

            Statement stmt = conn.createStatement();
            stmt.execute( command );
        } catch ( Exception e) {
            System.out.println( "Error while creating Passengers table: " + e );
        }

        // Ticket
        try {
            String command = "CREATE TABLE Tickets (ticketID INTEGER PRIMARY KEY AUTOINCREMENT, flightID INTEGER, passengerID INTEGER, status TEXT, type TEXT, FOREIGN KEY(flightID) REFERENCES Flights(flightID), FOREIGN KEY(passengerID) REFERENCES Passengers(passengerID))";

            Statement stmt = conn.createStatement();
            stmt.execute( command );
        } catch ( Exception e) {
            System.out.println( "Error while creating Tickets table: " + e );
        }
    }
}
