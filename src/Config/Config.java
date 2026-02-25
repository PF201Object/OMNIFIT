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
            + "Role TEXT DEFAULT 'User',"
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
                + "Service_Name TEXT NOT NULL,"
                + "Service_Type TEXT,"
                + "Fee REAL DEFAULT 0.0," 
                + "Staff_Assigned TEXT,"    
                + "FOREIGN KEY (Staff_Assigned) REFERENCES Management(Username));";

        // 5. Payments Table for Members
        String paymentsTable = "CREATE TABLE IF NOT EXISTS Payments ("
                + "Payment_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "Amount REAL DEFAULT 0.0,"
                + "Payment_Date TEXT,"
                + "Payment_Method TEXT,"
                + "Payment_Status TEXT DEFAULT 'Pending',"
                + "Reference_Number TEXT,"
                + "Service_ID INTEGER,"
                + "FOREIGN KEY (Member_ID) REFERENCES Members(M_ID),"
                + "FOREIGN KEY (Service_ID) REFERENCES Services(S_ID));";

        // 6. Payroll Table for Staff
        String payrollTable = "CREATE TABLE IF NOT EXISTS Payroll ("
                + "Payroll_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "Staff_ID INTEGER,"
                + "Pay_Date TEXT,"
                + "Gross_Pay REAL DEFAULT 0.0,"
                + "Tax REAL DEFAULT 0.0,"
                + "Net_Pay REAL DEFAULT 0.0,"
                + "Bonus REAL DEFAULT 0.0,"
                + "Hours_Worked INTEGER DEFAULT 0,"
                + "Deductions REAL DEFAULT 0.0,"
                + "Status TEXT DEFAULT 'Pending',"
                + "Pay_Period_Start TEXT,"
                + "Pay_Period_End TEXT,"
                + "FOREIGN KEY (Staff_ID) REFERENCES Management(Staffid));";

        // 7. Transactions Table for Overall Tracking
        String transactionsTable = "CREATE TABLE IF NOT EXISTS Transactions ("
                + "Transaction_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "Transaction_Type TEXT,"
                + "Amount REAL,"
                + "Transaction_Date TEXT,"
                + "Member_ID INTEGER,"
                + "Staff_ID INTEGER,"
                + "Payment_ID INTEGER,"
                + "Payroll_ID INTEGER,"
                + "Description TEXT,"
                + "FOREIGN KEY (Member_ID) REFERENCES Members(M_ID),"
                + "FOREIGN KEY (Staff_ID) REFERENCES Management(Staffid),"
                + "FOREIGN KEY (Payment_ID) REFERENCES Payments(Payment_ID),"
                + "FOREIGN KEY (Payroll_ID) REFERENCES Payroll(Payroll_ID));";

        // 8. Receipts Table
        String receiptsTable = "CREATE TABLE IF NOT EXISTS Receipts ("
                + "Receipt_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "Transaction_ID INTEGER,"
                + "Receipt_Number TEXT UNIQUE,"
                + "Generated_Date TEXT,"
                + "Receipt_Data TEXT,"
                + "FOREIGN KEY (Transaction_ID) REFERENCES Transactions(Transaction_ID));";

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            // Execute table creations
            stmt.execute(usersTable);
            stmt.execute(managementTable);
            stmt.execute(membersTable);
            stmt.execute(servicesTable);
            stmt.execute(paymentsTable);
            stmt.execute(payrollTable);
            stmt.execute(transactionsTable);
            stmt.execute(receiptsTable);
            
            // --- MIGRATION SCRIPTS ---
            // Add Gender column if not exists
            try {
                        // This line specifically adds the Fee column you're missing
                        stmt.execute("ALTER TABLE Services ADD COLUMN Fee REAL DEFAULT 0.0;");
                        System.out.println("Migration: Fee column added to Services table.");
                    } catch (SQLException e) {
                        // If the column already exists, it will throw an error; we ignore it.
                        if (!e.getMessage().contains("duplicate column name")) {
                            System.err.println("Migration Error: " + e.getMessage());
                        }
                    }
            
            // Add indexes for better performance
            try {
                stmt.execute("CREATE INDEX IF NOT EXISTS idx_payments_member ON Payments(Member_ID);");
                stmt.execute("CREATE INDEX IF NOT EXISTS idx_payroll_staff ON Payroll(Staff_ID);");
                stmt.execute("CREATE INDEX IF NOT EXISTS idx_transactions_date ON Transactions(Transaction_Date);");
                System.out.println("Indexes created successfully.");
            } catch (SQLException e) {
                System.err.println("Index creation note: " + e.getMessage());
            }
            
            setupDefaultAdmin();
            setupDefaultServices();
            setupSampleData(); // Optional: Add sample data for testing
            
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

    // New method: Process Member Payment
    public static boolean processMemberPayment(int memberId, double amount, String method, int serviceId) {
        String sql = "INSERT INTO Payments (Member_ID, Amount, Payment_Date, Payment_Method, Payment_Status, Service_ID) " +
                    "VALUES (?, ?, date('now'), ?, 'Paid', ?)";
        
        try (Connection conn = connect(); PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, memberId);
            pst.setDouble(2, amount);
            pst.setString(3, method);
            pst.setInt(4, serviceId);
            
            int result = pst.executeUpdate();
            
            // Record in transactions
            if (result > 0) {
                recordTransaction("MEMBER_PAYMENT", amount, memberId, 0, null, null, 
                                 "Member payment processed");
            }
            
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Payment Error: " + e.getMessage());
            return false;
        }
    }

    // New method: Process Staff Payroll
    public static boolean processStaffPayroll(int staffId, double grossPay, double tax, double netPay, 
                                             double bonus, int hours, String periodStart, String periodEnd) {
        String sql = "INSERT INTO Payroll (Staff_ID, Pay_Date, Gross_Pay, Tax, Net_Pay, Bonus, " +
                    "Hours_Worked, Status, Pay_Period_Start, Pay_Period_End) " +
                    "VALUES (?, date('now'), ?, ?, ?, ?, ?, 'Paid', ?, ?)";
        
        try (Connection conn = connect(); PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, staffId);
            pst.setDouble(2, grossPay);
            pst.setDouble(3, tax);
            pst.setDouble(4, netPay);
            pst.setDouble(5, bonus);
            pst.setInt(6, hours);
            pst.setString(7, periodStart);
            pst.setString(8, periodEnd);
            
            int result = pst.executeUpdate();
            
            // Record in transactions
            if (result > 0) {
                recordTransaction("STAFF_PAYROLL", netPay, 0, staffId, null, null, 
                                 "Staff payroll processed");
            }
            
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Payroll Error: " + e.getMessage());
            return false;
        }
        
        
    }
    
    private static void setupDefaultServices() {
    String checkServices = "SELECT COUNT(*) FROM Services"; 
    String insertService = "INSERT INTO Services (Service_Name, Service_Type, Fee) VALUES (?, ?, ?)";

    try (Connection conn = connect(); 
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(checkServices)) {
        
        if (rs.next() && rs.getInt(1) == 0) {
            try (PreparedStatement pstmt = conn.prepareStatement(insertService)) {
                
                // --- Membership Access ---
                addServiceEntry(pstmt, "Daily Pass", "Gym Access", 100.0);
                addServiceEntry(pstmt, "Regular", "Membership", 800.0);
                addServiceEntry(pstmt, "Premium", "Membership", 1500.0);
                addServiceEntry(pstmt, "VIP", "Membership", 2500.0);

                // --- Personal Training ---
                addServiceEntry(pstmt, "Single Session", "Personal Training", 800.0);
                addServiceEntry(pstmt, "Monthly Package", "Personal Training", 5000.0);

                // --- Group Classes ---
                addServiceEntry(pstmt, "Zumba", "Group Class", 150.0);
                addServiceEntry(pstmt, "Yoga", "Group Class", 150.0);

                System.out.println("All Default Gym Services (Memberships, PT, and Classes) added.");
            }
        }
    } catch (SQLException e) {
        System.err.println("Error setting up default services: " + e.getMessage());
    }
}

// Small helper to keep the code clean and prevent repetition
private static void addServiceEntry(PreparedStatement pstmt, String name, String type, double fee) throws SQLException {
    pstmt.setString(1, name);
    pstmt.setString(2, type);
    pstmt.setDouble(3, fee);
    pstmt.executeUpdate();
}

    // New method: Record Transaction
    private static void recordTransaction(String type, double amount, int memberId, int staffId, 
                                         Integer paymentId, Integer payrollId, String description) {
        String sql = "INSERT INTO Transactions (Transaction_Type, Amount, Transaction_Date, " +
                    "Member_ID, Staff_ID, Payment_ID, Payroll_ID, Description) " +
                    "VALUES (?, ?, datetime('now'), ?, ?, ?, ?, ?)";
        
        try (Connection conn = connect(); PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, type);
            pst.setDouble(2, amount);
            pst.setInt(3, memberId > 0 ? memberId : 0);
            pst.setInt(4, staffId > 0 ? staffId : 0);
            
            if (paymentId != null) {
                pst.setInt(5, paymentId);
            } else {
                pst.setNull(5, Types.INTEGER);
            }
            
            if (payrollId != null) {
                pst.setInt(6, payrollId);
            } else {
                pst.setNull(6, Types.INTEGER);
            }
            
            pst.setString(7, description);
            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Transaction Record Error: " + e.getMessage());
        }
    }

    // New method: Generate Receipt
    public static String generateReceipt(int transactionId) {
        String receiptNumber = "RCP-" + System.currentTimeMillis();
        String sql = "INSERT INTO Receipts (Transaction_ID, Receipt_Number, Generated_Date, Receipt_Data) " +
                    "VALUES (?, ?, datetime('now'), ?)";
        
        try (Connection conn = connect(); PreparedStatement pst = conn.prepareStatement(sql)) {
            String receiptData = getReceiptData(transactionId);
            pst.setInt(1, transactionId);
            pst.setString(2, receiptNumber);
            pst.setString(3, receiptData);
            pst.executeUpdate();
            return receiptNumber;
        } catch (SQLException e) {
            System.err.println("Receipt Generation Error: " + e.getMessage());
            return null;
        }
    }

    // Helper method for receipt data
    private static String getReceiptData(int transactionId) {
        String sql = "SELECT * FROM Transactions WHERE Transaction_ID = ?";
        try (Connection conn = connect(); PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, transactionId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return String.format("Transaction: %d, Type: %s, Amount: %.2f, Date: %s",
                    rs.getInt("Transaction_ID"),
                    rs.getString("Transaction_Type"),
                    rs.getDouble("Amount"),
                    rs.getString("Transaction_Date"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting receipt data: " + e.getMessage());
        }
        return "";
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
        } catch (SQLException e) { 
            return false; 
        }
    }

    public static boolean isEmailExists(String email) {
        String check = "SELECT COUNT(*) FROM Users WHERE Email = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(check)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) { 
            return false; 
        }
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
        } catch (SQLException e) { 
            System.err.println("Error getting user role: " + e.getMessage());
        }
        return null;
    }
    
    public static boolean executeUpdate(String sql, Object... params) {
        try (Connection conn = connect(); PreparedStatement pst = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                pst.setObject(i + 1, params[i]);
            }
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update Error: " + e.getMessage());
            return false;
        }
    }
    
    private static void setupDefaultAdmin() {
        if (!isUserExists("admin")) {
            registerUser("OMNI-00-1000", "admin", "admin123", "admin@Omni.com", 
                        "000", "Other", "Administrator", "Active", 50000.0);
        }
    }
    
    private static void setupSampleData() {
        // Add sample members if none exist
        String checkMembers = "SELECT COUNT(*) FROM Members";
        try (Connection conn = connect(); 
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkMembers)) {
            
            if (rs.next() && rs.getInt(1) == 0) {
                // Add sample members
                String insertMembers = "INSERT INTO Members (Name, Contact_No, Email, Join_date, Membership_Status, Membership_Type) VALUES " +
                    "('John Doe', '123-456-7890', 'john@email.com', date('now'), 'Active', 'Premium')," +
                    "('Jane Smith', '123-456-7891', 'jane@email.com', date('now'), 'Active', 'Regular')," +
                    "('Bob Johnson', '123-456-7892', 'bob@email.com', date('now'), 'Active', 'VIP')";
                stmt.executeUpdate(insertMembers);
                System.out.println("Sample members added.");
            }
        } catch (SQLException e) {
            System.err.println("Error adding sample data: " + e.getMessage());
        }
    }
    
    public static ResultSet getServiceReceipt(int serviceId) {
        String sql = "SELECT S.S_ID, S.Service_Name, S.Service_Type, S.Payment_Status, S.Staff_Assigned, M.Name " +
                     "FROM Services S JOIN Members M ON S.Member_ID = M.M_ID WHERE S.S_ID = ?";
        try {
            Connection conn = connect();
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, serviceId);
            return pst.executeQuery();
        } catch (SQLException e) { 
            System.err.println("Error getting service receipt: " + e.getMessage());
            return null; 
        }
    }

    public static ResultSet getStaffReceipt(int staffId) {
        String sql = "SELECT Staffid, Username, Role_Position, Salary_PayRate, WorkEmail FROM Management WHERE Staffid = ?";
        try {
            Connection conn = connect();
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, staffId);
            return pst.executeQuery();
        } catch (SQLException e) { 
            System.err.println("Error getting staff receipt: " + e.getMessage());
            return null; 
        }
    }

    // New method: Get payment history for a member
    public static ResultSet getMemberPayments(int memberId) {
        String sql = "SELECT * FROM Payments WHERE Member_ID = ? ORDER BY Payment_Date DESC";
        try {
            Connection conn = connect();
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, memberId);
            return pst.executeQuery();
        } catch (SQLException e) {
            System.err.println("Error getting member payments: " + e.getMessage());
            return null;
        }
    }

    // New method: Get payroll history for a staff member
    public static ResultSet getStaffPayroll(int staffId) {
        String sql = "SELECT * FROM Payroll WHERE Staff_ID = ? ORDER BY Pay_Date DESC";
        try {
            Connection conn = connect();
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, staffId);
            return pst.executeQuery();
        } catch (SQLException e) {
            System.err.println("Error getting staff payroll: " + e.getMessage());
            return null;
        }
    }

    // New method: Get all transactions
    public static ResultSet getAllTransactions() {
        String sql = "SELECT * FROM Transactions ORDER BY Transaction_Date DESC";
        try {
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            System.err.println("Error getting transactions: " + e.getMessage());
            return null;
        }
    }
}