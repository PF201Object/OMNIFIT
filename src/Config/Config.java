package Config;

import java.sql.*;

public class Config {
    private static final String URL = "jdbc:sqlite:GMS.db";

    public static Connection connect() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.err.println("Connection Failed: " + e.getMessage());
            return null;
        }
    }

    public static void initializeDB() {
        // 1. Users Table
        String usersTable = "CREATE TABLE IF NOT EXISTS Users ("
                + "U_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "Username TEXT NOT NULL UNIQUE,"
                + "Password TEXT NOT NULL," 
                + "Email TEXT NOT NULL UNIQUE,"
                + "Contact_No TEXT,"
                + "Members_Status TEXT DEFAULT 'Active');";

        // 2. Management Table
        String managementTable = "CREATE TABLE IF NOT EXISTS Management ("
                + "Staffid INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "Username TEXT NOT NULL,"
                + "Role_Position TEXT,"
                + "Salary_PayRate REAL DEFAULT 0.0,"
                + "WorkEmail TEXT,"
                + "FOREIGN KEY (Username) REFERENCES Users(Username));";

        // 3. Members Table
        String membersTable = "CREATE TABLE IF NOT EXISTS Members ("
                + "M_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "Name TEXT NOT NULL,"
                + "Contact_No TEXT,"
                + "Email TEXT UNIQUE,"
                + "Join_date TEXT,"
                + "Membership_Status TEXT,"
                + "Membership_Type TEXT);";

        // 4. Services Table
        String servicesTable = "CREATE TABLE IF NOT EXISTS Services ("
                + "S_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "Service_Name TEXT,"
                + "Service_Type TEXT,"
                + "Payment_Status REAL DEFAULT 0.0,"
                + "Staff_Assigned TEXT," 
                + "Member_ID INTEGER,"    
                + "FOREIGN KEY (Staff_Assigned) REFERENCES Management(Username),"
                + "FOREIGN KEY (Member_ID) REFERENCES Members(M_ID));";

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(usersTable);
            stmt.execute(managementTable);
            stmt.execute(membersTable);
            stmt.execute(servicesTable);
            setupDefaultAdmin(); 
        } catch (SQLException e) {
            System.err.println("DB Init Error: " + e.getMessage());
        }
    }

    /**
     * UPDATED: Validates credentials and retrieves the user's role.
     * Uses a JOIN to check Users table for password and Management for Role.
     */
    public static String getUserRole(String username, String password) {
        String sql = "SELECT M.Role_Position FROM Users U " +
                     "JOIN Management M ON U.Username = M.Username " +
                     "WHERE U.Username = ? AND U.Password = ?";
        
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("Role_Position");
            }
        } catch (SQLException e) {
            System.err.println("Login Query Error: " + e.getMessage());
        }
        return null; // Return null if user/pass is wrong
    }

    public static boolean isUserExists(String username) {
        String check = "SELECT COUNT(*) FROM Users WHERE Username = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(check)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void setupDefaultAdmin() {
        if (!isUserExists("admin")) {
            // Added 'admin123' as default password
            String insertUser = "INSERT INTO Users(Username, Password, Email, Members_Status) VALUES('admin', 'admin123', 'admin@swift.com', 'Active')";
            String insertMgmt = "INSERT INTO Management(Username, Role_Position) VALUES('admin', 'Administrator')";
            
            try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
                stmt.execute(insertUser);
                stmt.execute(insertMgmt);
                System.out.println("Default Admin initialized (User: admin, Pass: admin123)");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean registerUser(String username, String pass, String email, String contact, String role, String status, double salary) {
        String userSql = "INSERT INTO Users(Username, Password, Email, Contact_No, Members_Status) VALUES(?,?,?,?,?)";
        // ADDED: Salary_PayRate to the columns and a fourth '?' placeholder
        String mgmtSql = "INSERT INTO Management(Username, Role_Position, WorkEmail, Salary_PayRate) VALUES(?,?,?,?)";

        try (Connection conn = connect()) {
            if (conn == null) return false;

            conn.setAutoCommit(false); 

            try (PreparedStatement pstmt1 = conn.prepareStatement(userSql)) {
                pstmt1.setString(1, username);
                pstmt1.setString(2, pass);
                pstmt1.setString(3, email);
                pstmt1.setString(4, contact);
                pstmt1.setString(5, status);
                pstmt1.executeUpdate();
            }

            try (PreparedStatement pstmt2 = conn.prepareStatement(mgmtSql)) {
                pstmt2.setString(1, username);
                pstmt2.setString(2, role);
                pstmt2.setString(3, email);
                // ADDED: Set the salary value in the prepared statement
                pstmt2.setDouble(4, salary); 
                pstmt2.executeUpdate();
            }

            conn.commit(); 
            return true;
        } catch (SQLException e) {
            System.err.println("Registration Error: " + e.getMessage());
            return false;
        }
    }
    public static boolean isEmailExists(String email) {
    String check = "SELECT COUNT(*) FROM Users WHERE Email = ?";
    try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(check)) {
        pstmt.setString(1, email);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next() && rs.getInt(1) > 0) return true;
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
    }
}