package OMNIFIT;

import Config.Config;
import java.awt.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class User extends javax.swing.JPanel {

    public User() {
        initComponents(); 
        setOpaque(false);
        applyDashboardTheme(); 
        loadUserData();   
        setupTableClick();
    }

    private void applyDashboardTheme() {
        tableScroll.setBorder(new EmptyBorder(0, 0, 0, 0));
        tableScroll.setOpaque(false);
        tableScroll.getViewport().setOpaque(false);

        tblUsers.setRowHeight(35);
        tblUsers.setShowGrid(false);
        
        tblUsers.setBackground(new Color(30, 30, 30)); 
        tblUsers.setForeground(new Color(220, 220, 220)); 
        tblUsers.setSelectionBackground(new Color(255, 102, 0, 100));
        tblUsers.setSelectionForeground(Color.WHITE);
        
        tblUsers.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblUsers.getTableHeader().setBackground(new Color(25, 25, 25)); 
        tblUsers.getTableHeader().setForeground(new Color(180, 180, 180));
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setOpaque(true); 
        
        tblUsers.setDefaultRenderer(Object.class, centerRenderer);
        
        styleButtons();
    }
    
    private void styleButtons() {
        Color buttonBg = new Color(255, 102, 0);
        Color buttonFg = Color.WHITE;
        
        JButton[] buttons = {btnSearch, btnRefresh};
        
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

    private void setupTableClick() {
        tblUsers.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) { // Double click
                    showUserDetails();
                }
            }
        });
    }

    public void loadUserData() {
        loadUserData(null);
    }

    public void loadUserData(String searchTerm) {
        String[] columnNames = {"ID", "Username", "Email Address", "Contact #", "User Role", "Account Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        model.setRowCount(0);

        String sql = "SELECT U.U_ID, U.Username, U.Email, U.Contact_No, U.Gender, " +
                     "M.Role_Position, U.Members_Status, U.Profile_Pic " +
                     "FROM Users U " +
                     "LEFT JOIN Management M ON U.Username = M.Username";
        
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql += " WHERE U.Username LIKE ? OR U.Email LIKE ? OR U.Contact_No LIKE ? OR M.Role_Position LIKE ?";
        }

        try (Connection conn = Config.connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                String pattern = "%" + searchTerm + "%";
                pst.setString(1, pattern);
                pst.setString(2, pattern);
                pst.setString(3, pattern);
                pst.setString(4, pattern);
            }

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("U_ID"),
                        rs.getString("Username"),
                        rs.getString("Email"),
                        rs.getString("Contact_No") != null ? rs.getString("Contact_No") : "N/A",
                        rs.getString("Role_Position") != null ? rs.getString("Role_Position") : "No Role",
                        rs.getString("Members_Status")
                    });
                }
            }
            tblUsers.setModel(model);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "User Load Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showUserDetails() {
        int selectedRow = tblUsers.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user to view details");
            return;
        }

        String userId = (String) tblUsers.getValueAt(selectedRow, 0);
        String username = (String) tblUsers.getValueAt(selectedRow, 1);

        String sql = "SELECT U.*, M.Role_Position, M.WorkEmail, M.Salary_PayRate " +
                     "FROM Users U " +
                     "LEFT JOIN Management M ON U.Username = M.Username " +
                     "WHERE U.U_ID = ?";

        try (Connection conn = Config.connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, userId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                showUserDetailDialog(rs);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading user details: " + e.getMessage());
        }
    }

    private void showUserDetailDialog(ResultSet rs) throws SQLException {
        // Create custom dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "User Details", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);

        // Main panel with glass effect
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 30, 30, 240));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Profile Picture Panel
        JPanel picturePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        picturePanel.setOpaque(false);
        
        // Create profile picture display
        JLabel picLabel = new JLabel();
        picLabel.setPreferredSize(new Dimension(150, 150));
        picLabel.setBorder(BorderFactory.createLineBorder(new Color(255, 102, 0), 3));
        picLabel.setHorizontalAlignment(JLabel.CENTER);
        picLabel.setVerticalAlignment(JLabel.CENTER);
        
        String profilePic = rs.getString("Profile_Pic");
        if (profilePic != null && !profilePic.isEmpty()) {
            try {
                ImageIcon icon = new ImageIcon(profilePic);
                Image img = icon.getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH);
                picLabel.setIcon(new ImageIcon(img));
            } catch (Exception e) {
                picLabel.setText("No Image");
                picLabel.setForeground(Color.GRAY);
            }
        } else {
            // Create default avatar with initials
            String username = rs.getString("Username");
            String initials = username.substring(0, Math.min(2, username.length())).toUpperCase();
            picLabel.setText(initials);
            picLabel.setFont(new Font("Segoe UI", Font.BOLD, 40));
            picLabel.setForeground(new Color(255, 102, 0));
            picLabel.setBackground(new Color(45, 45, 45));
            picLabel.setOpaque(true);
        }
        
        picturePanel.add(picLabel);

        // Info Panel
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);

        // User Information
        addInfoRow(infoPanel, gbc, 0, "User ID:", rs.getString("U_ID"));
        addInfoRow(infoPanel, gbc, 1, "Username:", rs.getString("Username"));
        addInfoRow(infoPanel, gbc, 2, "Email:", rs.getString("Email"));
        addInfoRow(infoPanel, gbc, 3, "Contact Number:", rs.getString("Contact_No") != null ? rs.getString("Contact_No") : "N/A");
        addInfoRow(infoPanel, gbc, 4, "Gender:", rs.getString("Gender") != null ? rs.getString("Gender") : "Not Specified");
        addInfoRow(infoPanel, gbc, 5, "Account Status:", rs.getString("Members_Status"));
        addInfoRow(infoPanel, gbc, 6, "Role:", rs.getString("Role_Position") != null ? rs.getString("Role_Position") : "No Role");
        addInfoRow(infoPanel, gbc, 7, "Work Email:", rs.getString("WorkEmail") != null ? rs.getString("WorkEmail") : "N/A");
        
        double salary = rs.getDouble("Salary_PayRate");
        addInfoRow(infoPanel, gbc, 8, "Salary/Pay Rate:", salary > 0 ? "₱ " + String.format("%,.2f", salary) : "N/A");

        // Activity Panel (Recent Activity)
        JPanel activityPanel = new JPanel();
        activityPanel.setOpaque(false);
        activityPanel.setLayout(new BoxLayout(activityPanel, BoxLayout.Y_AXIS));
        activityPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(255, 102, 0)),
            " Recent Activity ",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            new Color(255, 102, 0)
        ));

        // Fetch recent transactions for this user
        String transactionSql = "SELECT * FROM Transactions WHERE Staff_ID = (SELECT Staffid FROM Management WHERE Username = ?) " +
                               "OR Member_ID = (SELECT M_ID FROM Members WHERE Email = ?) " +
                               "ORDER BY Transaction_Date DESC LIMIT 5";
        
        try (Connection conn = Config.connect();
             PreparedStatement pst = conn.prepareStatement(transactionSql)) {
            
            pst.setString(1, rs.getString("Username"));
            pst.setString(2, rs.getString("Email"));
            ResultSet transRs = pst.executeQuery();
            
            boolean hasActivity = false;
            while (transRs.next()) {
                hasActivity = true;
                JPanel transPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                transPanel.setOpaque(false);
                
                JLabel transLabel = new JLabel(String.format("%s: ₱%.2f - %s",
                    transRs.getString("Transaction_Type"),
                    transRs.getDouble("Amount"),
                    transRs.getString("Transaction_Date")));
                transLabel.setForeground(Color.WHITE);
                transLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                
                transPanel.add(transLabel);
                activityPanel.add(transPanel);
            }
            
            if (!hasActivity) {
                JLabel noActivity = new JLabel("No recent activity");
                noActivity.setForeground(Color.GRAY);
                noActivity.setFont(new Font("Segoe UI", Font.ITALIC, 11));
                activityPanel.add(noActivity);
            }
            
        } catch (SQLException e) {
            System.err.println("Error loading activity: " + e.getMessage());
        }

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);

        JButton closeBtn = new JButton("Close");
        closeBtn.setBackground(new Color(255, 102, 0));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        closeBtn.addActionListener(e -> dialog.dispose());
		
        buttonPanel.add(closeBtn);

        // Assemble dialog
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setOpaque(false);
        topSection.add(picturePanel, BorderLayout.NORTH);
        topSection.add(infoPanel, BorderLayout.CENTER);

        mainPanel.add(topSection, BorderLayout.NORTH);
        mainPanel.add(activityPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void addInfoRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        JLabel labelComp = new JLabel(label);
        labelComp.setForeground(new Color(255, 102, 0));
        labelComp.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(labelComp, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JLabel valueComp = new JLabel(value);
        valueComp.setForeground(Color.WHITE);
        valueComp.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(valueComp, gbc);
    }

    private void editUser(String userId) {
        // You can implement edit functionality here
        JOptionPane.showMessageDialog(this, "Edit user functionality coming soon!");
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
        tblUsers = new javax.swing.JTable();

        setOpaque(false);
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTitle.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText("USER RECORDS");
        add(lblTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 200, 30));

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

        add(topPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 20, 280, 40));

        tableScroll.setBorder(null);
        tableScroll.setOpaque(false);

        tblUsers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Username", "Email Address", "Contact #", "User Role", "Account Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tableScroll.setViewportView(tblUsers);

        add(tableScroll, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, 540, 250));
    }// </editor-fold>//GEN-END:initComponents
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSearch;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JTextField searchField;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JScrollPane tableScroll;
    private javax.swing.JTable tblUsers;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables
}