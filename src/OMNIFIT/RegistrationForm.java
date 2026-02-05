package OMNIFIT;

import Config.Config;
import javax.swing.JOptionPane;

public class RegistrationForm extends javax.swing.JPanel {

    private final DashboardForm dashboard;

    public RegistrationForm(DashboardForm parent) {
        this.dashboard = parent;
        initComponents();
        customInit();
    }

    private void customInit() {
        this.setOpaque(false);
        
        // Aesthetic setup for buttons
        btnAdminReg.setFocusPainted(false);
        btnAdminReg.setBorderPainted(false);
        btnAdminReg.setContentAreaFilled(false);
        
        btnBack.setFocusPainted(false);
        btnBack.setBorderPainted(false);
        btnBack.setContentAreaFilled(false);
    }

private void handleRegistration(boolean isAdminRequest) {
        String username = txtName.getText().trim(); 
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());
        String contact = txtContact.getText().trim();
        
        String role = isAdminRequest ? "Administrator" : comboRole.getSelectedItem().toString();
        String status = "Active";
        double salary = 0.00;

        // Automated Salary Assignment
        switch (role) {
            case "Administrator": salary = 50000.00; break;
            case "GYM Manager": salary = 30000.00; break;
            case "Coach/Instructor": salary = 20000.00; break;
            case "Receptionist": salary = 15000.00; break;
            case "Staff": salary = 14000.00; break;
            default: salary = 0.00; break;
        }

        // --- VALIDATIONS ---
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || contact.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all required fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Database Checks
        if (Config.isUserExists(username)) {
            JOptionPane.showMessageDialog(this, "Username is already taken!", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!email.contains("@") || !email.contains(".")) {
        JOptionPane.showMessageDialog(this, "Please enter a valid email address (e.g., user@example.com)", "Invalid Email", JOptionPane.ERROR_MESSAGE);
        return;
        }
        
        if (contact.length() != 11 || !contact.matches("\\d+")) {
        JOptionPane.showMessageDialog(this, "Contact number must be exactly 11 digits (e.g., 09123456789)", "Invalid Contact", JOptionPane.ERROR_MESSAGE);
        return;
        }
        
        if (Config.isUserExists(username)) {
        JOptionPane.showMessageDialog(this, "Username is already taken!", "Error", JOptionPane.WARNING_MESSAGE);
        return;
        }
        
        if (Config.isEmailExists(email)) {
        JOptionPane.showMessageDialog(this, "Email is already registered!", "Duplicate Email", JOptionPane.WARNING_MESSAGE);
        return;
        }

        if (isAdminRequest) {
            String key = JOptionPane.showInputDialog(this, "Enter Admin Registration Key:", "Authentication", JOptionPane.WARNING_MESSAGE);
            if (!"Admin123".equals(key)) {
                JOptionPane.showMessageDialog(this, "Invalid Admin Key!", "Access Denied", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // --- GENERATE CUSTOM ID (OMNI-00-1001) ---
        String customID = generateNextOMNIId();

        // Update your Config.registerUser to accept 'customID' as the first parameter
        boolean success = Config.registerUser(customID, username, password, email, contact, role, status, salary);        
        if (success) {
            JOptionPane.showMessageDialog(this, role + " Registered Successfully!");
            dashboard.showLogin(); 
        } else {
            JOptionPane.showMessageDialog(this, "Registration Failed!", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Logic to generate the ID: OMNI-00-1001
     * Ideally, this should query the DB for the last ID, but here is the logic structure.
     */
    private String generateNextOMNIId() {
        int nextNumber = 1001; // Default start
        try {
            // You should have a method in Config that does: 
            // "SELECT User_ID FROM Users ORDER BY User_ID DESC LIMIT 1"
            String lastId = Config.getLastUserID(); 
            if (lastId != null && lastId.startsWith("OMNI-00-")) {
                String numericPart = lastId.substring(8); // Extract "1001"
                nextNumber = Integer.parseInt(numericPart) + 1;
            }
        } catch (Exception e) {
            System.err.println("Error generating ID: " + e.getMessage());
        }
        return "OMNI-00-" + nextNumber;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        jLabel4 = new javax.swing.JLabel();
        txtContact = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        comboRole = new javax.swing.JComboBox<>();
        btnAdminReg = new javax.swing.JButton();
        btnBack = new javax.swing.JButton();
        Register = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Username:");
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 60, 100, 30));
        add(txtName, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 60, 160, 25));

        jLabel2.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Email:");
        add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 100, 100, 30));
        add(txtEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 100, 160, 25));

        jLabel3.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Password:");
        add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 140, 100, 30));
        add(txtPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 140, 160, 25));

        jLabel4.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Contact:");
        add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 180, 100, 30));
        add(txtContact, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 180, 160, 25));

        jLabel6.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Role:");
        add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 220, 100, 30));

        comboRole.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "GYM Manager", "Coach/Instructor", "Receptionist", "Staff", "Kitchen Assistant", "Event Coordinator" }));
        comboRole.addActionListener(new java.awt.event.ActionListener() {           
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboRoleActionPerformed(evt);
            }
        });
        add(comboRole, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 220, 160, 25));

        btnAdminReg.setFont(new java.awt.Font("Times New Roman", 3, 12)); // NOI18N
        btnAdminReg.setForeground(new java.awt.Color(255, 255, 255));
        btnAdminReg.setText("Admin Registration");
        btnAdminReg.setContentAreaFilled(false);
        btnAdminReg.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAdminReg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdminRegActionPerformed(evt);
            }
        });
        add(btnAdminReg, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 310, 150, 20));

        btnBack.setFont(new java.awt.Font("Times New Roman", 3, 12)); // NOI18N
        btnBack.setForeground(new java.awt.Color(255, 255, 255));
        btnBack.setText("Back to Login");
        btnBack.setContentAreaFilled(false);
        btnBack.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });
        add(btnBack, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 310, 120, 20));

        Register.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        Register.setText("REGISTER");
        Register.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        Register.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        Register.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RegisterActionPerformed(evt);
            }
        });
        add(Register, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 260, 160, 40));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/log1.png"))); // NOI18N
        add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 680, 360));
    }// </editor-fold>//GEN-END:initComponents

    private void comboRoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboRoleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_comboRoleActionPerformed

    private void RegisterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RegisterActionPerformed
        handleRegistration(false);
    }//GEN-LAST:event_RegisterActionPerformed

    private void btnAdminRegActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdminRegActionPerformed
        handleRegistration(true);
    }//GEN-LAST:event_btnAdminRegActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        dashboard.showLogin(); 
    }//GEN-LAST:event_btnBackActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Register;
    private javax.swing.JButton btnAdminReg;
    private javax.swing.JButton btnBack;
    private javax.swing.JComboBox<String> comboRole;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JTextField txtContact;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtName;
    private javax.swing.JPasswordField txtPassword;
    // End of variables declaration//GEN-END:variables
}