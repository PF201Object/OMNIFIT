package main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");

    static {
        dateFormat.setLenient(false);
    }

    public static void main(String[] args) {
        int choice;
        do {
            displayMenu();
            System.out.print("Enter your choice: ");
            try {
                choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        addCustomer();
                        break;
                    case 2:
                        updateCustomer();
                        break;
                    case 3:
                        deleteCustomer();
                        break;
                    case 4:
                        System.out.println("Exiting the system. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                choice = 0; // Reset choice to loop again
            }
        } while (choice != 4);
        scanner.close();
    }

    private static void displayMenu() {
        System.out.println("\n--- GYM MEMBERSHIP SYSTEM ---");
        System.out.println("1. Add Customer");
        System.out.println("2. Update/Edit Customer");
        System.out.println("3. Delete Customer");
        System.out.println("4. Exit");
    }

    private static void addCustomer() {
        System.out.println("\n--- Add New Customer ---");
        System.out.print("Customer Name: ");
        String name = scanner.nextLine();
        System.out.print("Customer Age: ");
        int age = getIntInput();
        System.out.print("Contact Number: ");
        String contactNumber = scanner.nextLine();

        Subscription subscription = selectSubscription();

        Date joinDate = getValidatedDate("Join Date (DD/MM/YY): ");
        Date expiryDate = calculateExpiryDate(joinDate, subscription.durationInMonths);

        String sql = "INSERT INTO customers(name, age, contact_number, subscription_type, subscription_cost, join_date, expiry_date) VALUES(?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = config.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.setString(3, contactNumber);
            pstmt.setString(4, subscription.type);
            pstmt.setDouble(5, subscription.cost);
            pstmt.setString(6, dateFormat.format(joinDate));
            pstmt.setString(7, dateFormat.format(expiryDate));
            pstmt.executeUpdate();
            System.out.println("Customer registered successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding customer: " + e.getMessage());
        }
    }

    private static void updateCustomer() {
        System.out.println("\n--- Update Customer ---");
        System.out.print("Enter Customer ID to Update: ");
        int customerId = getIntInput();

        // Check if customer exists
        if (!customerExists(customerId)) {
            System.out.println("Customer with ID " + customerId + " not found.");
            return;
        }

        System.out.print("Enter New Name: ");
        String newName = scanner.nextLine();
        System.out.print("Enter New Contact Number: ");
        String newContactNumber = scanner.nextLine();

        Subscription newSubscription = selectSubscription();
        Date newJoinDate = getValidatedDate("Enter New Join Date (DD/MM/YY): ");
        Date newExpiryDate = calculateExpiryDate(newJoinDate, newSubscription.durationInMonths);

        String sql = "UPDATE customers SET name = ?, contact_number = ?, subscription_type = ?, subscription_cost = ?, join_date = ?, expiry_date = ? WHERE customer_id = ?";

        try (Connection conn = config.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setString(2, newContactNumber);
            pstmt.setString(3, newSubscription.type);
            pstmt.setDouble(4, newSubscription.cost);
            pstmt.setString(5, dateFormat.format(newJoinDate));
            pstmt.setString(6, dateFormat.format(newExpiryDate));
            pstmt.setInt(7, customerId);
            pstmt.executeUpdate();
            System.out.println("Customer updated successfully.");
        } catch (SQLException e) {
            System.out.println("Error updating customer: " + e.getMessage());
        }
    }

    private static void deleteCustomer() {
        System.out.println("\n--- Delete Customer ---");
        System.out.print("Enter ID to Remove: ");
        int customerId = getIntInput();

        String sql = "DELETE FROM customers WHERE customer_id = ?";
        try (Connection conn = config.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Customer removed successfully.");
            } else {
                System.out.println("Customer with ID " + customerId + " not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting customer: " + e.getMessage());
        }
    }

    // Helper methods for user interaction and validation
    private static int getIntInput() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }

    private static Subscription selectSubscription() {
        System.out.println("\n--- Select a Subscription ---");
        Subscription[] options = {
                new Subscription("599 Php Monthly", 599.0, 1),
                new Subscription("1,599 Php for 3 Months", 1599.0, 3),
                new Subscription("3,399 Php for 6 Months", 3399.0, 6),
                new Subscription("Premium 5,199 Php for 9 Months", 5199.0, 9),
                new Subscription("Premium 6,999 Php for Yearly", 6999.0, 12)
        };

        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i].type);
        }
        
        int choice;
        while (true) {
            System.out.print("Select Subscription Type (1-5): ");
            choice = getIntInput();
            if (choice >= 1 && choice <= 5) {
                return options[choice - 1];
            } else {
                System.out.println("Invalid subscription choice.");
            }
        }
    }

    private static Date getValidatedDate(String prompt) {
        Date date = null;
        while (date == null) {
            System.out.print(prompt);
            String dateStr = scanner.nextLine();
            try {
                date = dateFormat.parse(dateStr);
            } catch (ParseException e) {
                System.out.println("Invalid date format. Please use DD/MM/YY.");
            }
        }
        return date;
    }

    private static Date calculateExpiryDate(Date joinDate, int months) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(joinDate);
        cal.add(java.util.Calendar.MONTH, months);
        return cal.getTime();
    }

    private static boolean customerExists(int customerId) {
        String sql = "SELECT COUNT(*) FROM customers WHERE customer_id = ?";
        try (Connection conn = config.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return false;
    }

    private static class Subscription {
        String type;
        double cost;
        int durationInMonths;

        public Subscription(String type, double cost, int durationInMonths) {
            this.type = type;
            this.cost = cost;
            this.durationInMonths = durationInMonths;
        }
    }
}