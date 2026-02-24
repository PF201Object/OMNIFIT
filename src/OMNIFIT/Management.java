package OMNIFIT;

import Config.Config;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public final class Management extends javax.swing.JPanel {

    public Management() {
        initComponents();
        setOpaque(false);
        applyDashboardTheme(); 
        loadBookingData();
    }

    private void applyDashboardTheme() {
        tableScroll.setBorder(new EmptyBorder(0, 0, 0, 0));
        tableScroll.setOpaque(false);
        tableScroll.getViewport().setOpaque(false);
        tableScroll.setViewportBorder(null);

        tblBookings.setRowHeight(35);
        tblBookings.setShowGrid(false);
        tblBookings.setIntercellSpacing(new Dimension(0, 0));
        
        tblBookings.setBackground(new Color(30, 30, 30)); 
        tblBookings.setForeground(new Color(220, 220, 220)); 
        tblBookings.setSelectionBackground(new Color(255, 102, 0, 100));
        tblBookings.setSelectionForeground(Color.WHITE);
        
        tblBookings.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblBookings.getTableHeader().setBackground(new Color(25, 25, 25)); 
        tblBookings.getTableHeader().setForeground(new Color(180, 180, 180));
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setOpaque(true); 
        tblBookings.setDefaultRenderer(Object.class, centerRenderer);
        
        styleButtons();
    }
    
private void styleButtons() {
    Color buttonBg = new Color(255, 102, 0);
    Color buttonFg = Color.WHITE;
    
    JButton[] buttons = {btnUpdate, btnDelete, btnRefresh, btnPaycheck, btnSearch};
    
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

    // --- ADD THIS SECTION TO FIX THE BUTTONS ---
    btnUpdate.addActionListener(e -> updateStaff());
    btnDelete.addActionListener(e -> deleteStaff());
    btnRefresh.addActionListener(e -> loadBookingData());
    btnPaycheck.addActionListener(e -> processPaycheck());
    btnSearch.addActionListener(e -> loadBookingData(searchField.getText()));
    // --------------------------------------------
    
    searchField.setBackground(new Color(45, 45, 45));
    searchField.setForeground(Color.WHITE);
    searchField.setCaretColor(Color.WHITE);
    searchField.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(255, 102, 0)),
        BorderFactory.createEmptyBorder(5, 10, 5, 10)));
}

    public void loadBookingData() {
        loadBookingData(null);
    }

    public void loadBookingData(String searchTerm) {
        String[] columnNames = {"Staff ID", "Username", "Role", "Pay Rate", "Work Email", "Last Payment", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        model.setRowCount(0);

        String sql = "SELECT m.Staffid, m.Username, m.Role_Position, m.Salary_PayRate, m.WorkEmail, " +
                     "MAX(p.Pay_Date) as Last_Payment, " +
                     "CASE WHEN MAX(p.Payroll_ID) IS NOT NULL THEN 'Paid' ELSE 'Pending' END as Payment_Status " +
                     "FROM Management m " +
                     "LEFT JOIN Payroll p ON m.Staffid = p.Staff_ID " +
                     "GROUP BY m.Staffid, m.Username, m.Role_Position, m.Salary_PayRate, m.WorkEmail";
        
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql = "SELECT * FROM (" + sql + ") t WHERE Username LIKE ? OR Role LIKE ? OR WorkEmail LIKE ?";
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
                    String lastPayment = rs.getString("Last_Payment");
                    if (lastPayment == null) {
                        lastPayment = "No payments yet";
                    }
                    
                    model.addRow(new Object[]{
                        rs.getInt("Staffid"),
                        rs.getString("Username"),
                        rs.getString("Role_Position"),
                        "₱ " + String.format("%,.2f", rs.getDouble("Salary_PayRate")),
                        rs.getString("WorkEmail"),
                        lastPayment,
                        rs.getString("Payment_Status")
                    });
                }
            }
            tblBookings.setModel(model);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
        }
    }

    private void addStaff() {
        JTextField usernameField = new JTextField();
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"Trainer", "Manager", "Receptionist", "Cleaner", "Administrator"});
        JTextField payRateField = new JTextField();
        JTextField emailField = new JTextField();

        Object[] message = {
            "Username:", usernameField,
            "Role:", roleBox,
            "Pay Rate (Monthly):", payRateField,
            "Work Email:", emailField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add New Staff", 
                                                   JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String sql = "INSERT INTO Management (Username, Role_Position, Salary_PayRate, WorkEmail) " +
                        "VALUES (?, ?, ?, ?)";
            
            try (Connection conn = Config.connect();
                 PreparedStatement pst = conn.prepareStatement(sql)) {
                
                pst.setString(1, usernameField.getText());
                pst.setString(2, roleBox.getSelectedItem().toString());
                pst.setDouble(3, Double.parseDouble(payRateField.getText()));
                pst.setString(4, emailField.getText());
                
                pst.executeUpdate();
                loadBookingData();
                JOptionPane.showMessageDialog(this, "Staff added successfully!");
                
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error adding staff: " + e.getMessage());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid pay rate format");
            }
        }
    }

    private void updateStaff() {
        int selectedRow = tblBookings.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a staff member to update");
            return;
        }

        int id = (int) tblBookings.getValueAt(selectedRow, 0);
        String username = (String) tblBookings.getValueAt(selectedRow, 1);
        String role = (String) tblBookings.getValueAt(selectedRow, 2);
        String payRateStr = ((String) tblBookings.getValueAt(selectedRow, 3)).replace("₱ ", "").replace(",", "");
        String email = (String) tblBookings.getValueAt(selectedRow, 4);

        JTextField usernameField = new JTextField(username);
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"Trainer", "Manager", "Receptionist", "Cleaner", "Administrator"});
        roleBox.setSelectedItem(role);
        JTextField payRateField = new JTextField(payRateStr);
        JTextField emailField = new JTextField(email);

        Object[] message = {
            "Username:", usernameField,
            "Role:", roleBox,
            "Pay Rate (Monthly):", payRateField,
            "Work Email:", emailField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Update Staff", 
                                                   JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String sql = "UPDATE Management SET Username=?, Role_Position=?, Salary_PayRate=?, WorkEmail=? " +
                        "WHERE Staffid=?";
            
            try (Connection conn = Config.connect();
                 PreparedStatement pst = conn.prepareStatement(sql)) {
                
                pst.setString(1, usernameField.getText());
                pst.setString(2, roleBox.getSelectedItem().toString());
                pst.setDouble(3, Double.parseDouble(payRateField.getText()));
                pst.setString(4, emailField.getText());
                pst.setInt(5, id);
                
                pst.executeUpdate();
                loadBookingData();
                JOptionPane.showMessageDialog(this, "Staff updated successfully!");
                
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error updating staff: " + e.getMessage());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid pay rate format");
            }
        }
    }

    private void deleteStaff() {
        int selectedRow = tblBookings.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a staff member to delete");
            return;
        }

        int id = (int) tblBookings.getValueAt(selectedRow, 0);
        String username = (String) tblBookings.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete " + username + "?\nThis will also delete all associated payroll records.", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            // First delete payroll records (foreign key constraint)
            String deletePayroll = "DELETE FROM Payroll WHERE Staff_ID=?";
            String deleteStaff = "DELETE FROM Management WHERE Staffid=?";
            
            try (Connection conn = Config.connect()) {
                conn.setAutoCommit(false);
                
                try (PreparedStatement pst1 = conn.prepareStatement(deletePayroll)) {
                    pst1.setInt(1, id);
                    pst1.executeUpdate();
                }
                
                try (PreparedStatement pst2 = conn.prepareStatement(deleteStaff)) {
                    pst2.setInt(1, id);
                    pst2.executeUpdate();
                }
                
                conn.commit();
                loadBookingData();
                JOptionPane.showMessageDialog(this, "Staff deleted successfully!");
                
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting staff: " + e.getMessage());
            }
        }
    }

    private void processPaycheck() {
        int selectedRow = tblBookings.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a staff member for paycheck");
            return;
        }

        int staffId = (int) tblBookings.getValueAt(selectedRow, 0);
        String username = (String) tblBookings.getValueAt(selectedRow, 1);
        String role = (String) tblBookings.getValueAt(selectedRow, 2);
        String payRateStr = ((String) tblBookings.getValueAt(selectedRow, 3)).replace("₱ ", "").replace(",", "");
        double payRate = Double.parseDouble(payRateStr);

        // Calculate paycheck (monthly salary)
        double monthlySalary = payRate;
        double tax = monthlySalary * 0.10; // 10% tax
        double netPay = monthlySalary - tax;

        // Get current date for pay period
        java.util.Calendar cal = java.util.Calendar.getInstance();
        String payDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
        
        // Calculate pay period (first day of month to current date or last day of month)
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
        String periodStart = new java.text.SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
        
        cal.set(java.util.Calendar.DAY_OF_MONTH, cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH));
        String periodEnd = new java.text.SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());

        JTextField hoursField = new JTextField("160"); // Default monthly hours
        JTextField bonusField = new JTextField("0");
        JTextField deductionsField = new JTextField("0");
        JTextField dateField = new JTextField(payDate);
        JTextField startField = new JTextField(periodStart);
        JTextField endField = new JTextField(periodEnd);

        Object[] message = {
            "Staff: " + username + " (" + role + ")",
            "Base Monthly Rate: ₱" + String.format("%,.2f", monthlySalary),
            "Pay Period Start (YYYY-MM-DD):", startField,
            "Pay Period End (YYYY-MM-DD):", endField,
            "Hours Worked:", hoursField,
            "Bonus:", bonusField,
            "Deductions:", deductionsField,
            "Pay Date:", dateField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Process Paycheck", 
                                                   JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                int hours = Integer.parseInt(hoursField.getText());
                double bonus = Double.parseDouble(bonusField.getText());
                double deductions = Double.parseDouble(deductionsField.getText());
                
                // Calculate based on hours (hourly rate = monthlyRate / 160)
                double hourlyRate = monthlySalary / 160;
                double grossPay = hourlyRate * hours + bonus;
                double taxAmount = grossPay * 0.10;
                double finalNetPay = grossPay - taxAmount - deductions;

                // Insert into payroll table
                String sql = "INSERT INTO Payroll (Staff_ID, Pay_Date, Gross_Pay, Tax, Net_Pay, Bonus, " +
                            "Deductions, Hours_Worked, Status, Pay_Period_Start, Pay_Period_End) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'Paid', ?, ?)";
                
                try (Connection conn = Config.connect();
                     PreparedStatement pst = conn.prepareStatement(sql)) {
                    
                    pst.setInt(1, staffId);
                    pst.setString(2, dateField.getText());
                    pst.setDouble(3, grossPay);
                    pst.setDouble(4, taxAmount);
                    pst.setDouble(5, finalNetPay);
                    pst.setDouble(6, bonus);
                    pst.setDouble(7, deductions);
                    pst.setInt(8, hours);
                    pst.setString(9, startField.getText());
                    pst.setString(10, endField.getText());
                    
                    pst.executeUpdate();
                    
                    // Show paycheck stub
                    String paycheck = "╔════════════════════ PAYCHECK STUB ════════════════════╗\n" +
                                    "║ Staff: " + padRight(username, 40) + " ║\n" +
                                    "║ Role: " + padRight(role, 42) + " ║\n" +
                                    "║ Pay Period: " + padRight(startField.getText() + " to " + endField.getText(), 33) + " ║\n" +
                                    "║ Pay Date: " + padRight(dateField.getText(), 37) + " ║\n" +
                                    "╠══════════════════════════════════════════════════════╣\n" +
                                    "║ Hours Worked: " + padRight(String.valueOf(hours), 34) + " ║\n" +
                                    "║ Hourly Rate: ₱" + padRight(String.format("%,.2f", hourlyRate), 34) + " ║\n" +
                                    "║ Gross Pay: ₱" + padRight(String.format("%,.2f", grossPay), 36) + " ║\n" +
                                    "║ Bonus: ₱" + padRight(String.format("%,.2f", bonus), 41) + " ║\n" +
                                    "║ Deductions: ₱" + padRight(String.format("%,.2f", deductions), 36) + " ║\n" +
                                    "║ Tax (10%): ₱" + padRight(String.format("%,.2f", taxAmount), 37) + " ║\n" +
                                    "╠══════════════════════════════════════════════════════╣\n" +
                                    "║ NET PAY: ₱" + padRight(String.format("%,.2f", finalNetPay), 38) + " ║\n" +
                                    "╚══════════════════════════════════════════════════════╝";
                    
                    JTextArea textArea = new JTextArea(paycheck);
                    textArea.setEditable(false);
                    textArea.setBackground(new Color(45, 45, 45));
                    textArea.setForeground(Color.WHITE);
                    textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                    
                    JOptionPane.showMessageDialog(this, new JScrollPane(textArea), 
                                                  "Paycheck Generated", JOptionPane.INFORMATION_MESSAGE);
                    
                    loadBookingData(); // Refresh the table
                    
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error processing paycheck: " + e.getMessage());
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid number format");
            }
        }
    }
    
    private String padRight(String text, int length) {
        return String.format("%-" + length + "s", text);
    }

    private void viewPayHistory() {
        int selectedRow = tblBookings.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a staff member to view payment history");
            return;
        }

        int staffId = (int) tblBookings.getValueAt(selectedRow, 0);
        String username = (String) tblBookings.getValueAt(selectedRow, 1);

        String sql = "SELECT Payroll_ID, Pay_Date, Gross_Pay, Tax, Net_Pay, Bonus, " +
                    "Deductions, Hours_Worked, Status FROM Payroll " +
                    "WHERE Staff_ID = ? ORDER BY Pay_Date DESC";
        
        try (Connection conn = Config.connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setInt(1, staffId);
            ResultSet rs = pst.executeQuery();
            
            StringBuilder history = new StringBuilder();
            history.append("Payment History for ").append(username).append(":\n\n");
            history.append(String.format("%-12s %-12s %-12s %-12s %-12s\n", 
                "Date", "Gross Pay", "Tax", "Net Pay", "Status"));
            history.append("--------------------------------------------------------\n");
            
            boolean hasRecords = false;
            while (rs.next()) {
                hasRecords = true;
                history.append(String.format("%-12s ₱%-11.2f ₱%-11.2f ₱%-11.2f %-12s\n",
                    rs.getString("Pay_Date"),
                    rs.getDouble("Gross_Pay"),
                    rs.getDouble("Tax"),
                    rs.getDouble("Net_Pay"),
                    rs.getString("Status")));
            }
            
            if (!hasRecords) {
                history.append("No payment records found.");
            }
            
            JTextArea textArea = new JTextArea(history.toString());
            textArea.setEditable(false);
            textArea.setBackground(new Color(45, 45, 45));
            textArea.setForeground(Color.WHITE);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            
            JOptionPane.showMessageDialog(this, new JScrollPane(textArea), 
                                          "Payment History", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error retrieving payment history: " + e.getMessage());
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
        tblBookings = new javax.swing.JTable();
        buttonPanel = new javax.swing.JPanel();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnPaycheck = new javax.swing.JButton();

        setOpaque(false);
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTitle.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText("STAFF MANAGE");
        add(lblTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 200, 30));

        topPanel.setOpaque(false);
        topPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 0));

        searchPanel.setOpaque(false);
        searchPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        topPanel.add(searchPanel);

        searchField.setPreferredSize(new java.awt.Dimension(100, 30));
        topPanel.add(searchField);

        btnSearch.setText("Search");
        topPanel.add(btnSearch);

        btnRefresh.setText("Refresh");
        topPanel.add(btnRefresh);

        add(topPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 20, 300, 40));

        tableScroll.setBorder(null);
        tableScroll.setOpaque(false);

        tblBookings.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Staff ID", "Username", "Role", "Pay Rate", "Work Email", "Last Payment", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblBookings.setIntercellSpacing(new java.awt.Dimension(0, 0));
        tableScroll.setViewportView(tblBookings);

        add(tableScroll, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, 540, 250));

        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 0));

        btnUpdate.setText("UPDATE");
        buttonPanel.add(btnUpdate);

        btnDelete.setText("DELETE");
        buttonPanel.add(btnDelete);

        btnPaycheck.setText("PAYCHECK");
        buttonPanel.add(btnPaycheck);

        add(buttonPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 320, 540, 30));
    }// </editor-fold>//GEN-END:initComponents
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnPaycheck;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JTextField searchField;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JScrollPane tableScroll;
    private javax.swing.JTable tblBookings;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables
}