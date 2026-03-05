package com.airline;

public class App {
    public static void main( String[] args ) {
        Database database = Database.getInstance();
        database.seed();
    }
}