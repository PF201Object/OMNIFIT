package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class config {

    private static final String DB_URL = "jdbc:sqlite:gym_members.db";

    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
            // Establish the connection to the database file
            conn = DriverManager.getConnection(DB_URL);
            System.out.println("Connection to SQLite has been established.");
            createTable(conn); // Ensure the table exists
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error connecting to the database: " + e.getMessage());
        }
        return conn;
    }

    private static void createTable(Connection conn) {
        String sql = "CREATE TABLE IF NOT EXISTS customers (\n"
                + "    customer_id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "    name TEXT NOT NULL,\n"
                + "    age INTEGER,\n"
                + "    contact_number TEXT,\n"
                + "    subscription_type TEXT,\n"
                + "    subscription_cost REAL,\n"
                + "    join_date TEXT,\n"
                + "    expiry_date TEXT\n"
                + ");";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Customers table is ready.");
        } catch (SQLException e) {
            System.out.println("Error creating table: " + e.getMessage());
        }
    }

    private static class var {

        public var() {
        }

        private void execute(String sql) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
}