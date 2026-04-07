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
                             btnSearch};
        
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

        add(tableScroll, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, 540, 230));

        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 0));

        btnAdd.setText("ADD");
        buttonPanel.add(btnAdd);

        btnUpdate.setText("UPDATE");
        buttonPanel.add(btnUpdate);

        btnDelete.setText("DELETE");
        buttonPanel.add(btnDelete);

        add(buttonPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 300, 540, 30));
    }// </editor-fold>//GEN-END:initComponents

	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSearch;
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