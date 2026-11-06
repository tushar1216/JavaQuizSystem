
package com.quiz;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseConnection {

    // MySQL connection details
    private static final String URL =
            "jdbc:mysql://localhost:3306/quiz_system";

    private static final String USER = "root";

    // Enter your actual MySQL root password here
    private static final String PASSWORD = "******";


    // Establish connection with MySQL
    public static Connection getConnection() throws SQLException {

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }


    // Reserve one quiz attempt for a registration number
    public static boolean startAttempt(String regNo)
            throws SQLException {

        String sql = "INSERT INTO quiz_attempts (reg_no, status) "
                + "VALUES (?, 'STARTED')";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, regNo);

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {

            // SQLState 23000 indicates a constraint violation,
            // including a duplicate registration number.
            if ("23000".equals(e.getSQLState())) {
                return false;
            }

            throw e;
        }
    }


    // Save the student's quiz result
    public static boolean saveResult(
            String regNo, int score, int totalQuestions)
            throws SQLException {

        String sql = "UPDATE quiz_attempts "
                + "SET score = ?, total_questions = ?, status = 'COMPLETED' "
                + "WHERE reg_no = ? AND status = 'STARTED'";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, score);
            ps.setInt(2, totalQuestions);
            ps.setString(3, regNo);

            return ps.executeUpdate() == 1;
        }
    }


    // Test database connection
    public static void main(String[] args) {

        try (Connection con = getConnection()) {

            System.out.println("Database connected successfully!");

            System.out.println(
                    "Connected database: "
                            + con.getCatalog()
            );

        } catch (SQLException e) {

            System.out.println("Database connection failed!");

            e.printStackTrace();
        }
    }
}
