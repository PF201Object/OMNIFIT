package OMNIFIT;

import Config.Config;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

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
        btnReceipt.addActionListener(evt -> viewReceipt());
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
                             btnPayment, btnReceipt, btnSearch};
        
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

    public void loadMemberData(String searchTerm) {
        String[] columnNames = {"ID", "Name", "Contact", "Email", "Joined", "Status", "Type", "Payment Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        model.setRowCount(0);

        String sql = "SELECT m.M_ID, m.Name, m.Contact_No, m.Email, m.Join_date, " +
                     "m.Membership_Status, m.Membership_Type, " +
                     "COALESCE(p.Payment_Status, 'Pending') as Payment_Status " +
                     "FROM Members m " +
                     "LEFT JOIN Payments p ON m.M_ID = p.Member_ID";
        
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql += " WHERE m.Name LIKE ? OR m.Email LIKE ? OR m.Contact_No LIKE ?";
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
                        rs.getString("Membership_Status"),
                        rs.getString("Membership_Type"),
                        rs.getString("Payment_Status")
                    });
                }
            }
            tblEvents.setModel(model);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
        }
    }

    private void addMember() {
        // Create input dialog for new member
        JTextField nameField = new JTextField();
        JTextField contactField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField joinDateField = new JTextField("YYYY-MM-DD");
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Active", "Inactive", "Pending"});
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Regular", "Premium", "VIP"});

        Object[] message = {
            "Name:", nameField,
            "Contact:", contactField,
            "Email:", emailField,
            "Join Date (YYYY-MM-DD):", joinDateField,
            "Status:", statusBox,
            "Type:", typeBox
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add New Member", 
                                                   JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String sql = "INSERT INTO Members (Name, Contact_No, Email, Join_date, " +
                        "Membership_Status, Membership_Type) VALUES (?, ?, ?, ?, ?, ?)";
            
            try (Connection conn = Config.connect();
                 PreparedStatement pst = conn.prepareStatement(sql)) {
                
                pst.setString(1, nameField.getText());
                pst.setString(2, contactField.getText());
                pst.setString(3, emailField.getText());
                pst.setString(4, joinDateField.getText());
                pst.setString(5, statusBox.getSelectedItem().toString());
                pst.setString(6, typeBox.getSelectedItem().toString());
                
                pst.executeUpdate();
                loadMemberData();
                JOptionPane.showMessageDialog(this, "Member added successfully!");
                
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error adding member: " + e.getMessage());
            }
        }
    }

    private void updateMember() {
        int selectedRow = tblEvents.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a member to update");
            return;
        }

        int id = (int) tblEvents.getValueAt(selectedRow, 0);
        String currentName = (String) tblEvents.getValueAt(selectedRow, 1);
        String currentContact = (String) tblEvents.getValueAt(selectedRow, 2);
        String currentEmail = (String) tblEvents.getValueAt(selectedRow, 3);
        String currentStatus = (String) tblEvents.getValueAt(selectedRow, 5);
        String currentType = (String) tblEvents.getValueAt(selectedRow, 6);

        JTextField nameField = new JTextField(currentName);
        JTextField contactField = new JTextField(currentContact);
        JTextField emailField = new JTextField(currentEmail);
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Active", "Inactive", "Pending"});
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Regular", "Premium", "VIP"});
        
        statusBox.setSelectedItem(currentStatus);
        typeBox.setSelectedItem(currentType);

        Object[] message = {
            "Name:", nameField,
            "Contact:", contactField,
            "Email:", emailField,
            "Status:", statusBox,
            "Type:", typeBox
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Update Member", 
                                                   JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String sql = "UPDATE Members SET Name=?, Contact_No=?, Email=?, " +
                        "Membership_Status=?, Membership_Type=? WHERE M_ID=?";
            
            try (Connection conn = Config.connect();
                 PreparedStatement pst = conn.prepareStatement(sql)) {
                
                pst.setString(1, nameField.getText());
                pst.setString(2, contactField.getText());
                pst.setString(3, emailField.getText());
                pst.setString(4, statusBox.getSelectedItem().toString());
                pst.setString(5, typeBox.getSelectedItem().toString());
                pst.setInt(6, id);
                
                pst.executeUpdate();
                loadMemberData();
                JOptionPane.showMessageDialog(this, "Member updated successfully!");
                
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error updating member: " + e.getMessage());
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

    private void processPayment() {
        int selectedRow = tblEvents.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a member for payment");
            return;
        }

        int memberId = (int) tblEvents.getValueAt(selectedRow, 0);
        String memberName = (String) tblEvents.getValueAt(selectedRow, 1);
        String memberType = (String) tblEvents.getValueAt(selectedRow, 6);

        // Calculate amount based on membership type
        double amount = memberType.equals("Premium") ? 1500 : 
                       memberType.equals("VIP") ? 2500 : 1000;

        JTextField amountField = new JTextField(String.valueOf(amount));
        JComboBox<String> methodBox = new JComboBox<>(new String[]{"Cash", "Card", "Bank Transfer"});
        JTextField dateField = new JTextField(new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));

        Object[] message = {
            "Member: " + memberName,
            "Member ID: " + memberId,
            "Amount:", amountField,
            "Payment Method:", methodBox,
            "Payment Date:", dateField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Process Payment", 
                                                   JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String sql = "INSERT INTO Payments (Member_ID, Amount, Payment_Date, Payment_Method, Payment_Status) " +
                        "VALUES (?, ?, ?, ?, 'Paid')";
            
            try (Connection conn = Config.connect();
                 PreparedStatement pst = conn.prepareStatement(sql)) {
                
                pst.setInt(1, memberId);
                pst.setDouble(2, Double.parseDouble(amountField.getText()));
                pst.setString(3, dateField.getText());
                pst.setString(4, methodBox.getSelectedItem().toString());
                
                pst.executeUpdate();
                loadMemberData();
                JOptionPane.showMessageDialog(this, "Payment processed successfully!");
                
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error processing payment: " + e.getMessage());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid amount format");
            }
        }
    }

    private void viewReceipt() {
        int selectedRow = tblEvents.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a member to view receipt");
            return;
        }

        int memberId = (int) tblEvents.getValueAt(selectedRow, 0);
        String memberName = (String) tblEvents.getValueAt(selectedRow, 1);
        
        String sql = "SELECT * FROM Payments WHERE Member_ID = ? ORDER BY Payment_Date DESC LIMIT 1";
        
        try (Connection conn = Config.connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setInt(1, memberId);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                String receipt = "=== PAYMENT RECEIPT ===\n" +
                               "Member: " + memberName + "\n" +
                               "Amount: ₱" + rs.getDouble("Amount") + "\n" +
                               "Date: " + rs.getString("Payment_Date") + "\n" +
                               "Method: " + rs.getString("Payment_Method") + "\n" +
                               "Status: " + rs.getString("Payment_Status") + "\n" +
                               "======================";
                
                JTextArea textArea = new JTextArea(receipt);
                textArea.setEditable(false);
                textArea.setBackground(new Color(45, 45, 45));
                textArea.setForeground(Color.WHITE);
                textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                
                JOptionPane.showMessageDialog(this, new JScrollPane(textArea), 
                                              "Payment Receipt", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No payment found for this member");
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error retrieving receipt: " + e.getMessage());
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
        btnReceipt = new javax.swing.JButton();

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

        btnReceipt.setText("RECEIPT");
        buttonPanel.add(btnReceipt);

        add(buttonPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 320, 540, 40));
    }// </editor-fold>//GEN-END:initComponents

	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnPayment;
    private javax.swing.JButton btnReceipt;
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