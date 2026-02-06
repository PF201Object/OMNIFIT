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
        // 1. Users Table Definition
        String usersTable = "CREATE TABLE IF NOT EXISTS Users ("
            + "U_ID TEXT PRIMARY KEY," 
            + "Username TEXT NOT NULL UNIQUE,"
            + "Password TEXT NOT NULL," 
            + "Email TEXT NOT NULL UNIQUE,"
            + "Contact_No TEXT,"
            + "Gender TEXT," 
            + "Members_Status TEXT DEFAULT 'Active',"
            + "Profile_Pic VARCHAR(255));";

        // 2. Management Table Definition
        String managementTable = "CREATE TABLE IF NOT EXISTS Management ("
                + "Staffid INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "Username TEXT NOT NULL,"
                + "Role_Position TEXT,"
                + "Salary_PayRate REAL DEFAULT 0.0,"
                + "WorkEmail TEXT,"
                + "FOREIGN KEY (Username) REFERENCES Users(Username));";

        // 3. Members Table Definition
        String membersTable = "CREATE TABLE IF NOT EXISTS Members ("
                + "M_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "Name TEXT NOT NULL,"
                + "Contact_No TEXT,"
                + "Email TEXT UNIQUE,"
                + "Join_date TEXT,"
                + "Membership_Status TEXT,"
                + "Membership_Type TEXT);";

        // 4. Services Table Definition
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
            // Execute table creations
            stmt.execute(usersTable);
            stmt.execute(managementTable);
            stmt.execute(membersTable);
            stmt.execute(servicesTable);
            
            // --- MIGRATION SCRIPT: Force add Gender if table already existed without it ---
            try {
                stmt.execute("ALTER TABLE Users ADD COLUMN Gender TEXT;");
                System.out.println("Migration: Gender column added to Users table.");
            } catch (SQLException e) {
                // Ignore error if column already exists
                if (!e.getMessage().contains("duplicate column name")) {
                    System.err.println("Migration Note: " + e.getMessage());
                }
            }
            
            setupDefaultAdmin(); 
        } catch (SQLException e) {
            System.err.println("DB Init Error: " + e.getMessage());
        }
    }

    public static boolean registerUser(String customID, String username, String pass, String email, 
                                     String contact, String gender, String role, String status, double salary) {
        
        if (isUserExists(username)) {
            System.err.println("Registration Failed: Username already exists.");
            return false;
        }
        if (isEmailExists(email)) {
            System.err.println("Registration Failed: Email already exists.");
            return false;
        }

        String userSql = "INSERT INTO Users(U_ID, Username, Password, Email, Contact_No, Gender, Members_Status) VALUES(?,?,?,?,?,?,?)";
        String mgmtSql = "INSERT INTO Management(Username, Role_Position, WorkEmail, Salary_PayRate) VALUES(?,?,?,?)";

        try (Connection conn = connect()) {
            if (conn == null) return false;
            conn.setAutoCommit(false); 

            try (PreparedStatement pstmt1 = conn.prepareStatement(userSql)) {
                pstmt1.setString(1, customID);
                pstmt1.setString(2, username);
                pstmt1.setString(3, pass);
                pstmt1.setString(4, email);
                pstmt1.setString(5, contact);
                pstmt1.setString(6, gender); 
                pstmt1.setString(7, status);
                pstmt1.executeUpdate();
            }

            try (PreparedStatement pstmt2 = conn.prepareStatement(mgmtSql)) {
                pstmt2.setString(1, username);
                pstmt2.setString(2, role); 
                pstmt2.setString(3, email);
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

    public static String getLastUserID() {
        String sql = "SELECT U_ID FROM Users ORDER BY U_ID DESC LIMIT 1";
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getString("U_ID");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching last ID: " + e.getMessage());
        }
        return "OMNI-00-1000"; 
    }

    public static boolean isUserExists(String username) {
        String check = "SELECT COUNT(*) FROM Users WHERE Username = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(check)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) { return false; }
    }

    public static boolean isEmailExists(String email) {
        String check = "SELECT COUNT(*) FROM Users WHERE Email = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(check)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) { return false; }
    }

    public static String getUserRole(String username, String password) {
        String sql = "SELECT M.Role_Position FROM Users U " +
                     "INNER JOIN Management M ON U.Username = M.Username " +
                     "WHERE U.Username = ? AND U.Password = ?";
        try (Connection conn = connect(); PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) return rs.getString("Role_Position");
        } catch (SQLException e) { }
        return null;
    }
    
    private static void setupDefaultAdmin() {
        if (!isUserExists("admin")) {
            registerUser("OMNI-00-1000", "admin", "admin123", "admin@Omni.com", "000", "Other", "Administrator", "Active", 50000.0);
        }
    }
}