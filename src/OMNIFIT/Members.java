package OMNIFIT;

import Config.Config;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public final class Members extends javax.swing.JPanel {

    public Members() {
        initComponents();
        setOpaque(false);
        applyDashboardTheme(); 
        
        btnSearch.addActionListener(evt -> loadMemberData(searchField.getText()));
        searchField.addActionListener(evt -> loadMemberData(searchField.getText()));
        btnRefresh.addActionListener(evt -> {
            searchField.setText("");
            loadMemberData();
        });
    
        btnAdd.addActionListener(evt -> addMember());
        btnUpdate.addActionListener(evt -> updateMember());
        btnDelete.addActionListener(evt -> deleteMember());
        btnPayment.addActionListener(evt -> processPayment());
        
            tblEvents.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) { // Detection for double click
                    showFullMemberInfo();
                }
            }
        });
    }
        private void showFullMemberInfo() {
        int row = tblEvents.getSelectedRow();
        if (row < 0) return;

        // Get ID from the first column of the selected row
        int memberId = (int) tblEvents.getValueAt(row, 0);

        String sql = "SELECT m.*, s.Service_Name, s.Fee, s.Service_Type " +
                     "FROM Members m " +
                     "LEFT JOIN Services s ON m.S_ID = s.S_ID " +
                     "WHERE m.M_ID = ?";

        try (Connection conn = Config.connect(); 
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, memberId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                // Create a stylized panel for the info
                JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
                panel.setBackground(new Color(30, 30, 30));
                panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

                String[][] info = {
                    {"MEMBER ID:", String.valueOf(rs.getInt("M_ID"))},
                    {"FULL NAME:", rs.getString("Name")},
                    {"CONTACT:", rs.getString("Contact_No")},
                    {"EMAIL:", rs.getString("Email")},
                    {"JOIN DATE:", rs.getString("Join_date")},
                    {"EXPIRY DATE:", rs.getString("Expiry_date")},
                    {"STATUS:", rs.getString("Membership_Status")},
                    {"SERVICE:", rs.getString("Service_Name") + " (" + rs.getString("Service_Type") + ")"},
                    {"MONTHLY FEE:", "₱" + rs.getDouble("Fee")}
                };

                for (String[] item : info) {
                    JLabel lbl = new JLabel("<html><font color='#FF6600'><b>" + item[0] + "</b></font> " +
                                           "<font color='white'>" + (item[1] != null ? item[1] : "N/A") + "</font></html>");
                    lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    panel.add(lbl);
                }

                JOptionPane.showMessageDialog(this, panel, "Member Profile: " + rs.getString("Name"), JOptionPane.PLAIN_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching profile: " + e.getMessage());
        }
    }

    private void applyDashboardTheme() {
        tableScroll.setBorder(new EmptyBorder(0, 0, 0, 0));
        tableScroll.setOpaque(false);
        tableScroll.getViewport().setOpaque(false);

        tblEvents.setRowHeight(35);
        tblEvents.setShowGrid(false);
        tblEvents.setIntercellSpacing(new Dimension(0, 0));
        
        tblEvents.setBackground(new Color(30, 30, 30)); 
        tblEvents.setForeground(new Color(220, 220, 220)); 
        tblEvents.setSelectionBackground(new Color(255, 102, 0, 100));
        
        tblEvents.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblEvents.getTableHeader().setBackground(new Color(25, 25, 25)); 
        tblEvents.getTableHeader().setForeground(new Color(180, 180, 180));
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setOpaque(true); 
        tblEvents.setDefaultRenderer(Object.class, centerRenderer);
        
        // Style buttons
        styleButtons();
    }
    
    private void styleButtons() {
        Color buttonBg = new Color(255, 102, 0);
        Color buttonFg = Color.WHITE;
        
        JButton[] buttons = {btnAdd, btnUpdate, btnDelete, btnRefresh, 
                             btnPayment, btnAuth, btnSearch, btnTransaction};
        
        for (JButton btn : buttons) {
            if (btn != null) {
                btn.setBackground(buttonBg);
                btn.setForeground(buttonFg);
                btn.setFocusPainted(false);
                btn.setBorderPainted(false);
                btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        }
        
        searchField.setBackground(new Color(45, 45, 45));
        searchField.setForeground(Color.WHITE);
        searchField.setCaretColor(Color.WHITE);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 102, 0)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
    }

    public void loadMemberData() {
        loadMemberData(null);
    }
    
    private String calculateExpiry(String serviceName) {
    java.util.Calendar cal = java.util.Calendar.getInstance();
    
    switch (serviceName) {
        // --- 1 Day / Single Session ---
        case "Daily Pass":
        case "Zumba":
        case "Yoga":
        case "Single Session":
            cal.add(java.util.Calendar.DAY_OF_MONTH, 1);
            break;
            
        // --- 1 Month ---
        case "Regular":
        case "Monthly Package":
            cal.add(java.util.Calendar.MONTH, 1);
            break;
            
        // --- 6 Months ---
        case "Premium":
            cal.add(java.util.Calendar.MONTH, 6);
            break;
            
        // --- 1 Year ---
        case "VIP":
            cal.add(java.util.Calendar.YEAR, 1);
            break;
            
        default:
            cal.add(java.util.Calendar.MONTH, 1); // Default fallback
            break;
    }
    return new java.text.SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
}

public void loadMemberData(String searchTerm) {
    String[] columnNames = {"ID", "Name", "Contact", "Email", "Joined", "Expiry", "Status", "Service Type", "Payment Status"};
    DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };
    
    String sql = "SELECT m.M_ID, m.Name, m.Contact_No, m.Email, m.Join_date, m.Expiry_date, " +
                 "m.Membership_Status, s.Service_Name, " +
                 "CASE WHEN EXISTS (SELECT 1 FROM Payments p " +
                 "                  WHERE p.Member_ID = m.M_ID " +
                 "                  AND p.Service_ID = m.S_ID " +
                 "                  AND p.Payment_Status = 'Paid') " +
                 "THEN 'Paid' ELSE 'Pending' END as Payment_Status " +
                 "FROM Members m " +
                 "LEFT JOIN Services s ON m.S_ID = s.S_ID";
    
    if (searchTerm != null && !searchTerm.trim().isEmpty()) {
        sql += " WHERE m.Name LIKE ? OR m.Email LIKE ? OR s.Service_Name LIKE ?";
    }

    try (Connection conn = Config.connect();
         PreparedStatement pst = conn.prepareStatement(sql)) {
        
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            String pattern = "%" + searchTerm + "%";
            pst.setString(1, pattern);
            pst.setString(2, pattern);
            pst.setString(3, pattern);
        }

        try (ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("M_ID"),
                    rs.getString("Name"),
                    rs.getString("Contact_No"),
                    rs.getString("Email"),
                    rs.getString("Join_date"),
                    rs.getString("Expiry_date"),
                    rs.getString("Membership_Status"),
                    rs.getString("Service_Name"),
                    rs.getString("Payment_Status")
                });
            }
        }
        tblEvents.setModel(model);

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
    }
}
// Helper class to store Service data in the JComboBox
private class ServiceItem {
    int id;
    String name;
    double fee;

    public ServiceItem(int id, String name, double fee) {
        this.id = id;
        this.name = name;
        this.fee = fee;
    }

    @Override
    public String toString() {
        return name + " (₱" + fee + ")";
    }
}

private void addMember() {
    JTextField nameField = new JTextField();
    JTextField contactField = new JTextField();
    JTextField emailField = new JTextField();
    String today = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
    
    JComboBox<ServiceItem> serviceBox = new JComboBox<>();
    try (Connection conn = Config.connect(); 
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT S_ID, Service_Name, Fee FROM Services")) {
        while (rs.next()) {
            serviceBox.addItem(new ServiceItem(rs.getInt("S_ID"), rs.getString("Service_Name"), rs.getDouble("Fee")));
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error loading services: " + e.getMessage());
    }

    Object[] message = { "Name:", nameField, "Contact:", contactField, "Email:", emailField, "Service:", serviceBox };

    int option = JOptionPane.showConfirmDialog(this, message, "Add New Member", JOptionPane.OK_CANCEL_OPTION);
    
    if (option == JOptionPane.OK_OPTION) {
        ServiceItem selected = (ServiceItem) serviceBox.getSelectedItem();
        String expiryDate = calculateExpiry(selected.name); // <--- LOGIC APPLIED HERE

        String sql = "INSERT INTO Members (Name, Contact_No, Email, Join_date, Expiry_date, Membership_Status, S_ID) VALUES (?, ?, ?, ?, ?, 'Active', ?)";

        try (Connection conn = Config.connect(); PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, nameField.getText());
            pst.setString(2, contactField.getText());
            pst.setString(3, emailField.getText());
            pst.setString(4, today);
            pst.setString(5, expiryDate);
            pst.setInt(6, selected.id);

            pst.executeUpdate();
            loadMemberData();
            JOptionPane.showMessageDialog(this, "Member added! Expiry: " + expiryDate);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}

private void updateMember() {
    int selectedRow = tblEvents.getSelectedRow();
    if (selectedRow < 0) return;

    int id = (int) tblEvents.getValueAt(selectedRow, 0);
    JTextField nameField = new JTextField((String) tblEvents.getValueAt(selectedRow, 1));
    nameField.setEditable(false);
    nameField.setBackground(new Color(200, 200, 200));

    JTextField contactField = new JTextField((String) tblEvents.getValueAt(selectedRow, 2));
    JTextField emailField = new JTextField((String) tblEvents.getValueAt(selectedRow, 3));
    JComboBox<String> statusBox = new JComboBox<>(new String[]{"Active", "Inactive"});
    statusBox.setSelectedItem(tblEvents.getValueAt(selectedRow, 6));

    JComboBox<ServiceItem> serviceBox = new JComboBox<>();
    String currentServiceName = (String) tblEvents.getValueAt(selectedRow, 7);
    int currentServiceId = -1;

    try (Connection conn = Config.connect(); 
         PreparedStatement pst = conn.prepareStatement("SELECT S_ID FROM Members WHERE M_ID = ?")) {
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            currentServiceId = rs.getInt("S_ID");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    try (Connection conn = Config.connect(); Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT S_ID, Service_Name, Fee FROM Services")) {
        while (rs.next()) {
            ServiceItem item = new ServiceItem(rs.getInt("S_ID"), rs.getString("Service_Name"), rs.getDouble("Fee"));
            serviceBox.addItem(item);
            if (item.name.equals(currentServiceName)) serviceBox.setSelectedItem(item);
        }
    } catch (SQLException e) {}

    Object[] message = { "Name:", nameField, "Contact:", contactField, "Email:", emailField, "Status:", statusBox, "Update Service:", serviceBox };

    if (JOptionPane.showConfirmDialog(this, message, "Update Member", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
        ServiceItem selected = (ServiceItem) serviceBox.getSelectedItem();
        boolean serviceChanged = selected.id != currentServiceId;

        try (Connection conn = Config.connect()) {
            conn.setAutoCommit(false); // Start transaction

            // 1. Update Member Details
            String sql = "UPDATE Members SET Contact_No=?, Email=?, Membership_Status=?, S_ID=?, Expiry_date=? WHERE M_ID=?";
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, contactField.getText());
                pst.setString(2, emailField.getText());
                pst.setString(3, statusBox.getSelectedItem().toString());
                pst.setInt(4, selected.id);
                pst.setString(5, calculateExpiry(selected.name));
                pst.setInt(6, id);
                pst.executeUpdate();
            }

            // 2. Reset ONLY the CURRENT member's payment status to Pending if service changed
            // This will affect how it displays in the Members table, but previous payments remain Paid in history
            if (serviceChanged) {
                // Update the payment status for this specific member and new service to Pending
                String sqlResetStatus = "UPDATE Payments SET Payment_Status='Pending' WHERE Member_ID=? AND Service_ID=? AND Payment_Status='Paid'";
                try (PreparedStatement pstReset = conn.prepareStatement(sqlResetStatus)) {
                    pstReset.setInt(1, id);
                    pstReset.setInt(2, selected.id);
                    pstReset.executeUpdate();
                }
            }

            conn.commit();
            loadMemberData();
            JOptionPane.showMessageDialog(this, "Updated! Service change detected: Payment status reset to Pending.");
        } catch (SQLException e) {
            try (Connection conn = Config.connect()) {
                conn.rollback();
            } catch (SQLException ex) {}
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}

    private void deleteMember() {
        int selectedRow = tblEvents.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a member to delete");
            return;
        }

        int id = (int) tblEvents.getValueAt(selectedRow, 0);
        String name = (String) tblEvents.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete " + name + "?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM Members WHERE M_ID=?";
            
            try (Connection conn = Config.connect();
                 PreparedStatement pst = conn.prepareStatement(sql)) {
                
                pst.setInt(1, id);
                pst.executeUpdate();
                loadMemberData();
                JOptionPane.showMessageDialog(this, "Member deleted successfully!");
                
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting member: " + e.getMessage());
            }
        }
    }

// Replace your existing processPayment method with this updated version:
private void processPayment() {
    int selectedRow = tblEvents.getSelectedRow();
    if (selectedRow < 0) {
        JOptionPane.showMessageDialog(this, "Please select a member first.");
        return;
    }

    // 1. Get Member Data from Table
    int memberId = (int) tblEvents.getValueAt(selectedRow, 0);
    String memberName = (String) tblEvents.getValueAt(selectedRow, 1);
    String serviceName = (String) tblEvents.getValueAt(selectedRow, 7);
    String currentPaymentStatus = (String) tblEvents.getValueAt(selectedRow, 8);

    // 2. Fetch the current Fee and current S_ID from DB
    double amountDue = 0;
    int currentServiceId = -1;
    
    try (Connection conn = Config.connect(); 
         PreparedStatement pst = conn.prepareStatement("SELECT m.S_ID, s.Fee FROM Members m JOIN Services s ON m.S_ID = s.S_ID WHERE m.M_ID = ?")) {
        pst.setInt(1, memberId);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            currentServiceId = rs.getInt("S_ID");
            amountDue = rs.getDouble("Fee");
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error fetching fee: " + e.getMessage());
        return; 
    }

    // 3. Check if payment is already Paid
    if (currentPaymentStatus.equals("Paid")) {
        int viewReceipt = JOptionPane.showConfirmDialog(this, 
            "This payment is already marked as PAID.\n\nDo you want to view the receipt?", 
            "Payment Already Processed", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.QUESTION_MESSAGE);
        
        if (viewReceipt == JOptionPane.YES_OPTION) {
            showReceipt(memberId, memberName);
        }
        return;
    }

    // 4. Get current user role from Config
    String currentUserRole = Config.getCurrentUserRole();
    String currentUsername = Config.getCurrentUsername();
    
    // 5. Prepare UI Components
    JTextField amountField = new JTextField("₱" + String.format("%.2f", amountDue));
    amountField.setEditable(false);
    amountField.setBackground(new Color(220, 220, 220));
    
    JTextField receivedField = new JTextField();
    JComboBox<String> methodBox = new JComboBox<>(new String[]{"Cash", "GCash", "PayMaya", "Bank Transfer"});
    
    // Admin authorization checkbox (only shown if user is not Admin)
    JCheckBox requireAuthCheckBox = new JCheckBox("Require Admin Authorization");
    
    JLabel changeLabel = new JLabel("Change: ₱0.00");
    changeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
    changeLabel.setForeground(new Color(255, 102, 0));

    // Live Change Calculation
    double finalAmountDue = amountDue;
    receivedField.addKeyListener(new java.awt.event.KeyAdapter() {
        @Override
        public void keyReleased(java.awt.event.KeyEvent evt) {
            try {
                String input = receivedField.getText().trim();
                if (input.isEmpty()) {
                    changeLabel.setText("Change: ₱0.00");
                    return;
                }
                double received = Double.parseDouble(input);
                double change = received - finalAmountDue;
                changeLabel.setText(String.format("Change: ₱%.2f", change));
                changeLabel.setForeground(change >= 0 ? new Color(0, 153, 51) : Color.RED);
            } catch (NumberFormatException e) {
                changeLabel.setText("Invalid Amount");
                changeLabel.setForeground(Color.RED);
            }
        }
    });

    // Build message dynamically based on user role
    java.util.List<Object> messageList = new java.util.ArrayList<>();
    messageList.add("Member: " + memberName);
    messageList.add("Service: " + serviceName);
    messageList.add("Amount Due:");
    messageList.add(amountField);
    messageList.add("Payment Method:");
    messageList.add(methodBox);
    messageList.add("Amount Received:");
    messageList.add(receivedField);
    
    // Add authorization checkbox if user is not Admin
    if (!"Administrator".equals(currentUserRole) && !"Admin".equals(currentUserRole)) {
        messageList.add(" ");
        messageList.add(requireAuthCheckBox);
    }
    
    messageList.add(" ");
    messageList.add(changeLabel);

    Object[] message = messageList.toArray();

    int option = JOptionPane.showConfirmDialog(this, message, "Process Payment", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    
    if (option == JOptionPane.OK_OPTION) {
        try {
            double received = Double.parseDouble(receivedField.getText().trim());
            if (received < finalAmountDue) {
                JOptionPane.showMessageDialog(this, "Insufficient amount received!");
                return;
            }

            // Determine payment status based on authorization
            String paymentStatus = "Paid";
            String notes = "Processed by: " + currentUsername + " (" + currentUserRole + ")";
            
            // If user is not admin and requires authorization, set status to Pending
            if (!"Administrator".equals(currentUserRole) && !"Admin".equals(currentUserRole) && requireAuthCheckBox.isSelected()) {
                paymentStatus = "Pending";
                notes = "Pending Authorization - " + notes;
            }

            try (Connection conn = Config.connect()) {
                conn.setAutoCommit(false);
                
                // Check if there's an existing Pending payment for this member and service
                String checkSql = "SELECT Payment_ID FROM Payments WHERE Member_ID = ? AND Service_ID = ? AND Payment_Status = 'Pending'";
                int existingPaymentId = -1;
                
                try (PreparedStatement checkPst = conn.prepareStatement(checkSql)) {
                    checkPst.setInt(1, memberId);
                    checkPst.setInt(2, currentServiceId);
                    ResultSet rs = checkPst.executeQuery();
                    if (rs.next()) {
                        existingPaymentId = rs.getInt("Payment_ID");
                    }
                }
                
                String paymentDate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
                
                if (existingPaymentId != -1) {
                    // Update existing pending payment
                    String updateSql = "UPDATE Payments SET Payment_Status=?, Payment_Date=?, Payment_Method=?, Notes=? WHERE Payment_ID=?";
                    try (PreparedStatement updatePst = conn.prepareStatement(updateSql)) {
                        updatePst.setString(1, paymentStatus);
                        updatePst.setString(2, paymentDate);
                        updatePst.setString(3, methodBox.getSelectedItem().toString());
                        updatePst.setString(4, notes);
                        updatePst.setInt(5, existingPaymentId);
                        updatePst.executeUpdate();
                    }
                } else {
                    // Insert new payment record
                    String insertSql = "INSERT INTO Payments (Member_ID, Service_ID, Amount, Payment_Date, Payment_Method, Payment_Status, Notes) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement insertPst = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                        insertPst.setInt(1, memberId);
                        insertPst.setInt(2, currentServiceId);
                        insertPst.setDouble(3, finalAmountDue);
                        insertPst.setString(4, paymentDate);
                        insertPst.setString(5, methodBox.getSelectedItem().toString());
                        insertPst.setString(6, paymentStatus);
                        insertPst.setString(7, notes);
                        insertPst.executeUpdate();
                        
                        // Get the generated payment ID
                        ResultSet generatedKeys = insertPst.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            existingPaymentId = generatedKeys.getInt(1);
                        }
                    }
                }
                
                conn.commit();
                loadMemberData(); // Refresh the Members table
                
                // Show appropriate message
                if ("Paid".equals(paymentStatus)) {
                    JOptionPane.showMessageDialog(this, "Payment processed successfully!");
                    if (existingPaymentId != -1) {
                        showReceipt(memberId, memberName);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Payment submitted for Admin Authorization. Status: Pending");
                }
                
            } catch (SQLException e) {
                try (Connection conn = Config.connect()) {
                    conn.rollback();
                } catch (SQLException ex) {}
                JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric amount.");
        }
    }
}

    public void updateButtonVisibility(boolean isAdmin) {
        // Show AUTH button only for admin users
        if (btnAuth != null) {
            btnAuth.setVisible(isAdmin);
        }
    } 

// Add this helper method to show receipt (replace your existing viewReceipt method):
// Add this helper method to show receipt (replace your existing showReceipt method):
private void showReceipt(int memberId, String memberName) {
    try (Connection conn = Config.connect(); 
         PreparedStatement pst = conn.prepareStatement(
            "SELECT p.*, s.Service_Name " +
            "FROM Payments p " +
            "LEFT JOIN Services s ON p.Service_ID = s.S_ID " +
            "WHERE p.Member_ID = ? ORDER BY p.Payment_Date DESC LIMIT 1")) {
        
        pst.setInt(1, memberId);
        ResultSet rs = pst.executeQuery();
        
        if (rs.next()) {
            String notes = rs.getString("Notes");
            String authInfo = "";
            if (notes != null && !notes.isEmpty()) {
                authInfo = "\n" + notes;
            }
            
            String receiptText = "╔════════════════════════════════╗\n" +
                                 "║      OMNIFIT GYM CENTER       ║\n" +
                                 "║        OFFICIAL RECEIPT        ║\n" +
                                 "╠════════════════════════════════╣\n" +
                                 "║ Receipt #: " + padRight(String.valueOf(rs.getInt("Payment_ID")), 20) + " ║\n" +
                                 "║ Date: " + padRight(rs.getString("Payment_Date"), 22) + " ║\n" +
                                 "╠════════════════════════════════╣\n" +
                                 "║ MEMBER DETAILS:                ║\n" +
                                 "║ ID: " + padRight(String.valueOf(memberId), 26) + " ║\n" +
                                 "║ Name: " + padRight(memberName, 24) + " ║\n" +
                                 "║ Service: " + padRight(rs.getString("Service_Name"), 22) + " ║\n" +
                                 "╠════════════════════════════════╣\n" +
                                 "║ PAYMENT DETAILS:               ║\n" +
                                 "║ Amount: ₱" + padRight(String.format("%.2f", rs.getDouble("Amount")), 19) + " ║\n" +
                                 "║ Method: " + padRight(rs.getString("Payment_Method"), 22) + " ║\n" +
                                 "║ Status: " + padRight(rs.getString("Payment_Status"), 22) + " ║\n" +
                                 "╠════════════════════════════════╣\n" +
                                 "║      PAID IN FULL               ║\n" +
                                 "║    THANK YOU FOR CHOOSING US!   ║\n" +
                                 "╚════════════════════════════════╝" +
                                 authInfo;
            
            JTextArea textArea = new JTextArea(receiptText);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            textArea.setEditable(false);
            textArea.setBackground(new Color(45, 45, 45));
            textArea.setForeground(new Color(0, 255, 0));
            textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(new Color(45, 45, 45));
            panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
            
            JButton printBtn = new JButton("PRINT RECEIPT");
            printBtn.setBackground(new Color(255, 102, 0));
            printBtn.setForeground(Color.WHITE);
            printBtn.setFocusPainted(false);
            printBtn.setBorderPainted(false);
            printBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            printBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            printBtn.addActionListener(e -> {
                try {
                    textArea.print();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Print failed: " + ex.getMessage());
                }
            });
            
            panel.add(printBtn, BorderLayout.SOUTH);

            JOptionPane.showMessageDialog(this, panel, "OFFICIAL RECEIPT", JOptionPane.PLAIN_MESSAGE);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error generating receipt: " + e.getMessage());
    }
}

// Add this helper method for text padding:
private String padRight(String text, int length) {
    if (text == null) text = "";
    return String.format("%-" + length + "s", text);
}

private boolean verifyAdminCredentials(String username, String password) {
    String sql = "SELECT Role_Position FROM Users u " +
                 "INNER JOIN Management m ON u.Username = m.Username " +
                 "WHERE u.Username = ? AND u.Password = ? AND (m.Role_Position = 'Admin' OR m.Role_Position = 'Administrator')";
    
    try (Connection conn = Config.connect();
         PreparedStatement pst = conn.prepareStatement(sql)) {
        
        pst.setString(1, username);
        pst.setString(2, password);
        
        ResultSet rs = pst.executeQuery();
        return rs.next();
        
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(10, 10, 10, 180)); 
        g2.fillRoundRect(10, 10, getWidth()-20, getHeight()-20, 30, 30); 
        g2.dispose();
        super.paintComponent(g);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitle = new javax.swing.JLabel();
        topPanel = new javax.swing.JPanel();
        searchPanel = new javax.swing.JPanel();
        searchField = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        tableScroll = new javax.swing.JScrollPane();
        tblEvents = new javax.swing.JTable();
        buttonPanel = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnPayment = new javax.swing.JButton();
        btnTransaction = new javax.swing.JButton();
        btnAuth = new javax.swing.JButton();

        setOpaque(false);
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTitle.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText("GYM MEMBERS");
        add(lblTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 290, 30));

        topPanel.setOpaque(false);
        topPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 0));

        searchPanel.setOpaque(false);
        searchPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));

        searchField.setPreferredSize(new java.awt.Dimension(100, 30));
        searchPanel.add(searchField);

        btnSearch.setText("Search");
        searchPanel.add(btnSearch);

        btnRefresh.setText("Refresh");
        searchPanel.add(btnRefresh);

        topPanel.add(searchPanel);

        add(topPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 20, 270, 40));

        tableScroll.setBorder(null);
        tableScroll.setOpaque(false);

        tblEvents.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Name", "Contact", "Email", "Joined", "Status", "Type", "Payment Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tableScroll.setViewportView(tblEvents);

        add(tableScroll, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, 540, 250));

        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 0));

        btnAdd.setText("ADD");
        buttonPanel.add(btnAdd);

        btnUpdate.setText("UPDATE");
        buttonPanel.add(btnUpdate);

        btnDelete.setText("DELETE");
        buttonPanel.add(btnDelete);

        btnPayment.setText("PAYMENT");
        buttonPanel.add(btnPayment);

        btnTransaction.setText("Transaction");
        btnTransaction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTransactionActionPerformed(evt);
            }
        });
        buttonPanel.add(btnTransaction);

        btnAuth.setText("AUTH");
        btnAuth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAuthActionPerformed(evt);
            }
        });
        buttonPanel.add(btnAuth);

        add(buttonPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 320, 540, 40));
    }// </editor-fold>//GEN-END:initComponents

    private void btnTransactionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTransactionActionPerformed
int selectedRow = tblEvents.getSelectedRow();
    if (selectedRow < 0) {
        JOptionPane.showMessageDialog(this, "Please select a member to view their transaction history.");
        return;
    }

    int memberId = (int) tblEvents.getValueAt(selectedRow, 0);
    String memberName = (String) tblEvents.getValueAt(selectedRow, 1);

    // Create a Table for History
    String[] columns = {"Payment ID", "Date", "Amount", "Method", "Status"};
    DefaultTableModel historyModel = new DefaultTableModel(columns, 0);
    JTable historyTable = new JTable(historyModel);
    
    // Style the history table to match your theme
    historyTable.setBackground(new Color(45, 45, 45));
    historyTable.setForeground(Color.WHITE);
    historyTable.setGridColor(new Color(60, 60, 60));

    String sql = "SELECT Payment_ID, Payment_Date, Amount, Payment_Method, Payment_Status " +
                 "FROM Payments WHERE Member_ID = ? ORDER BY Payment_Date DESC";

    try (Connection conn = Config.connect();
         PreparedStatement pst = conn.prepareStatement(sql)) {
        
        pst.setInt(1, memberId);
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            historyModel.addRow(new Object[]{
                rs.getInt("Payment_ID"),
                rs.getString("Payment_Date"),
                "₱" + rs.getDouble("Amount"),
                rs.getString("Payment_Method"),
                rs.getString("Payment_Status")
            });
        }

        if (historyModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No transaction history found for " + memberName);
        } else {
            JScrollPane scrollPane = new JScrollPane(historyTable);
            scrollPane.setPreferredSize(new Dimension(500, 300));
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            
            JPanel container = new JPanel(new BorderLayout());
            container.setBackground(new Color(30, 30, 30));
            container.add(new JLabel("<html><body style='padding:5px;'><b style='color:#FF6600;'>Transaction History for:</b> <span style='color:white;'>" + memberName + "</span></body></html>"), BorderLayout.NORTH);
            container.add(scrollPane, BorderLayout.CENTER);

            JOptionPane.showMessageDialog(this, container, "Transaction Records", JOptionPane.PLAIN_MESSAGE);
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error loading transactions: " + e.getMessage());
    }
    }//GEN-LAST:event_btnTransactionActionPerformed

    private void btnAuthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAuthActionPerformed
    String currentUserRole = Config.getCurrentUserRole();
    if (!"Administrator".equals(currentUserRole) && !"Admin".equals(currentUserRole)) {
        JOptionPane.showMessageDialog(this, "Access Denied: Only Administrators can authorize payments.");
        return;
    }
    
    // Create dialog to show pending payments
    JDialog authDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Payment Authorization", true);
    authDialog.setLayout(new BorderLayout());
    authDialog.setSize(800, 500);
    authDialog.setLocationRelativeTo(this);
    
    // Create table for pending payments
    String[] columns = {"Payment ID", "Member ID", "Member Name", "Service", "Amount", "Date", "Requested By", "Action"};
    DefaultTableModel model = new DefaultTableModel(columns, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 7; // Only Action column editable
        }
    };
    
    JTable pendingTable = new JTable(model);
    pendingTable.setRowHeight(35);
    pendingTable.setBackground(new Color(45, 45, 45));
    pendingTable.setForeground(Color.WHITE);
    pendingTable.setGridColor(new Color(60, 60, 60));
    pendingTable.getTableHeader().setBackground(new Color(25, 25, 25));
    pendingTable.getTableHeader().setForeground(new Color(180, 180, 180));
    
    // Add button column for authorization
    pendingTable.getColumnModel().getColumn(7).setCellRenderer(new ButtonRenderer());
    pendingTable.getColumnModel().getColumn(7).setCellEditor(new ButtonEditor(new JCheckBox()));
    
    // Load pending payments
    loadPendingPayments(model);
    
    JScrollPane scrollPane = new JScrollPane(pendingTable);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    
    // Refresh button
    JButton refreshBtn = new JButton("REFRESH");
    refreshBtn.setBackground(new Color(255, 102, 0));
    refreshBtn.setForeground(Color.WHITE);
    refreshBtn.setFocusPainted(false);
    refreshBtn.setBorderPainted(false);
    refreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
    refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    refreshBtn.addActionListener(e -> {
        model.setRowCount(0);
        loadPendingPayments(model);
    });
    
    JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    topPanel.setBackground(new Color(30, 30, 30));
    topPanel.add(refreshBtn);
    
    authDialog.add(topPanel, BorderLayout.NORTH);
    authDialog.add(scrollPane, BorderLayout.CENTER);
    authDialog.setVisible(true);
}

private void loadPendingPayments(DefaultTableModel model) {
    String sql = "SELECT p.Payment_ID, p.Member_ID, m.Name as Member_Name, " +
                 "s.Service_Name, p.Amount, p.Payment_Date, p.Notes " +
                 "FROM Payments p " +
                 "LEFT JOIN Members m ON p.Member_ID = m.M_ID " +
                 "LEFT JOIN Services s ON p.Service_ID = s.S_ID " +
                 "WHERE p.Payment_Status = 'Pending' " +
                 "ORDER BY p.Payment_Date ASC";
    
    try (Connection conn = Config.connect();
         PreparedStatement pst = conn.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {
        
        while (rs.next()) {
            String notes = rs.getString("Notes");
            String requestedBy = "Unknown";
            if (notes != null && notes.contains("Processed by:")) {
                requestedBy = notes.replace("Pending Authorization - ", "").replace("Processed by:", "").trim();
            }
            
            model.addRow(new Object[]{
                rs.getInt("Payment_ID"),
                rs.getInt("Member_ID"),
                rs.getString("Member_Name"),
                rs.getString("Service_Name"),
                "₱" + rs.getDouble("Amount"),
                rs.getString("Payment_Date"),
                requestedBy,
                "AUTHORIZE"
            });
        }
        
        if (model.getRowCount() == 0) {
            model.addRow(new Object[]{"No pending payments found", "", "", "", "", "", "", ""});
        }
        
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error loading pending payments: " + e.getMessage());
    }
}

// Custom button renderer and editor for the Auth button
class ButtonRenderer extends JButton implements TableCellRenderer {
    public ButtonRenderer() {
        setOpaque(true);
        setBackground(new Color(255, 102, 0));
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setBorderPainted(false);
        setFont(new Font("Segoe UI", Font.BOLD, 11));
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        setText((value == null) ? "" : value.toString());
        return this;
    }
}

class ButtonEditor extends DefaultCellEditor {
    protected JButton button;
    private String label;
    private boolean isPushed;
    private int selectedRow;
    private int paymentId;
    private JTable table;
    
    public ButtonEditor(JCheckBox checkBox) {
        super(checkBox);
        button = new JButton();
        button.setOpaque(true);
        button.setBackground(new Color(255, 102, 0));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 11));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addActionListener(e -> {
            fireEditingStopped();
            
            if (table != null && selectedRow >= 0) {
                paymentId = (int) table.getValueAt(selectedRow, 0);
                String memberName = (String) table.getValueAt(selectedRow, 2);
                
                int confirm = JOptionPane.showConfirmDialog(button, 
                    "Authorize payment for " + memberName + "?", 
                    "Confirm Authorization", 
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    authorizePayment(paymentId);
                    // Refresh the table
                    ((DefaultTableModel) table.getModel()).setRowCount(0);
                    loadPendingPayments((DefaultTableModel) table.getModel());
                }
            }
        });
    }
    
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        this.table = table;
        this.selectedRow = row;
        this.label = (value == null) ? "" : value.toString();
        button.setText(label);
        isPushed = true;
        return button;
    }
    
    @Override
    public Object getCellEditorValue() {
        isPushed = false;
        return label;
    }
    
    @Override
    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }
}

private void authorizePayment(int paymentId) {
    String currentAdmin = Config.getCurrentUsername();
    String updateSql = "UPDATE Payments SET Payment_Status = 'Paid', Notes = Notes || ' (Authorized by: " + currentAdmin + ")' WHERE Payment_ID = ?";
    
    try (Connection conn = Config.connect();
         PreparedStatement pst = conn.prepareStatement(updateSql)) {
        
        pst.setInt(1, paymentId);
        int result = pst.executeUpdate();
        
        if (result > 0) {
            JOptionPane.showMessageDialog(this, "Payment authorized successfully!");
            loadMemberData(); // Refresh main table
        } else {
            JOptionPane.showMessageDialog(this, "Failed to authorize payment.");
        }
        
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error authorizing payment: " + e.getMessage());
    }
    }//GEN-LAST:event_btnAuthActionPerformed

	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnAuth;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnPayment;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnTransaction;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JTextField searchField;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JScrollPane tableScroll;
    private javax.swing.JTable tblEvents;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables
}