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
        btnSearch.addActionListener(e -> loadBookingData(searchField.getText()));
        searchField.addActionListener(e -> loadBookingData(searchField.getText()));
        btnRefresh.addActionListener(e -> {
            searchField.setText("");
            loadBookingData();
        });
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
    
    JButton[] buttons = {btnAdd, btnUpdate, btnDelete, btnRefresh, btnSearch};
    
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
    btnAdd.addActionListener(e -> addStaff());
    btnUpdate.addActionListener(e -> updateStaff());
    btnDelete.addActionListener(e -> deleteStaff());
    btnRefresh.addActionListener(e -> loadBookingData());
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
        sql = "SELECT * FROM (" + sql + ") t WHERE Username LIKE ? OR Role_Position LIKE ? OR WorkEmail LIKE ?";        }

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
    // Create custom dialog
    JDialog addDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "ADD NEW STAFF", true);
    addDialog.setLayout(new BorderLayout());
    addDialog.setSize(550, 700);
    addDialog.setLocationRelativeTo(this);
    addDialog.setUndecorated(true);
    
    // Main panel with gradient background
    JPanel mainPanel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp = new GradientPaint(0, 0, new Color(25, 25, 35), 0, getHeight(), new Color(15, 15, 25));
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            
            // Add gym pattern
            g2d.setColor(new Color(255, 102, 0, 30));
            for (int i = 0; i < 10; i++) {
                g2d.drawLine(0, i * 50, getWidth(), i * 50 + 25);
            }
            g2d.dispose();
        }
    };
    mainPanel.setLayout(new BorderLayout(15, 15));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
    // Header Panel
    JPanel headerPanel = new JPanel(new BorderLayout(10, 5));
    headerPanel.setOpaque(false);
    headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
    
    JLabel titleIcon = new JLabel("👥");
    titleIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
    titleIcon.setForeground(new Color(255, 102, 0));
    
    JLabel titleLabel = new JLabel("REGISTER NEW STAFF");
    titleLabel.setFont(new Font("Impact", Font.BOLD, 24));
    titleLabel.setForeground(new Color(255, 102, 0));
    
    JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
    titlePanel.setOpaque(false);
    titlePanel.add(titleIcon);
    titlePanel.add(titleLabel);
    headerPanel.add(titlePanel, BorderLayout.CENTER);
    
    JPanel separatorLine = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            GradientPaint gp = new GradientPaint(0, 0, new Color(255, 102, 0), getWidth(), 0, new Color(255, 102, 0, 50));
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), 2);
            g2d.dispose();
        }
    };
    separatorLine.setPreferredSize(new Dimension(400, 2));
    headerPanel.add(separatorLine, BorderLayout.SOUTH);
    
    // Form Panel with ScrollPane
    JPanel formContainer = new JPanel(new BorderLayout());
    formContainer.setOpaque(false);
    
    JPanel formPanel = new JPanel(new GridBagLayout());
    formPanel.setOpaque(false);
    formPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(8, 10, 8, 10);
    gbc.weightx = 1.0;
    
    int row = 0;
    
    // Username
    addFormField(formPanel, gbc, "👤 USERNAME", row++);
    JTextField usernameField = createStyledTextField();
    gbc.gridy = row++;
    formPanel.add(usernameField, gbc);
    
    // Role Selection
    addFormField(formPanel, gbc, "🎯 ROLE", row++);
    String[] roles = {"GYM Manager", "Coach/Instructor", "Receptionist", "Staff"};
    JComboBox<String> roleBox = new JComboBox<>(roles);
    roleBox.setBackground(new Color(45, 45, 45));
    roleBox.setForeground(Color.WHITE);
    roleBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    roleBox.setBorder(BorderFactory.createLineBorder(new Color(255, 102, 0, 100), 1));
    roleBox.setPreferredSize(new Dimension(350, 35));
    gbc.gridy = row++;
    formPanel.add(roleBox, gbc);
    
    // Pay Rate (Auto-calculated)
    addFormField(formPanel, gbc, "💰 SALARY RATE", row++);
    JTextField payRateField = new JTextField("₱ 30,000.00");
    payRateField.setEditable(false);
    payRateField.setBackground(new Color(45, 45, 45));
    payRateField.setForeground(new Color(255, 215, 0));
    payRateField.setFont(new Font("Segoe UI", Font.BOLD, 14));
    payRateField.setHorizontalAlignment(JTextField.CENTER);
    payRateField.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(255, 102, 0, 100), 1),
        BorderFactory.createEmptyBorder(8, 10, 8, 10)));
    gbc.gridy = row++;
    formPanel.add(payRateField, gbc);
    
    // Update salary based on role
    roleBox.addActionListener(e -> {
        String selected = (String) roleBox.getSelectedItem();
        double salary = 0;
        switch (selected) {
            case "GYM Manager": salary = 30000; break;
            case "Coach/Instructor": salary = 25000; break;
            case "Receptionist": salary = 15000; break;
            case "Staff": salary = 10000; break;
        }
        payRateField.setText(String.format("₱ %, .2f", salary));
    });
    
    // Work Email
    addFormField(formPanel, gbc, "📧 WORK EMAIL", row++);
    JTextField emailField = createStyledTextField();
    gbc.gridy = row++;
    formPanel.add(emailField, gbc);
    
    // Contact Number
    addFormField(formPanel, gbc, "📞 CONTACT NUMBER", row++);
    JTextField contactField = createStyledTextField();
    gbc.gridy = row++;
    formPanel.add(contactField, gbc);
    
    // Password
    addFormField(formPanel, gbc, "🔒 PASSWORD", row++);
    JPasswordField passwordField = new JPasswordField();
    stylePasswordField(passwordField);
    gbc.gridy = row++;
    formPanel.add(passwordField, gbc);
    
    // Confirm Password
    addFormField(formPanel, gbc, "✓ CONFIRM PASSWORD", row++);
    JPasswordField confirmField = new JPasswordField();
    stylePasswordField(confirmField);
    gbc.gridy = row++;
    formPanel.add(confirmField, gbc);
    
    // Add a spacer at the bottom
    gbc.gridy = row++;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weighty = 1.0;
    formPanel.add(Box.createVerticalGlue(), gbc);
    
    // Create ScrollPane
    JScrollPane scrollPane = new JScrollPane(formPanel);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    scrollPane.getViewport().setOpaque(false);
    scrollPane.setOpaque(false);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    
    // Style scrollbar
    scrollPane.getVerticalScrollBar().setBackground(new Color(30, 30, 30));
    scrollPane.getVerticalScrollBar().setForeground(new Color(255, 102, 0));
    
    formContainer.add(scrollPane, BorderLayout.CENTER);
    
    // Button Panel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
    buttonPanel.setOpaque(false);
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
    
    JButton btnSave = createGymButton("REGISTER STAFF", new Color(255, 102, 0));
    JButton btnCancel = createGymButton("CANCEL", new Color(100, 100, 100));
    
    buttonPanel.add(btnSave);
    buttonPanel.add(btnCancel);
    
    mainPanel.add(headerPanel, BorderLayout.NORTH);
    mainPanel.add(formContainer, BorderLayout.CENTER);
    mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    
    // Add shadow effect
    JPanel shadowPanel = new JPanel(new BorderLayout());
    shadowPanel.setBackground(new Color(0, 0, 0, 80));
    shadowPanel.setBorder(BorderFactory.createLineBorder(new Color(255, 102, 0, 150), 2));
    shadowPanel.add(mainPanel, BorderLayout.CENTER);
    
    addDialog.add(shadowPanel);
    
    btnSave.addActionListener(e -> {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String contact = contactField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmField.getPassword());
        String role = (String) roleBox.getSelectedItem();
        double salary = Double.parseDouble(payRateField.getText().replace("₱", "").replace(",", "").trim());
        
        // Validation
        if (username.isEmpty()) {
            showValidationError("Username is required!");
            return;
        }
        if (username.length() < 3) {
            showValidationError("Username must be at least 3 characters!");
            return;
        }
        if (email.isEmpty()) {
            showValidationError("Email is required!");
            return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            showValidationError("Please enter a valid email address!");
            return;
        }
        if (contact.isEmpty()) {
            showValidationError("Contact number is required!");
            return;
        }
        if (!contact.matches("^\\d{11}$")) {
            showValidationError("Contact number must be 11 digits!");
            return;
        }
        if (password.isEmpty()) {
            showValidationError("Password is required!");
            return;
        }
        if (password.length() < 6) {
            showValidationError("Password must be at least 6 characters!");
            return;
        }
        if (!password.equals(confirm)) {
            showValidationError("Passwords do not match!");
            return;
        }
        
        // Check if username exists
        if (Config.isUserExists(username)) {
            showValidationError("Username already exists!");
            return;
        }
        if (Config.isEmailExists(email)) {
            showValidationError("Email already registered!");
            return;
        }
        
        // Generate Staff ID
        String staffId = "STAFF-" + System.currentTimeMillis();
        
        // Register user
        boolean success = Config.registerUser(staffId, username, password, email, contact, "Staff", role, "Active", salary);
        
        if (success) {
            JOptionPane.showMessageDialog(addDialog, 
                "✅ STAFF REGISTERED SUCCESSFULLY!\n\nID: " + staffId + "\nUsername: " + username + "\nRole: " + role, 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            addDialog.dispose();
            loadBookingData();
        } else {
            showValidationError("Registration failed! Please try again.");
        }
    });
    
    btnCancel.addActionListener(e -> addDialog.dispose());
    
    addDialog.setVisible(true);
}

private void updateStaff() {
    int selectedRow = tblBookings.getSelectedRow();
    if (selectedRow < 0) {
        JOptionPane.showMessageDialog(this, "Please select a staff member to update");
        return;
    }
    
    int id = (int) tblBookings.getValueAt(selectedRow, 0);
    String currentUsername = (String) tblBookings.getValueAt(selectedRow, 1);
    String currentEmail = (String) tblBookings.getValueAt(selectedRow, 4);
    String currentRole = (String) tblBookings.getValueAt(selectedRow, 2);
    
    // Create custom dialog
    JDialog updateDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "UPDATE STAFF", true);
    updateDialog.setLayout(new BorderLayout());
    updateDialog.setSize(550, 650);
    updateDialog.setLocationRelativeTo(this);
    updateDialog.setUndecorated(true);
    
    // Main panel with gradient background
    JPanel mainPanel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp = new GradientPaint(0, 0, new Color(25, 25, 35), 0, getHeight(), new Color(15, 15, 25));
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.setColor(new Color(255, 102, 0, 30));
            for (int i = 0; i < 10; i++) {
                g2d.drawLine(0, i * 50, getWidth(), i * 50 + 25);
            }
            g2d.dispose();
        }
    };
    mainPanel.setLayout(new BorderLayout(15, 15));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
    // Header Panel
    JPanel headerPanel = new JPanel(new BorderLayout(10, 5));
    headerPanel.setOpaque(false);
    headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
    
    JLabel titleIcon = new JLabel("✏️");
    titleIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
    titleIcon.setForeground(new Color(255, 102, 0));
    
    JLabel titleLabel = new JLabel("UPDATE STAFF PROFILE");
    titleLabel.setFont(new Font("Impact", Font.BOLD, 24));
    titleLabel.setForeground(new Color(255, 102, 0));
    
    JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
    titlePanel.setOpaque(false);
    titlePanel.add(titleIcon);
    titlePanel.add(titleLabel);
    headerPanel.add(titlePanel, BorderLayout.CENTER);
    
    JPanel separatorLine = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            GradientPaint gp = new GradientPaint(0, 0, new Color(255, 102, 0), getWidth(), 0, new Color(255, 102, 0, 50));
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), 2);
            g2d.dispose();
        }
    };
    separatorLine.setPreferredSize(new Dimension(400, 2));
    headerPanel.add(separatorLine, BorderLayout.SOUTH);
    
    // Form Panel with ScrollPane
    JPanel formContainer = new JPanel(new BorderLayout());
    formContainer.setOpaque(false);
    
    JPanel formPanel = new JPanel(new GridBagLayout());
    formPanel.setOpaque(false);
    formPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(8, 10, 8, 10);
    gbc.weightx = 1.0;
    
    int row = 0;
    
    // Username (Display only, not editable)
    addFormField(formPanel, gbc, "👤 USERNAME", row++);
    JTextField usernameField = createStyledTextField();
    usernameField.setText(currentUsername);
    usernameField.setEditable(false);
    usernameField.setBackground(new Color(35, 35, 45));
    gbc.gridy = row++;
    formPanel.add(usernameField, gbc);
    
    // Role (Display only)
    addFormField(formPanel, gbc, "🎯 ROLE", row++);
    JTextField roleField = createStyledTextField();
    roleField.setText(currentRole);
    roleField.setEditable(false);
    roleField.setBackground(new Color(35, 35, 45));
    gbc.gridy = row++;
    formPanel.add(roleField, gbc);
    
    // Work Email (Editable)
    addFormField(formPanel, gbc, "📧 WORK EMAIL", row++);
    JTextField emailField = createStyledTextField();
    emailField.setText(currentEmail);
    gbc.gridy = row++;
    formPanel.add(emailField, gbc);
    
    // Contact Number (Editable)
    addFormField(formPanel, gbc, "📞 CONTACT NUMBER", row++);
    JTextField contactField = createStyledTextField();
    try (Connection conn = Config.connect();
         PreparedStatement pst = conn.prepareStatement("SELECT Contact_No FROM Users WHERE Username = ?")) {
        pst.setString(1, currentUsername);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            contactField.setText(rs.getString("Contact_No"));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    gbc.gridy = row++;
    formPanel.add(contactField, gbc);
    
    // Password (Optional)
    addFormField(formPanel, gbc, "🔒 NEW PASSWORD (Optional)", row++);
    JPasswordField passwordField = new JPasswordField();
    stylePasswordField(passwordField);
    passwordField.setToolTipText("Leave blank to keep current password");
    gbc.gridy = row++;
    formPanel.add(passwordField, gbc);
    
    // Confirm Password
    addFormField(formPanel, gbc, "✓ CONFIRM PASSWORD", row++);
    JPasswordField confirmField = new JPasswordField();
    stylePasswordField(confirmField);
    gbc.gridy = row++;
    formPanel.add(confirmField, gbc);
    
    // Info note
    JLabel infoNote = new JLabel("Note: Password can be changed only if you enter a new one");
    infoNote.setFont(new Font("Segoe UI", Font.ITALIC, 10));
    infoNote.setForeground(new Color(255, 165, 0));
    infoNote.setHorizontalAlignment(SwingConstants.CENTER);
    gbc.gridy = row++;
    gbc.insets = new Insets(5, 10, 10, 10);
    formPanel.add(infoNote, gbc);
    
    // Add spacer
    gbc.gridy = row++;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weighty = 1.0;
    formPanel.add(Box.createVerticalGlue(), gbc);
    
    // Create ScrollPane
    JScrollPane scrollPane = new JScrollPane(formPanel);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    scrollPane.getViewport().setOpaque(false);
    scrollPane.setOpaque(false);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    
    scrollPane.getVerticalScrollBar().setBackground(new Color(30, 30, 30));
    scrollPane.getVerticalScrollBar().setForeground(new Color(255, 102, 0));
    
    formContainer.add(scrollPane, BorderLayout.CENTER);
    
    // Button Panel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
    buttonPanel.setOpaque(false);
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
    
    JButton btnUpdate = createGymButton("UPDATE STAFF", new Color(255, 102, 0));
    JButton btnCancel = createGymButton("CANCEL", new Color(100, 100, 100));
    
    buttonPanel.add(btnUpdate);
    buttonPanel.add(btnCancel);
    
    mainPanel.add(headerPanel, BorderLayout.NORTH);
    mainPanel.add(formContainer, BorderLayout.CENTER);
    mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    
    JPanel shadowPanel = new JPanel(new BorderLayout());
    shadowPanel.setBackground(new Color(0, 0, 0, 80));
    shadowPanel.setBorder(BorderFactory.createLineBorder(new Color(255, 102, 0, 150), 2));
    shadowPanel.add(mainPanel, BorderLayout.CENTER);
    
    updateDialog.add(shadowPanel);
    
    btnUpdate.addActionListener(e -> {
        String email = emailField.getText().trim();
        String contact = contactField.getText().trim();
        String newPassword = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmField.getPassword());
        
        // Validation
        if (email.isEmpty()) {
            showValidationError("Email is required!");
            return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            showValidationError("Please enter a valid email address!");
            return;
        }
        if (contact.isEmpty()) {
            showValidationError("Contact number is required!");
            return;
        }
        if (!contact.matches("^\\d{11}$")) {
            showValidationError("Contact number must be 11 digits!");
            return;
        }
        
        // Check if password is being updated
        boolean updatePassword = !newPassword.isEmpty();
        if (updatePassword) {
            if (newPassword.length() < 6) {
                showValidationError("Password must be at least 6 characters!");
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                showValidationError("Passwords do not match!");
                return;
            }
        }
        
        try (Connection conn = Config.connect()) {
            conn.setAutoCommit(false);
            
            // Update Users table
            String userSql = "UPDATE Users SET Email = ?, Contact_No = ?" + (updatePassword ? ", Password = ?" : "") + " WHERE Username = ?";
            
            try (PreparedStatement pst = conn.prepareStatement(userSql)) {
                int paramIndex = 1;
                pst.setString(paramIndex++, email);
                pst.setString(paramIndex++, contact);
                if (updatePassword) {
                    String hashedPassword = Config.hashPassword(newPassword);
                    pst.setString(paramIndex++, hashedPassword);
                }
                pst.setString(paramIndex, currentUsername);
                pst.executeUpdate();
            }
            
            // Update Management table
            String mgmtSql = "UPDATE Management SET WorkEmail = ? WHERE Username = ?";
            try (PreparedStatement pst = conn.prepareStatement(mgmtSql)) {
                pst.setString(1, email);
                pst.setString(2, currentUsername);
                pst.executeUpdate();
            }
            
            conn.commit();
            
            JOptionPane.showMessageDialog(updateDialog, 
                "STAFF UPDATED SUCCESSFULLY!\n\nUsername: " + currentUsername + "\nEmail: " + email, 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            updateDialog.dispose();
            loadBookingData();
            
        } catch (SQLException ex) {
            try (Connection conn = Config.connect()) {
                conn.rollback();
            } catch (SQLException rollbackEx) {}
            showValidationError("Update failed: " + ex.getMessage());
        }
    });
    
    btnCancel.addActionListener(e -> updateDialog.dispose());
    
    updateDialog.setVisible(true);
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
    
    private void addFormField(JPanel panel, GridBagConstraints gbc, String label, int row) {
    JLabel lblIcon = new JLabel(label.split(" ")[0]);
    lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
    JLabel lblText = new JLabel(label.substring(2));
    lblText.setFont(new Font("Segoe UI", Font.BOLD, 13));
    lblText.setForeground(new Color(255, 102, 0));
    
    JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
    labelPanel.setOpaque(false);
    labelPanel.add(lblIcon);
    labelPanel.add(lblText);
    
    gbc.gridx = 0;
    gbc.gridy = row;
    gbc.gridwidth = 2;
    panel.add(labelPanel, gbc);
    gbc.gridwidth = 1;
}

private JTextField createStyledTextField() {
    JTextField field = new JTextField();
    field.setBackground(new Color(45, 45, 45));
    field.setForeground(Color.WHITE);
    field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    field.setCaretColor(Color.WHITE);
    field.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(255, 102, 0, 100), 1),
        BorderFactory.createEmptyBorder(8, 10, 8, 10)));
    return field;
}

private void stylePasswordField(JPasswordField field) {
    field.setBackground(new Color(45, 45, 45));
    field.setForeground(Color.WHITE);
    field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    field.setCaretColor(Color.WHITE);
    field.setEchoChar('•');
    field.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(255, 102, 0, 100), 1),
        BorderFactory.createEmptyBorder(8, 10, 8, 10)));
}

private JButton createGymButton(String text, Color bgColor) {
    JButton button = new JButton(text);
    button.setBackground(bgColor);
    button.setForeground(Color.WHITE);
    button.setFont(new Font("Segoe UI", Font.BOLD, 13));
    button.setFocusPainted(false);
    button.setBorderPainted(false);
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    button.setPreferredSize(new Dimension(160, 40));
    return button;
}

private void showValidationError(String message) {
    JOptionPane.showMessageDialog(this, 
        "⚠️ " + message, 
        "Validation Error", 
        JOptionPane.WARNING_MESSAGE);
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
        btnAdd = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();

        setOpaque(false);
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTitle.setFont(new java.awt.Font("Impact", 1, 24)); // NOI18N
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
    private javax.swing.JTable tblBookings;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables
}