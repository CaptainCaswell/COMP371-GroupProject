package com.airline;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class Plane {
    private String tailNumber;
    private int firstClassSeats;
    private int coachSeats;
    private int economySeats;

    public Plane(String tailNumber, int firstClassSeats, int coachSeats, int economySeats){
        this.tailNumber = tailNumber;
        this.firstClassSeats = firstClassSeats;
        this.coachSeats = coachSeats;
        this.economySeats = economySeats;
    }

    public void insertPlane() {
        try {
            Connection conn = Database.getInstance().getConnection();

            String command = "INSERT INTO Planes(tailNumber, firstClassSeats, coachSeats, economySeats) VALUES (?,?,?,?)";


            PreparedStatement stmt = conn.prepareStatement(command);

            stmt.setString(1, this.tailNumber);
            stmt.setInt(2, this.firstClassSeats);
            stmt.setInt(3, this.coachSeats);
            stmt.setInt(4, this.economySeats);

            stmt.executeUpdate();

        } catch (Exception e) {
            System.out.println("Error inserting plane: " + e);
        }
    }




}
