package OMNIFIT;

import Config.Config;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public final class Services extends javax.swing.JPanel {

    public Services() {
        initComponents();
        setOpaque(false);
        applyDashboardTheme(); 
        loadMenuData();
        
        btnAdd.addActionListener(evt -> addService());
        btnUpdate.addActionListener(evt -> updateService());
        btnDelete.addActionListener(evt -> deleteService());
    }

    private void applyDashboardTheme() {
        tableScroll.setBorder(new EmptyBorder(0, 0, 0, 0));
        tableScroll.setOpaque(false);
        tableScroll.getViewport().setOpaque(false);
        tableScroll.setViewportBorder(null);

        tblMenu.setRowHeight(35);
        tblMenu.setShowGrid(false);
        tblMenu.setIntercellSpacing(new Dimension(0, 0));
        
        tblMenu.setBackground(new Color(30, 30, 30)); 
        tblMenu.setForeground(new Color(220, 220, 220)); 
        tblMenu.setSelectionBackground(new Color(255, 102, 0, 100)); 
        tblMenu.setSelectionForeground(Color.WHITE);
        
        tblMenu.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblMenu.getTableHeader().setBackground(new Color(25, 25, 25)); 
        tblMenu.getTableHeader().setForeground(new Color(180, 180, 180));
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setOpaque(true); 
        tblMenu.setDefaultRenderer(Object.class, centerRenderer);
        
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

    public void loadMenuData() {
        loadMenuData(null);
    }

    public void loadMenuData(String searchTerm) {
        String[] columnNames = {"S_ID", "Service Name", "Type", "Fee", "Assigned Staff", "Member ID", "Payment Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        String sql = "SELECT s.S_ID, s.Service_Name, s.Service_Type, s.Payment_Status as Fee, " +
                     "s.Staff_Assigned, s.Member_ID, " +
                     "COALESCE(p.Payment_Status, 'Pending') as Payment_Status " +
                     "FROM Services s " +
                     "LEFT JOIN Payments p ON s.Member_ID = p.Member_ID";
        
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql += " WHERE s.Service_Name LIKE ? OR s.Staff_Assigned LIKE ?";
        }

        try (Connection conn = Config.connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                String pattern = "%" + searchTerm + "%";
                pst.setString(1, pattern);
                pst.setString(2, pattern);
            }

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("S_ID"),
                        rs.getString("Service_Name"),
                        rs.getString("Service_Type"),
                        "₱ " + String.format("%.2f", rs.getDouble("Fee")),
                        rs.getString("Staff_Assigned"),
                        rs.getInt("Member_ID"),
                        rs.getString("Payment_Status")
                    });
                }
            }
            tblMenu.setModel(model);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
        }
    }

    private void addService() {
        JTextField nameField = new JTextField();
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Personal Training", "Group Class", "Nutrition", "Recovery"});
        JTextField feeField = new JTextField();
        JTextField staffField = new JTextField();
        JTextField memberIdField = new JTextField();

        Object[] message = {
            "Service Name:", nameField,
            "Service Type:", typeBox,
            "Fee:", feeField,
            "Assigned Staff:", staffField,
            "Member ID:", memberIdField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add New Service", 
                                                   JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String sql = "INSERT INTO Services (Service_Name, Service_Type, Payment_Status, " +
                        "Staff_Assigned, Member_ID) VALUES (?, ?, ?, ?, ?)";
            
            try (Connection conn = Config.connect();
                 PreparedStatement pst = conn.prepareStatement(sql)) {
                
                pst.setString(1, nameField.getText());
                pst.setString(2, typeBox.getSelectedItem().toString());
                pst.setDouble(3, Double.parseDouble(feeField.getText()));
                pst.setString(4, staffField.getText());
                pst.setInt(5, Integer.parseInt(memberIdField.getText()));
                
                pst.executeUpdate();
                loadMenuData();
                JOptionPane.showMessageDialog(this, "Service added successfully!");
                
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error adding service: " + e.getMessage());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid number format");
            }
        }
    }

    private void updateService() {
        int selectedRow = tblMenu.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a service to update");
            return;
        }

        int id = (int) tblMenu.getValueAt(selectedRow, 0);
        String name = (String) tblMenu.getValueAt(selectedRow, 1);
        String type = (String) tblMenu.getValueAt(selectedRow, 2);
        String feeStr = ((String) tblMenu.getValueAt(selectedRow, 3)).replace("₱ ", "");
        String staff = (String) tblMenu.getValueAt(selectedRow, 4);
        int memberId = (int) tblMenu.getValueAt(selectedRow, 5);

        JTextField nameField = new JTextField(name);
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Personal Training", "Group Class", "Nutrition", "Recovery"});
        typeBox.setSelectedItem(type);
        JTextField feeField = new JTextField(feeStr);
        JTextField staffField = new JTextField(staff);
        JTextField memberIdField = new JTextField(String.valueOf(memberId));

        Object[] message = {
            "Service Name:", nameField,
            "Service Type:", typeBox,
            "Fee:", feeField,
            "Assigned Staff:", staffField,
            "Member ID:", memberIdField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Update Service", 
                                                   JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String sql = "UPDATE Services SET Service_Name=?, Service_Type=?, Payment_Status=?, " +
                        "Staff_Assigned=?, Member_ID=? WHERE S_ID=?";
            
            try (Connection conn = Config.connect();
                 PreparedStatement pst = conn.prepareStatement(sql)) {
                
                pst.setString(1, nameField.getText());
                pst.setString(2, typeBox.getSelectedItem().toString());
                pst.setDouble(3, Double.parseDouble(feeField.getText()));
                pst.setString(4, staffField.getText());
                pst.setInt(5, Integer.parseInt(memberIdField.getText()));
                pst.setInt(6, id);
                
                pst.executeUpdate();
                loadMenuData();
                JOptionPane.showMessageDialog(this, "Service updated successfully!");
                
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error updating service: " + e.getMessage());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid number format");
            }
        }
    }

    private void deleteService() {
        int selectedRow = tblMenu.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a service to delete");
            return;
        }

        int id = (int) tblMenu.getValueAt(selectedRow, 0);
        String name = (String) tblMenu.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete " + name + "?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM Services WHERE S_ID=?";
            
            try (Connection conn = Config.connect();
                 PreparedStatement pst = conn.prepareStatement(sql)) {
                
                pst.setInt(1, id);
                pst.executeUpdate();
                loadMenuData();
                JOptionPane.showMessageDialog(this, "Service deleted successfully!");
                
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting service: " + e.getMessage());
            }
        }
    }

    private void processServicePayment() {
        int selectedRow = tblMenu.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a service for payment");
            return;
        }

        int serviceId = (int) tblMenu.getValueAt(selectedRow, 0);
        int memberId = (int) tblMenu.getValueAt(selectedRow, 5);
        String serviceName = (String) tblMenu.getValueAt(selectedRow, 1);
        String feeStr = ((String) tblMenu.getValueAt(selectedRow, 3)).replace("₱ ", "");
        double amount = Double.parseDouble(feeStr);

        JTextField amountField = new JTextField(String.valueOf(amount));
        JComboBox<String> methodBox = new JComboBox<>(new String[]{"Cash", "Card", "Bank Transfer"});
        JTextField dateField = new JTextField(new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));

        Object[] message = {
            "Service: " + serviceName,
            "Member ID: " + memberId,
            "Amount:", amountField,
            "Payment Method:", methodBox,
            "Payment Date:", dateField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Process Service Payment", 
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
                
                // Update service payment status if needed
                String updateSql = "UPDATE Services SET Payment_Status = ? WHERE S_ID = ?";
                try (PreparedStatement updatePst = conn.prepareStatement(updateSql)) {
                    updatePst.setDouble(1, amount);
                    updatePst.setInt(2, serviceId);
                    updatePst.executeUpdate();
                }
                
                loadMenuData();
                JOptionPane.showMessageDialog(this, "Service payment processed successfully!");
                
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error processing payment: " + e.getMessage());
            }
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
        tblMenu = new javax.swing.JTable();
        buttonPanel = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();

        setOpaque(false);
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTitle.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText("GYM SERVICES");
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

        add(topPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 20, 260, 40));

        tableScroll.setBorder(null);
        tableScroll.setOpaque(false);

        tblMenu.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "S_ID", "Service Name", "Type", "Fee", "Assigned Staff", "Member ID", "Payment Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tableScroll.setViewportView(tblMenu);

        add(tableScroll, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, 540, 250));

        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 0));

        btnAdd.setText("ADD");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });
        buttonPanel.add(btnAdd);

        btnUpdate.setText("UPDATE");
        buttonPanel.add(btnUpdate);

        btnDelete.setText("DELETE");
        buttonPanel.add(btnDelete);

        add(buttonPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 320, 540, 30));
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnAddActionPerformed

	
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
    private javax.swing.JTable tblMenu;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables
}